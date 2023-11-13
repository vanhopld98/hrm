package vn.com.humanresourcesmanagement.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.exception.BusinessException;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfileResponse;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.data.entity.UserProfile;
import vn.com.humanresourcesmanagement.data.repository.UserProfileRepository;
import vn.com.humanresourcesmanagement.service.KeycloakService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProfileBusiness {

    private final UserProfileRepository userProfileRepository;
    private final KeycloakService keycloakService;

    public UserProfileResponse execute() {
        var username = JWTUtils.getUsername();
        var userProfileOptional = userProfileRepository.findByUsername(username);
        if (userProfileOptional.isPresent()) {
            return getUserProfile(userProfileOptional.get());
        } else {
            throw new BusinessException("Không tìm thấy thông tin tài khoản");
        }
    }

    public UserProfileResponse execute(String keycloakId) {
        var userProfileOptional = userProfileRepository.findByKeycloakId(keycloakId);
        if (userProfileOptional.isPresent()) {
            return getUserProfile(userProfileOptional.get());
        } else {
            throw new BusinessException("Không tìm thấy thông tin tài khoản");
        }
    }

    private UserProfileResponse getUserProfile(UserProfile userProfile) {
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setAddress(userProfile.getAddress());
        userProfileResponse.setKeycloakId(userProfile.getKeycloakId());
        userProfileResponse.setUsername(userProfile.getUsername());
        userProfileResponse.setFirstName(userProfile.getFirstName());
        userProfileResponse.setLastName(userProfile.getLastName());
        userProfileResponse.setPhoneNumber(userProfile.getPhoneNumber());
        userProfileResponse.setIsActive(userProfile.getIsActive());
        userProfileResponse.setEmail(userProfile.getEmail());
        userProfileResponse.setCreatedAt(userProfile.getCreatedAt());
        userProfileResponse.setRoles(keycloakService.getRolesByKeycloakId(userProfile.getKeycloakId()));
        return userProfileResponse;
    }
}
