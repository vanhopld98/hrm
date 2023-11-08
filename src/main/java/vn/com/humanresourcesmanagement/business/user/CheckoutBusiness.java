package vn.com.humanresourcesmanagement.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.enums.TimekeepingStatusEnums;
import vn.com.humanresourcesmanagement.common.exception.BusinessException;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.data.repository.TimekeepingRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CheckoutBusiness {

    private final TimekeepingRepository timekeepingRepository;

    public void execute() {
        var username = JWTUtils.getUsername();

        var timekeeping = timekeepingRepository.getByUsernameAndStatusCheckin(username);

        if (Objects.isNull(timekeeping)) {
            throw new BusinessException("Bạn chưa checkin chấm công. Vui lòng checkin");
        }

        timekeeping.setCheckoutTime(LocalDateTime.now());
        Duration duration = Duration.between(timekeeping.getCheckinTime(), timekeeping.getCheckoutTime());
        timekeeping.setWorkTime(duration.toMinutes());
        timekeeping.setStatus(TimekeepingStatusEnums.CHECKOUT.name());
        timekeepingRepository.save(timekeeping);
    }

}
