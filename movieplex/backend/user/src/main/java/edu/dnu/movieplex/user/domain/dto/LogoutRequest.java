package edu.dnu.movieplex.user.domain.dto;

import lombok.*;

/**
 * Represents a request object used for user logout.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequest {
    private String refreshToken;
    private String clientId;
    private String clientSecret;
}
