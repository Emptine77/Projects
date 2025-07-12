package edu.dnu.movieplex.user.service.impl;

import edu.dnu.movieplex.user.domain.dto.*;
import edu.dnu.movieplex.user.persistance.model.entity.User;
import edu.dnu.movieplex.user.persistance.model.enums.UserStatus;
import edu.dnu.movieplex.user.persistance.repository.UserRepository;
import edu.dnu.movieplex.user.service.KeycloakClient;
import edu.dnu.movieplex.user.service.KeycloakService;
import edu.dnu.movieplex.user.service.exception.LogoutException;
import edu.dnu.movieplex.user.service.exception.UserAlreadyExistsException;
import edu.dnu.movieplex.user.service.exception.UserIsNotActiveException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * KeycloakService implementation responsible for Keycloak-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;
    private final KeycloakClientImpl keycloakClient;
    private final UserRepository userRepository;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.auth-server-url}")
    private String authUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("GuhlaIVyckTGzlJY3tInnkTsv77Z5WPq")
    private String secretKey;

    @Override
    public Response createUser(UserRegistrationRequest request) {
        if (!findByUsername(request.getEmail()).isEmpty()) {
            throw new UserAlreadyExistsException(
                    String.format("User with email='%s' already exists", request.getEmail()));
        }

        log.debug("Trying to save new user[email={}] into keycloak", request.getEmail());
        CredentialRepresentation password = preparePasswordRepresentation(request.getPassword());
        UserRepresentation user = prepareUserRepresentation(request, password);

        Response response = keycloak.realm(realm).users().create(user);
        log.debug("New user[email={}] saved into keycloak", user.getEmail());
        return response;
    }

    @Override
    public TokenResponse auth(UserLoginRequest loginRequest) {
        User user = userRepository.findUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials."));

        if (user.getStatus() == UserStatus.ACTIVE) {
            try (Keycloak userKeycloak = keycloakCredentialBuilder(loginRequest)) {
                log.debug("Trying to login with [email={}] into keycloak", loginRequest.getEmail());
                AccessTokenResponse accessTokenResponse = userKeycloak.tokenManager().getAccessToken();
                log.debug("Successfully login with [email={}] into keycloak", loginRequest.getEmail());
                return TokenResponse.builder()
                        .accessToken(accessTokenResponse.getToken())
                        .refreshToken(accessTokenResponse.getRefreshToken())
                        .build();
            } catch (NotAuthorizedException e) {
                throw new BadCredentialsException("Username or password incorrect");
            }
        } else {
            throw new UserIsNotActiveException("You can't log in without verifying your email");
        }
    }

    @Override
    public String logout(String refreshToken) {
        LogoutRequest logoutRequest = LogoutRequest.builder()
                .refreshToken(refreshToken)
                .clientId(clientId)
                .clientSecret(secretKey)
                .build();
        log.debug("Trying logout from keycloak");
        Map<String, ?> logoutRequestParams = keycloakClient.mapLogoutRequest(logoutRequest);
        ResponseEntity<String> response = keycloakClient.logout(logoutRequestParams);
        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
            log.debug("Failed logout from keycloak");
            throw new LogoutException("Failed logout from keycloak");
        }
        log.debug("Successfully logout from keycloak");
        return response.getBody();
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .clientSecret(secretKey)
                .clientId(clientId)
                .grantType("refresh_token")
                .refreshToken(refreshToken)
                .build();
        log.debug("Trying refresh token");
        Map<String, ?> refreshTokenParams = keycloakClient.mapRefreshToken(request);
        AccessTokenResponse response = keycloakClient.refreshToken(refreshTokenParams);
        log.debug("Token successfully refreshed");

        return TokenResponse.builder()
                .accessToken(response.getToken())
                .refreshToken(response.getRefreshToken())
                .build();
    }

    private List<UserRepresentation> findByUsername(String username) {
        return keycloak.realm(realm).users().search(username);
    }

    private CredentialRepresentation preparePasswordRepresentation(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }

    private UserRepresentation prepareUserRepresentation(
            UserRegistrationRequest request, CredentialRepresentation credentialRepresentation) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(request.getEmail());
        newUser.setEmail(request.getEmail());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setCredentials(Collections.singletonList(credentialRepresentation));
        newUser.setEnabled(true);
        return newUser;
    }

    private Keycloak keycloakCredentialBuilder(UserLoginRequest request) {
        return KeycloakBuilder.builder()
                .serverUrl(authUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(secretKey)
                .username(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}
