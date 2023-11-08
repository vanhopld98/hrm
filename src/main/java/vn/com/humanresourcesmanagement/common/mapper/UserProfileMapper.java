package vn.com.humanresourcesmanagement.common.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfileResponse;
import vn.com.humanresourcesmanagement.data.entity.UserProfile;

@Component
@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileResponse convertToUserProfileResponse(UserProfile userProfile);

}
