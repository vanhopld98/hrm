package vn.com.humanresourcesmanagement.business.authentication;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.constants.Constant;
import vn.com.humanresourcesmanagement.common.exception.BusinessException;
import vn.com.humanresourcesmanagement.common.model.payload.request.RegisterRequest;
import vn.com.humanresourcesmanagement.common.utils.PasswordUtils;
import vn.com.humanresourcesmanagement.configuration.properties.KeycloakProperties;
import vn.com.humanresourcesmanagement.data.entity.UserProfile;
import vn.com.humanresourcesmanagement.data.repository.UserProfileRepository;
import vn.com.humanresourcesmanagement.service.UserProfileService;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RegisterBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterBusiness.class);

    private static final int TOTAL_FALSE_OTP = 5;
    public static final String ROLE_USER = "ROLE_USER";

    private final UserProfileService userProfileService;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;
    private final UserProfileRepository userProfileRepository;
    private final LoginBusiness loginBusiness;

    public void register(RegisterRequest request) {

        var username = request.getUsername();

        /* Tìm kiếm trong DB xem username này đã tồn tại hay chưa */
        var userProfile = userProfileService.findUserProfileByUsername(username);
        if (Objects.nonNull(userProfile)) {
            throw new BusinessException(Constant.USERNAME_EXISTS);
        }

        var userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(request.getUsername());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setEmailVerified(true);
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_REPRESENTATION][{}]", username, userRepresentation);

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

                List<RoleRepresentation> roleRepresentationList = userResource.roles().realmLevel().listAvailable();

                for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                    if (roleRepresentation.getName().equals(ROLE_USER)) {
                        userResource.roles().realmLevel().add(List.of(roleRepresentation));
                        break;
                    }
                }

                /* Cập nhật thông tin của user vào keycloak */
                userResource.update(new UserRepresentation());
            } else if (userCreateResponse.getStatus() == 409) {
                LOGGER.info("[AUTHENTICATION][{}][REGISTER][USERNAME_EXISTS_IN_KEYCLOAK]", username);
                throw new BusinessException(Constant.USERNAME_EXISTS);
            } else {
                throw new BusinessException("Có lỗi xảy ra trong quá trình tạo mới người dùng. Vui lòng thử lại sau");
            }
        }
    }

}
