package vn.com.humanresourcesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.humanresourcesmanagement.aop.Secured;
import vn.com.humanresourcesmanagement.business.user.CheckinBusiness;
import vn.com.humanresourcesmanagement.business.user.CheckoutBusiness;
import vn.com.humanresourcesmanagement.common.enums.RoleEnum;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/v1")
public class UserController {

    private final CheckinBusiness checkinBusiness;
    private final CheckoutBusiness checkoutBusiness;

    @Secured(roles = RoleEnum.USER)
    @PostMapping("/timekeeping/checkin")
    public void checkin(){
        checkinBusiness.execute();
    }

    @Secured(roles = RoleEnum.USER)
    @PostMapping("/timekeeping/checkout")
    public void checkout(){
        checkoutBusiness.execute();
    }

}
