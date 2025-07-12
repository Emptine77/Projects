package edu.dnu.movieplex.user.service;

import edu.dnu.movieplex.user.domain.dto.LogoutRequest;
import edu.dnu.movieplex.user.domain.dto.RefreshTokenRequest;
import feign.QueryMap;
import java.util.HashMap;
import java.util.Map;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Feign client interface for communicating with the Keycloak API.
 */
@FeignClient(name = "keycloak-client", url = "${keycloak.base-url}")
public interface KeycloakClient {

    /**
     * Log out a user session using the provided parameters.
     *
     * @param logoutRequestParams Map containing logout request parameters.
     * @return ResponseEntity containing the result of the logout operation.
     */
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> logout(@QueryMap Map<String, ?> logoutRequestParams);

    /**
     * Refresh the user access token using the provided parameters.
     *
     * @param refreshTokenParams Map containing refresh token request parameters.
     * @return AccessTokenResponse containing the refreshed access token and new refresh token.
     */
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AccessTokenResponse refreshToken(@QueryMap Map<String, ?> refreshTokenParams);

    /**
     * Maps the LogoutRequest object to a map of request parameters.
     *
     * @param logoutRequest LogoutRequest object containing logout request details.
     * @return Map of parameters representing the logout request.
     */
    default Map<String, ?> mapLogoutRequest(LogoutRequest logoutRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("refresh_token", logoutRequest.getRefreshToken());
        params.put("client_id", logoutRequest.getClientId());
        params.put("client_secret", logoutRequest.getClientSecret());
        return params;
    }

    /**
     * Maps the RefreshTokenRequest object to a map of request parameters.
     *
     * @param refreshTokenRequest RefreshTokenRequest object containing refresh token request
     *                            details.
     * @return Map of parameters representing the refresh token request.
     */
    default Map<String, ?> mapRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", refreshTokenRequest.getGrantType());
        params.put("refresh_token", refreshTokenRequest.getRefreshToken());
        params.put("client_id", refreshTokenRequest.getClientId());
        params.put("client_secret", refreshTokenRequest.getClientSecret());
        return params;
    }
}

