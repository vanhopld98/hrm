package vn.com.humanresourcesmanagement.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timekeeping")
public class Timekeeping extends BaseEntity {

    /**
     * Username
     */
    @Column(name = "username")
    private String username;

    /**
     * Thời gian checkin
     */
    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;

    /**
     * Thời gian checkout
     */
    @Column(name = "checkout_time")
    private LocalDateTime checkoutTime;

    /**
     * Trạng thái
     * - Đi muộn
     * - Về sớm
     * - Nghỉ
     */
    @Column(name = "status")
    private String status;

    /**
     * Thời gian làm việc (Tính theo phút)
     */
    @Column(name = "work_time")
    private Long workTime;

    /**
     * Ngày làm việc
     */
    @Column(name = "work_date")
    private LocalDate workDate;

}
