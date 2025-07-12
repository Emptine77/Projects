package edu.dnu.movieplex.apigateway.config.security;


import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

/**
 * This class converts a JSON Web Token (JWT) into a collection of granted authorities.
 * By default, Spring Security authorizes users based on scope claims from the JWT Access Token.
 * However, to authorize users by roles, this class overrides the default configuration
 * by implementing a custom converter.
 * The converter extracts role information from the "realm_access" claim in the JWT
 * and converts them into Spring Security GrantedAuthority objects with a "ROLE_" prefix.
 */
@Component
@AllArgsConstructor
public class GrantedAuthoritiesConverter implements Converter<Jwt, AbstractAuthenticationToken> {
  private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
          new JwtGrantedAuthoritiesConverter();

  @Override
  public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
    Collection<GrantedAuthority> authorities =
            Stream.concat(
                            jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                            extractResourceRoles(jwt).stream())
                    .collect(Collectors.toSet());
    return new JwtAuthenticationToken(jwt, authorities);
  }

  private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
    Map<String, Collection<String>> resourceAccess;

    if (jwt.getClaim("realm_access") == null) {
      return Set.of();
    }
    resourceAccess = jwt.getClaim("realm_access");

    if (resourceAccess.get("roles") == null) {
      return Set.of();
    }
    Collection<String> resourceRoles = resourceAccess.get("roles");

    return resourceRoles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .toList();
  }
}
