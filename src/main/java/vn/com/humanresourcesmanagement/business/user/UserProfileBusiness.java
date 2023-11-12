package vn.com.humanresourcesmanagement.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.exception.BusinessException;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfileResponse;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.data.entity.UserProfile;
import vn.com.humanresourcesmanagement.data.repository.UserProfileRepository;

@Component
@RequiredArgsConstructor
public class UserProfileBusiness {

    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse execute() {
        var username = JWTUtils.getUsername();
        var userProfileOptional = userProfileRepository.findByUsername(username);
        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();
            UserProfileResponse userProfileResponse = new UserProfileResponse();
            userProfileResponse.setAddress(userProfile.getAddress());
            userProfileResponse.setKeycloakId(userProfile.getKeycloakId());
            userProfileResponse.setUsername(userProfile.getUsername());
            userProfileResponse.setFirstName(userProfile.getFirstName());
            userProfileResponse.setLastName(userProfile.getLastName());
            userProfileResponse.setPhoneNumber(userProfile.getPhoneNumber());
            userProfileResponse.setIsActive(userProfile.getIsActive());
            userProfileResponse.setEmail(userProfile.getEmail());
            return userProfileResponse;
        } else {
            throw new BusinessException("Không tìm thấy thông tin tài khoản");
        }
    }
}
