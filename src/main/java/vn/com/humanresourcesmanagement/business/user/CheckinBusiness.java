package vn.com.humanresourcesmanagement.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.enums.TimekeepingStatusEnums;
import vn.com.humanresourcesmanagement.common.exception.BusinessException;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.data.entity.Timekeeping;
import vn.com.humanresourcesmanagement.data.repository.TimekeepingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CheckinBusiness {

    private final TimekeepingRepository timekeepingRepository;

    public void execute() {
        var username = JWTUtils.getUsername();

        var isExistTimekeeping = timekeepingRepository.existsByUsernameCurrentDate(username, TimekeepingStatusEnums.CHECKIN.name());

        if (Boolean.TRUE.equals(isExistTimekeeping)) {
            throw new BusinessException("Bạn đã chấm công trong ngày hôm nay");
        }

        var timekeeping = new Timekeeping();
        timekeeping.setStatus(TimekeepingStatusEnums.CHECKIN.name());
        timekeeping.setUsername(username);
        timekeeping.setCheckinTime(LocalDateTime.now());
        timekeeping.setWorkDate(LocalDate.now());
        timekeeping.setWorkTime(0L);
        timekeepingRepository.save(timekeeping);
    }

}
