package vn.com.humanresourcesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.humanresourcesmanagement.aop.Secured;
import vn.com.humanresourcesmanagement.business.admin.UserProfilesBusiness;
import vn.com.humanresourcesmanagement.common.enums.RoleEnum;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfilesResponse;

@RestController
@RequestMapping("/admin/v1")
@RequiredArgsConstructor
public class AdminController {

    private final UserProfilesBusiness userProfilesBusiness;

    @Secured(roles = RoleEnum.ADMIN)
    @GetMapping("/users")
    public ResponseEntity<UserProfilesResponse> userProfiles(@RequestParam("page") int page,
                                                             @RequestParam("size") int size) {
        return ResponseEntity.ok(userProfilesBusiness.process(page, size));
    }

}
