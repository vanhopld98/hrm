package vn.com.humanresourcesmanagement.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;
import vn.com.humanresourcesmanagement.common.model.payload.response.AccessTokenResponse;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface AuthenticationMapper {

    @Mappings({
            @Mapping(target = "accessToken", source = "accessTokenResponse.token"),
            @Mapping(target = "sessionId", source = "accessTokenResponse.sessionState")
    })
    AccessTokenResponse mapToAccessTokenResponse(org.keycloak.representations.AccessTokenResponse accessTokenResponse, List<String> roles);

}
