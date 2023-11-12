package vn.com.humanresourcesmanagement.service;

import java.util.List;

public interface KeycloakService {

    List<String> getRolesByKeycloakId(String keycloakId);

}
