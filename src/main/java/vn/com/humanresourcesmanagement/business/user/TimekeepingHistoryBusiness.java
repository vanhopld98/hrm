package vn.com.humanresourcesmanagement.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.model.payload.response.TimekeepingHistoryResponse;
import vn.com.humanresourcesmanagement.common.utils.DateUtils;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.data.entity.Timekeeping;
import vn.com.humanresourcesmanagement.data.repository.TimekeepingRepository;

@Component
@RequiredArgsConstructor
public class TimekeepingHistoryBusiness {

    private final TimekeepingRepository timekeepingRepository;

    public TimekeepingHistoryResponse execute() {
        TimekeepingHistoryResponse response = new TimekeepingHistoryResponse();
        String username = JWTUtils.getUsername();
        Timekeeping timekeeping = timekeepingRepository.getTimekeepingCurrent(username);
        if (timekeeping == null) {
            return response;
        }

        response.setCheckoutTime(DateUtils.convertLocalDateTimeToString(timekeeping.getCheckoutTime(), DateUtils.HH_MM_SS));
        response.setCheckinTime(DateUtils.convertLocalDateTimeToString(timekeeping.getCheckinTime(), DateUtils.HH_MM_SS));
        response.setWorkingTime(timekeeping.getWorkTime().intValue());

        switch (timekeeping.getStatus()) {
            case "CHECKIN":
                response.setIsCheckin(true);
                response.setIsCheckout(false);
                break;
            case "CHECKOUT":
                response.setIsCheckin(true);
                response.setIsCheckout(true);
                break;
            default:
                response.setIsCheckin(false);
                response.setIsCheckout(false);
        }

        return response;
    }

}
