package vn.com.humanresourcesmanagement.common.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimekeepingHistoryResponse {

    private String checkinTime;
    private String checkoutTime;
    private Boolean isCheckin;
    private Boolean isCheckout;
    private Integer workingTime;
    private String workingDay;
}
