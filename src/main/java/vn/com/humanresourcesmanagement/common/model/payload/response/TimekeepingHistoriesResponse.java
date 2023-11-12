package vn.com.humanresourcesmanagement.common.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimekeepingHistoriesResponse {

    private int totalPage;
    private int totalRecord;
    private List<TimekeepingHistoryResponse> timekeepingHistories;

}
