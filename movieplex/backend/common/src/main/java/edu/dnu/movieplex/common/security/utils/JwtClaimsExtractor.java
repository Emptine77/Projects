package edu.dnu.movieplex.common.security.utils;

import edu.dnu.movieplex.common.dto.UserDetails;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Utility class for extracting values from the current JWT authentication token.
 */
@Component
public class JwtClaimsExtractor {

  /**
  * Extracts user details from the current JWT authentication token.
  *
  * @return UserDetails containing user information extracted from the JWT token.
  * @throws IllegalStateException if an authentication is null
  *     or not an instance of JwtAuthenticationToken.
  */
  public static UserDetails extractUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof JwtAuthenticationToken)) {
      throw new IllegalStateException(
                "Couldn't extract user details from jwt token. "
                + "Expected JwtAuthenticationToken, but got: "
                + authentication);
    }

    Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
    Map<String, Object> claims = jwt.getClaims();

    UUID userId = UUID.fromString(extractClaim(claims, UserClaims.UUID.getClaimName()));
    String email = extractClaim(claims, UserClaims.EMAIL.getClaimName());
    Optional<String> phoneNumber = Optional.ofNullable(
            extractClaim(claims, UserClaims.PHONE_NUMBER.getClaimName()));


    return new UserDetails(userId, email, phoneNumber);
  }

  private static String extractClaim(Map<String, Object> claims, String claimName) {
    return claims.containsKey(claimName) ? claims.get(claimName).toString() : null;
  }
}
