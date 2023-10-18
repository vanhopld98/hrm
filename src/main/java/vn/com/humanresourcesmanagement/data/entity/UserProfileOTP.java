package vn.com.humanresourcesmanagement.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile_otp")
public class UserProfileOTP extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "otp")
    private String otp;

    @Column(name = "type")
    private String type;

    @Column(name = "verify_at")
    private LocalDateTime verifyAt;

    @Column(name = "count_verify_false")
    private Integer countVerifyFalse;

    @Column(name = "last_verify_at")
    private LocalDateTime lastVerifyAt;

}