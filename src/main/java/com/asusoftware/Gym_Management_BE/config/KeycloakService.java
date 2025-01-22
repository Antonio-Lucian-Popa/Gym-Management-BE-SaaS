package com.asusoftware.Gym_Management_BE.config;

import com.asusoftware.Gym_Management_BE.user.model.dto.CreateUserDto;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

@Service
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    public KeycloakService(@Value("${keycloak.admin.username}") String adminUsername,
                           @Value("${keycloak.admin.password}") String adminPassword,
                           @Value("${keycloak.auth-server-url}") String authServerUrl) {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm("master")
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    /**
     * Creează un utilizator în Keycloak
     *
     * @param createUserDto Datele utilizatorului
     * @return ID-ul utilizatorului creat în Keycloak
     */
    public String createKeycloakUser(CreateUserDto createUserDto) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(createUserDto.getEmail());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setEmail(createUserDto.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setRealmRoles(Collections.singletonList(createUserDto.getRole())); // Asociază rolul

        // Setează parola
        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setTemporary(false);
        password.setValue(createUserDto.getPassword());
        user.setCredentials(Collections.singletonList(password));

        // Creează utilizatorul
        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() == 201) {
            String location = response.getHeaderString("Location");
            return location.substring(location.lastIndexOf('/') + 1); // Returnează ID-ul utilizatorului
        } else {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatus());
        }
    }

    /**
     * Autentifică utilizatorul și returnează un token de acces
     *
     * @param email    Email-ul utilizatorului
     * @param password Parola utilizatorului
     * @return Tokenul de acces JWT
     */
    public String loginUser(String email, String password) {
        return obtainToken(email, password);
    }

    public String obtainToken(String username, String password) {
        Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .build();

        return userKeycloak.tokenManager().getAccessToken().getToken();
    }

    /**
     * Șterge un utilizator din Keycloak
     *
     * @param keycloakId ID-ul utilizatorului din Keycloak
     */
    public void deleteKeycloakUser(String keycloakId) {
        keycloak.realm(realm).users().delete(keycloakId);
    }
}