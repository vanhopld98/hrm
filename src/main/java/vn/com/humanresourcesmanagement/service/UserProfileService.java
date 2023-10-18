package vn.com.humanresourcesmanagement.service;

import vn.com.humanresourcesmanagement.data.entity.UserProfile;

public interface UserProfileService {

    UserProfile findUserProfileByUsername (String username);

}
