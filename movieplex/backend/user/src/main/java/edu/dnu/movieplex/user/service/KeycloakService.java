package edu.dnu.movieplex.user.service;

import edu.dnu.movieplex.user.domain.dto.TokenResponse;
import edu.dnu.movieplex.user.domain.dto.UserLoginRequest;
import edu.dnu.movieplex.user.domain.dto.UserRegistrationRequest;
import jakarta.ws.rs.core.Response;
import org.springframework.http.ResponseEntity;

/**
 * KeycloakService interface for Keycloak-related operations.
 */
public interface KeycloakService {

    Response createUser(UserRegistrationRequest registrationRequest);

    TokenResponse auth(UserLoginRequest loginRequest);

    String logout(String refreshToken);

    TokenResponse refreshToken(String refreshToken);
}
