package edu.dnu.movieplex.user.domain.dto;

import lombok.*;

/**
 * Represents a request object used for refreshing access tokens.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenRequest {
    private String grantType;
    private String refreshToken;
    private String clientId;
    private String clientSecret;
}
