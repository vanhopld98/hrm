package vn.com.humanresourcesmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;
import vn.com.humanresourcesmanagement.configuration.properties.KeycloakProperties;
import vn.com.humanresourcesmanagement.service.KeycloakService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final KeycloakProperties keycloakProperties;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    @Override
    public List<String> getRolesByKeycloakId(String keycloakId) {
        var keycloakInstance = keycloakProperties.getKeycloakByClient();
        var usersResource = keycloakInstance.realm(keycloakSpringBootProperties.getRealm()).users();
        var userResource = usersResource.get(keycloakId);
        List<RoleRepresentation> roleRepresentationList = userResource.roles().realmLevel().listEffective();
        return roleRepresentationList.stream().map(RoleRepresentation::getName).filter(name -> name.startsWith("ROLE_")).collect(Collectors.toList());
    }

}
