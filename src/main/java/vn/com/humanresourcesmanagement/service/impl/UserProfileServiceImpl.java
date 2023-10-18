package vn.com.humanresourcesmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.humanresourcesmanagement.data.entity.UserProfile;
import vn.com.humanresourcesmanagement.data.repository.UserProfileRepository;
import vn.com.humanresourcesmanagement.service.UserProfileService;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;

    @Override
    public UserProfile findUserProfileByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

}
