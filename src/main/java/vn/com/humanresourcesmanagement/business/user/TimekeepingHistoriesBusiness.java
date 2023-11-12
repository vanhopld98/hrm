package vn.com.humanresourcesmanagement.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.model.payload.response.TimekeepingHistoriesResponse;
import vn.com.humanresourcesmanagement.common.model.payload.response.TimekeepingHistoryResponse;
import vn.com.humanresourcesmanagement.common.utils.DateUtils;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.data.entity.Timekeeping;
import vn.com.humanresourcesmanagement.data.repository.TimekeepingRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TimekeepingHistoriesBusiness {

    private final TimekeepingRepository timekeepingRepository;

    public TimekeepingHistoriesResponse execute(int page, int size) {
        String username = JWTUtils.getUsername();
        TimekeepingHistoriesResponse responses = new TimekeepingHistoriesResponse();
        List<TimekeepingHistoryResponse> timekeepingHistoriesResponse = new ArrayList<>();
        List<Timekeeping> timekeepingHistories = timekeepingRepository.findAllOrderCurrentDate(username);
        timekeepingHistories = timekeepingHistories.stream().skip((long) page * size).limit(size).collect(Collectors.toList());

        for (Timekeeping timekeepingHistory : timekeepingHistories) {
            TimekeepingHistoryResponse timekeepingHistoryResponse = new TimekeepingHistoryResponse();
            timekeepingHistoryResponse.setCheckoutTime(DateUtils.convertLocalDateTimeToString(timekeepingHistory.getCheckoutTime(), DateUtils.HH_MM_SS));
            timekeepingHistoryResponse.setCheckinTime(DateUtils.convertLocalDateTimeToString(timekeepingHistory.getCheckinTime(), DateUtils.HH_MM_SS));
            timekeepingHistoryResponse.setWorkingTime(timekeepingHistory.getWorkTime().intValue());
            timekeepingHistoryResponse.setWorkingDay(DateUtils.convertLocalDateToString(timekeepingHistory.getWorkDate(), DateUtils.DD_MM_YYYY));
            switch (timekeepingHistory.getStatus()) {
                case "CHECKIN":
                    timekeepingHistoryResponse.setIsCheckin(true);
                    timekeepingHistoryResponse.setIsCheckout(false);
                    break;
                case "CHECKOUT":
                    timekeepingHistoryResponse.setIsCheckin(true);
                    timekeepingHistoryResponse.setIsCheckout(true);
                    break;
                default:
                    timekeepingHistoryResponse.setIsCheckin(false);
                    timekeepingHistoryResponse.setIsCheckout(false);
            }
            timekeepingHistoriesResponse.add(timekeepingHistoryResponse);
        }

        int totalPage = (int) Math.ceil((double) timekeepingHistoriesResponse.size() / size);
        if (totalPage == 0) {
            totalPage = 1;
        }

        responses.setTimekeepingHistories(timekeepingHistoriesResponse);
        responses.setTotalRecord(timekeepingHistoriesResponse.size());
        responses.setTotalPage(totalPage);
        return responses;
    }

}
