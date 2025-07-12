package edu.dnu.movieplex.user.service.impl;

import edu.dnu.movieplex.user.service.KeycloakClient;
import edu.dnu.movieplex.user.service.exception.FeignClientRequestException;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of the KeycloakClient interface responsible for handling Keycloak client
 * operations.
 */
@Service
public class KeycloakClientImpl implements KeycloakClient {

    @Qualifier("edu.dnu.movieplex.user.service.KeycloakClient")
    @Autowired
    private KeycloakClient keycloakClient;

    @Override
    public ResponseEntity<String> logout(Map<String, ?> logoutRequestParams) {
        try {
            return keycloakClient.logout(logoutRequestParams);
        } catch (Exception e) {
            throw new FeignClientRequestException("Feign client request failed: " + e.getMessage());
        }
    }

    @Override
    public AccessTokenResponse refreshToken(Map<String, ?> refreshTokenRequestParams) {
        try {
            return keycloakClient.refreshToken(refreshTokenRequestParams);
        } catch (Exception e) {
            throw new FeignClientRequestException("Feign client request failed: " + e.getMessage());
        }
    }
}

