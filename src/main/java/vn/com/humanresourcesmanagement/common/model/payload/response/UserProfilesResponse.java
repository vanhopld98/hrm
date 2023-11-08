package vn.com.humanresourcesmanagement.common.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilesResponse extends BasePageResponse {

    private List<UserProfileResponse> userProfileResponses;

}
