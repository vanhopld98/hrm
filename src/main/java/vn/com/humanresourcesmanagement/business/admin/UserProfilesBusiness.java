package vn.com.humanresourcesmanagement.business.admin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.mapper.UserProfileMapper;
import vn.com.humanresourcesmanagement.common.model.payload.response.RolesUserResponse;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfileResponse;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfilesResponse;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.configuration.properties.KeycloakProperties;
import vn.com.humanresourcesmanagement.data.entity.UserProfile;
import vn.com.humanresourcesmanagement.data.repository.UserProfileRepository;
import vn.com.humanresourcesmanagement.service.KeycloakService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserProfilesBusiness {

    private final UserProfileRepository repository;
    private final UserProfileMapper userProfileMapper;
    private final KeycloakService keycloakService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfilesBusiness.class);

    public UserProfilesResponse process(int page, int size) {
        var username = JWTUtils.getUsername();
        LOGGER.info("[ADMIN][USERS][{}] Starting get list user profile", username);
        List<UserProfile> userProfiles = repository.findAll();
        List<UserProfileResponse> userProfileResponses = new ArrayList<>();
        for (var profile : userProfiles) {
            UserProfileResponse userProfile = userProfileMapper.convertToUserProfileResponse(profile);
            userProfile.setRoles(keycloakService.getRolesByKeycloakId(userProfile.getKeycloakId()));
            userProfileResponses.add(userProfile);
        }

        var totalPage = (int) Math.ceil((double) userProfiles.size() / size);

        if (totalPage == 0) {
            totalPage = 1;
        }
        LOGGER.info("[ADMIN][USERS][{}] Get list user profile success", username);

        return UserProfilesResponse
                .builder()
                .totalPage(totalPage)
                .totalRecord(userProfiles.size())
                .userProfileResponses(userProfileResponses.stream().skip((long) page * size).limit(size).collect(Collectors.toList()))
                .build();
    }

}
