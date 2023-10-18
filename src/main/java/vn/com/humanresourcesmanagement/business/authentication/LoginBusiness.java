package vn.com.humanresourcesmanagement.business.authentication;

import com.auth0.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.exception.BusinessException;
import vn.com.humanresourcesmanagement.common.mapper.AuthenticationMapper;
import vn.com.humanresourcesmanagement.common.model.payload.request.LoginRequest;
import vn.com.humanresourcesmanagement.common.model.payload.response.AccessTokenResponse;
import vn.com.humanresourcesmanagement.common.model.payload.response.RolesUserResponse;
import vn.com.humanresourcesmanagement.common.redis.TokenService;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.common.utils.StringUtils;
import vn.com.humanresourcesmanagement.configuration.properties.KeycloakProperties;
import vn.com.humanresourcesmanagement.service.UserProfileService;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoginBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginBusiness.class);
    private final UserProfileService userProfileService;
    private final LogoutBusiness logoutBusiness;
    private final KeycloakProperties keycloakProperties;
    private final AuthenticationMapper authenticationMapper;
    private final TokenService tokenService;

    public AccessTokenResponse login(LoginRequest request) {

        /* Validate username password request */
        if (StringUtils.isNullOrEmpty(request.getUsername()) || StringUtils.isNullOrEmpty(request.getPassword())) {
            throw new BusinessException("Tài khoản hoặc mật khẩu không được để trống");
        }

        var username = request.getUsername();

        /* Tìm kiếm trong DB xem có tồn tại username này không */
        var userProfile = userProfileService.findUserProfileByUsername(username);
        LOGGER.info("[AUTHENTICATION][{}][LOGIN] User Profile: {}", username, userProfile);

        if (Objects.isNull(userProfile)) {
            throw new BusinessException("Username không tồn tại");
        }

        /* Logout tài khoản hiện tại */
        logoutBusiness.logout(username);

        var keyCloak = keycloakProperties.getKeycloak(username, request.getPassword());

        var accessTokenKeycloak = keyCloak.tokenManager().getAccessToken();

        var jwtDecode = JWT.decode(accessTokenKeycloak.getToken());
        var roles = jwtDecode.getClaim("realm_access").as(RolesUserResponse.class).getRoles().stream().filter(role -> role.startsWith("ROLE")).collect(Collectors.toList());

        LOGGER.info("[AUTHENTICATION][{}][LOGIN] Login Success", username);

        var expiredTimeToken = JWTUtils.getExpiredTime(accessTokenKeycloak.getToken());

        /* Lưu token vào redis theo thời gian hết hạn token */
        tokenService.set(username, accessTokenKeycloak.getToken(), expiredTimeToken);

        return authenticationMapper.mapToAccessTokenResponse(accessTokenKeycloak, roles);
    }

}
