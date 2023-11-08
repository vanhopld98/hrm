package vn.com.humanresourcesmanagement.common.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String keycloakId;

    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String address;

    private Boolean isActive;

    private String email;

    private LocalDateTime createdAt;

}
