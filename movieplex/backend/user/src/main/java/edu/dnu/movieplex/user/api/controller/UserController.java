package edu.dnu.movieplex.user.api.controller;

import edu.dnu.movieplex.user.domain.dto.UserDetailsResponse;
import edu.dnu.movieplex.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller class that handles operations related to user.
 * - GET /users/me : Get user profile<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "This method retrieves a information about current user.")
    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> getUserDetails() {
        return ResponseEntity.ok(userService.getUserDetails());
    }
}
