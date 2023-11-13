package vn.com.humanresourcesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.humanresourcesmanagement.aop.Secured;
import vn.com.humanresourcesmanagement.business.user.CheckinBusiness;
import vn.com.humanresourcesmanagement.business.user.CheckoutBusiness;
import vn.com.humanresourcesmanagement.business.user.TimekeepingHistoriesBusiness;
import vn.com.humanresourcesmanagement.business.user.TimekeepingHistoryBusiness;
import vn.com.humanresourcesmanagement.business.user.UserProfileBusiness;
import vn.com.humanresourcesmanagement.common.enums.RoleEnum;
import vn.com.humanresourcesmanagement.common.model.payload.response.TimekeepingHistoriesResponse;
import vn.com.humanresourcesmanagement.common.model.payload.response.TimekeepingHistoryResponse;
import vn.com.humanresourcesmanagement.common.model.payload.response.UserProfileResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/v1")
public class UserController {

    private final CheckinBusiness checkinBusiness;
    private final CheckoutBusiness checkoutBusiness;
    private final UserProfileBusiness userProfileBusiness;
    private final TimekeepingHistoryBusiness timekeepingHistoryBusiness;
    private final TimekeepingHistoriesBusiness timekeepingHistoriesBusiness;

    /**
     * API chấm công vào (checkin)
     */
    @Secured(roles = RoleEnum.USER)
    @PostMapping("/timekeeping/checkin")
    public void checkin() {
        checkinBusiness.execute();
    }

    /**
     * API chấm công ra (checkout)
     */
    @Secured(roles = RoleEnum.USER)
    @PostMapping("/timekeeping/checkout")
    public void checkout() {
        checkoutBusiness.execute();
    }

    /**
     * API lấy ra thông tin user
     */
    @Secured(roles = RoleEnum.ALL)
    @GetMapping("/my-profile")
    public ResponseEntity<UserProfileResponse> myProfile() {
        return ResponseEntity.ok(userProfileBusiness.execute());
    }

    /**
     * API lấy ra thông tin chấm công của ngày hiện tại
     */
    @Secured(roles = RoleEnum.USER)
    @GetMapping("/timekeeping/history")
    public ResponseEntity<TimekeepingHistoryResponse> getHistoryTimekeeping() {
        return ResponseEntity.ok(timekeepingHistoryBusiness.execute());
    }

    /**
     * API lấy ra danh sách tất cả các thông tin chấm công của người dùng đó
     */
    @Secured(roles = RoleEnum.USER)
    @GetMapping("/timekeeping/histories")
    public ResponseEntity<TimekeepingHistoriesResponse> getHistoriesTimekeeping(@RequestParam("page") int page,
                                                                                @RequestParam("size") int size) {
        return ResponseEntity.ok(timekeepingHistoriesBusiness.execute(page, size));
    }

}
