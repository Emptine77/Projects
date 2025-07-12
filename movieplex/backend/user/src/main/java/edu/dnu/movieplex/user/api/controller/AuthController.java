package edu.dnu.movieplex.user.api.controller;

import edu.dnu.movieplex.user.domain.dto.*;
import edu.dnu.movieplex.user.service.AuthService;
import edu.dnu.movieplex.user.service.KeycloakService;
import edu.dnu.movieplex.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling authentication-related endpoints. Endpoints provided:<br>
 * - /register: Creates a new user based on the provided registration request.<br>
 * - /login: Authenticates a user based on the login request.<br>
 * - /logout: Logs out a user session using the refresh token.<br>
 * - /token/refresh: Refreshes the user's access token using the refresh token.<br>
 * - /verify: Verifies the user's email address which was entered during registration.<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;
    private final KeycloakService keycloakService;
    private final UserService userService;

    @Operation(summary = "This method is used for registration. Don't use dummy email addresses, "
            + "use your real one.")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> register(
            @RequestBody @Valid UserRegistrationRequest registrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.createUser(registrationRequest));
    }

    @Operation(summary =
            "This method is used for verifying email. The token can be found in an email "
                    + "letter that a user received during registration. But it's more convenient to verify "
                    + "directly from the email letter.")
    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmailByToken(@RequestParam("token") String token) {
        userService.verifyEmailByToken(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "This method is used for login.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(keycloakService.auth(request));
    }

    @Operation(summary = "This method is used for logout.")
    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(@RequestBody @Valid RefreshToken token) {
        keycloakService.logout(token.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "This method is used for refresh of the token.")
    @PostMapping("/token/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid RefreshToken token) {
        return ResponseEntity.ok(keycloakService.refreshToken(token.getRefreshToken()));
    }
}
