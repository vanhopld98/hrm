package vn.com.humanresourcesmanagement.business.authentication;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.redis.TokenService;
import vn.com.humanresourcesmanagement.common.utils.StringUtils;
import vn.com.humanresourcesmanagement.configuration.properties.KeycloakProperties;
import vn.com.humanresourcesmanagement.service.UserProfileService;

@Component
@RequiredArgsConstructor
public class LogoutBusiness {

    private final UserProfileService userProfileService;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;
    private final TokenService tokenService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutBusiness.class);

    public void logout(String username) {
        LOGGER.info("[AUTHENTICATION][{}][LOGOUT] Starting.....", username);

        if (StringUtils.isNullOrEmpty(username)) {
            return;
        }

        var userProfile = userProfileService.findUserProfileByUsername(username);
        LOGGER.info("[AUTHENTICATION][{}][LOGOUT][User Profile: {}]", username, userProfile);

        var instance = keycloakProperties.getKeycloakByClient();
        var usersResource = instance.realm(keycloakSpringBootProperties.getRealm()).users().get(userProfile.getKeycloakId());
        var userSessions = usersResource.getUserSessions();

        if (!userSessions.isEmpty()) {
            usersResource.logout();
        }

        /* Xoá key ở redis */
        tokenService.remove(username);

        LOGGER.info("[AUTHENTICATION][{}][LOGOUT] Logout Success", username);
    }
}
