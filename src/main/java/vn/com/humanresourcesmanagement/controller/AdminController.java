package vn.com.humanresourcesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.humanresourcesmanagement.aop.Secured;
import vn.com.humanresourcesmanagement.business.admin.UserProfilesBusiness;
import vn.com.humanresourcesmanagement.business.user.TimekeepingHistoriesBusiness;
import vn.com.humanresourcesmanagement.business.user.UserProfileBusiness;
import vn.com.humanresourcesmanagement.common.enums.RoleEnum;
import vn.com.humanresourcesmanagement.common.model.payload.response.TimekeepingHistoriesResponse;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfileResponse;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfilesResponse;

@RestController
@RequestMapping("/admin/v1")
@RequiredArgsConstructor
public class AdminController {

    private final UserProfilesBusiness userProfilesBusiness;
    private final UserProfileBusiness userProfileBusiness;
    private final TimekeepingHistoriesBusiness timekeepingHistoriesBusiness;

    /**
     * API lấy ra danh sách tất cả thông tin của user hiện có
     */
    @Secured(roles = RoleEnum.ADMIN)
    @GetMapping("/users")
    public ResponseEntity<UserProfilesResponse> userProfiles(@RequestParam("page") int page,
                                                             @RequestParam("size") int size) {
        return ResponseEntity.ok(userProfilesBusiness.process(page, size));
    }

    /**
     * API lấy ra thông tin của 1 user theo id của keycloak
     */
    @Secured(roles = RoleEnum.ADMIN)
    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<UserProfileResponse> userProfile(@PathVariable("keycloakId") String keycloakId) {
        return ResponseEntity.ok(userProfileBusiness.execute(keycloakId));
    }

    @Secured(roles = RoleEnum.ADMIN)
    @GetMapping("/timekeeping/histories")
    public ResponseEntity<TimekeepingHistoriesResponse> histories(@RequestParam("page") int page,
                                                                  @RequestParam("size") int size) {
        return ResponseEntity.ok(timekeepingHistoriesBusiness.getAll(page, size));
    }

}
