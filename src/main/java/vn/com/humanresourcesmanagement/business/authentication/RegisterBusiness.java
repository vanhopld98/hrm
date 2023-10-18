package vn.com.humanresourcesmanagement.business.authentication;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.constants.Constant;
import vn.com.humanresourcesmanagement.common.enums.OTPTypeEnum;
import vn.com.humanresourcesmanagement.common.exception.BusinessException;
import vn.com.humanresourcesmanagement.common.model.payload.request.LoginRequest;
import vn.com.humanresourcesmanagement.common.model.payload.request.RegisterRequest;
import vn.com.humanresourcesmanagement.common.model.payload.response.AccessTokenResponse;
import vn.com.humanresourcesmanagement.common.utils.PasswordUtils;
import vn.com.humanresourcesmanagement.configuration.properties.KeycloakProperties;
import vn.com.humanresourcesmanagement.data.entity.UserProfile;
import vn.com.humanresourcesmanagement.data.repository.UserProfileOTPRepository;
import vn.com.humanresourcesmanagement.data.repository.UserProfileRepository;
import vn.com.humanresourcesmanagement.service.UserProfileService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RegisterBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterBusiness.class);

    private static final int TOTAL_FALSE_OTP = 5;

    private final UserProfileService userProfileService;
    private final UserProfileOTPRepository userProfileOTPRepository;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;
    private final UserProfileRepository userProfileRepository;
    private final LoginBusiness loginBusiness;

    public AccessTokenResponse register(RegisterRequest request) {

        var username = request.getUsername();

        /* Tìm kiếm trong DB xem username này đã tồn tại hay chưa */
        var userProfile = userProfileService.findUserProfileByUsername(username);
        if (Objects.nonNull(userProfile)) {
            throw new BusinessException(Constant.USERNAME_EXISTS);
        }

        /* Kiểm tra xem có đúng là người nhận được otp tạo tài khoản hay không. */
//        var userProfileOTP = userProfileOTPRepository.getLatestOTP(username, OTPTypeEnum.REGISTER.name());
//        LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_PROFILE_OTP][{}]", username, userProfileOTP);

//        if (Objects.isNull(userProfileOTP)) {
//            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
//        }
//
//        if (Objects.isNull(userProfileOTP.getLastVerifyAt())) {
//            throw new BusinessException(Constant.OTP_VERIFY_NULL);
//        }
//
//        int countVerifyFail = Objects.isNull(userProfileOTP.getCountVerifyFalse()) ? 0 : userProfileOTP.getCountVerifyFalse();
//        LOGGER.info("[AUTHENTICATION][{}][REGISTER][COUNT_VERIFY_FAIL][{}]", username, countVerifyFail);
//
//        if (Boolean.FALSE.equals(userProfileOTP.getStatus()) && countVerifyFail >= TOTAL_FALSE_OTP) {
//            throw new BusinessException(Constant.VERIFY_OTP_5TH);
//        }
//
//        /* Kiểm tra xem có trùng otp hay không */
//        if (StringUtils.notEquals(userProfileOTP.getOtp(), request.getOtp())) {
//            userProfileOTP.setCountVerifyFalse(countVerifyFail + 1);
//            userProfileOTP.setLastVerifyAt(LocalDateTime.now());
//            userProfileOTPRepository.save(userProfileOTP);
//            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
//        }

        var userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(request.getUsername());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setEmailVerified(true);
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_REPRESENTATION][{}]", username, userRepresentation);

        var role = new RoleRepresentation();
        role.setId("9feab2bf-96e7-4a35-a0a4-5e4d2cc56d84");

        /* Lấy keycloak */
        var keycloakInstance = keycloakProperties.getKeycloakByClient();
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][KEYCLOAK_INSTANCE][{}]", username, keycloakInstance);

        var usersResource = keycloakInstance.realm(keycloakSpringBootProperties.getRealm()).users();
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_RESOURCE][{}]", username, usersResource);

        try (var userCreateResponse = usersResource.create(userRepresentation)) {
            LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_CREATE_RESPONSE][{}]", username, userCreateResponse);

            if (userCreateResponse.getStatus() == 201) {
                var keycloakId = CreatedResponseUtil.getCreatedId(userCreateResponse);
                LOGGER.info("[AUTHENTICATION][{}][REGISTER][KEYCLOAK_ID][{}]", username, keycloakId);

                var userProfileCreate = UserProfile
                        .builder()
                        .address(request.getAddress())
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .keycloakId(keycloakId)
                        .username(username)
                        .isActive(true)
                        .phoneNumber(request.getPhoneNumber())
                        .password(PasswordUtils.encryptMD5(request.getPassword()))
                        .build();
                userProfileRepository.save(userProfileCreate);

                /* Get user từ keycloak theo id của keycloak vừa đc tạo */
                var userResource = usersResource.get(keycloakId);

                /* Set password của user vào keycloak */
                userResource.resetPassword(keycloakProperties.credentialRepresentation(request.getPassword()));

                String userRole = "ROLE_USER";

                List<RoleRepresentation> roleRepresentationList = userResource.roles().realmLevel().listAvailable();

                for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                    if (roleRepresentation.getName().equals(userRole)) {
                        userResource.roles().realmLevel().add(List.of(roleRepresentation));
                        break;
                    }
                }

                /* Cập nhật thông tin của user vào keycloak */
                userResource.update(new UserRepresentation());
            } else if (userCreateResponse.getStatus() == 409) {
                LOGGER.info("[AUTHENTICATION][{}][REGISTER][USERNAME_EXISTS_IN_KEYCLOAK]", username);
                throw new BusinessException(Constant.USERNAME_EXISTS);
            }
        }

        /* Inactive toàn bộ OTP của user */
        userProfileOTPRepository.inactiveAllStatus(username, OTPTypeEnum.REGISTER.name());

        /* Thực hiện login để trả ra token mới nhất của user */
        return loginBusiness.login(LoginRequest
                .builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build());
    }

}
