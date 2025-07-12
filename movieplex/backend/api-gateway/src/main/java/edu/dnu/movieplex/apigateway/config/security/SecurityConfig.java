package edu.dnu.movieplex.apigateway.config.security;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for handling security aspects of API Gateway HTTP requests.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {
  private static final String[] AUTH_WHITELIST = {
    // -- Auth
    "/api/v1/auth/**",
    // -- Swagger UI v3 (OpenAPI)
    "/swagger-resources/**",
    "/v3/api-docs/**",
    "/swagger-ui/**",
    // -- open for all users (anonymous as well)
    "/api/v1/movies/**",
    "/api/v1/schedules/**",
  };
  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private String jwkSetUri;
  @Autowired
  private GrantedAuthoritiesConverter authoritiesConverter;

  /**
  * Configures and returns a {@link SecurityFilterChain}
  *     for handling security aspects of HTTP requests.
  *
  */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
             .cors(httpSecurityCorsConfigurer -> {
               CorsConfiguration corsConfig = new CorsConfiguration();
               corsConfig.addAllowedOrigin("*");
               corsConfig.addAllowedMethod("*");
               corsConfig.addAllowedHeader("*");
               httpSecurityCorsConfigurer.configurationSource(request -> corsConfig);
             })
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(httpRequests ->
                        httpRequests
                                .requestMatchers(AUTH_WHITELIST)
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                    .jwt(jwt -> jwt.decoder(NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build())
                            .jwtAuthenticationConverter(authoritiesConverter))
            )
                .build();
  }

  /**
  * Configures and provides a CorsConfigurationSource bean to handle
  * Cross-Origin Resource Sharing (CORS) settings.
  *
  * @return The configured CorsConfigurationSource bean.
  */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowCredentials(false);
    configuration.setAllowedHeaders(
        Arrays.asList(
          "Content-Type", "X-Auth-Token", "Authorization", "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials"));
    configuration.setExposedHeaders(
        Arrays.asList(
          "Content-Type", "X-Auth-Token", "Authorization", "Access-Control-Allow-Origin",
          "Access-Control-Allow-Credentials"));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
