package vn.com.humanresourcesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.humanresourcesmanagement.business.authentication.LoginBusiness;
import vn.com.humanresourcesmanagement.business.authentication.LogoutBusiness;
import vn.com.humanresourcesmanagement.business.authentication.RegisterBusiness;
import vn.com.humanresourcesmanagement.common.model.payload.request.LoginRequest;
import vn.com.humanresourcesmanagement.common.model.payload.request.LogoutRequest;
import vn.com.humanresourcesmanagement.common.model.payload.request.RegisterRequest;
import vn.com.humanresourcesmanagement.common.model.payload.response.AccessTokenResponse;
import vn.com.humanresourcesmanagement.configuration.properties.KeycloakProperties;

import java.util.List;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final LoginBusiness loginBusiness;
    private final RegisterBusiness registerBusiness;
    private final LogoutBusiness logoutBusiness;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    @PostMapping("/v1/register")
    public ResponseEntity<AccessTokenResponse> register(@RequestBody RegisterRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][STARTING...][REQUEST][{}]", request.getUsername(), request);
        return ResponseEntity.ok(registerBusiness.register(request));
    }

    @PostMapping("/v1/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][LOGIN][STARTING...][REQUEST][{}]", request.getUsername(), request);
        return ResponseEntity.ok(loginBusiness.login(request));
    }

    @PostMapping("/v1/logout")
    public void logout(@RequestBody LogoutRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][LOGOUT][STARTING...][REQUEST][{}]", request.getUsername(), request);
        logoutBusiness.logout(request.getUsername());
    }

}
