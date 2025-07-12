package edu.dnu.movieplex.user.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import edu.dnu.movieplex.user.config.JwtConfiguration.JwtProperties;
import edu.dnu.movieplex.user.persistance.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Jwt Service class implements main operations with jwt, like creating, extracting claims etc.
 *
 * @author osynelnyk
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String JWT_TOKEN_SUBJECT = "Verification Email Jwt Token";
    private final JwtProperties jwtProperties;

    /**
     * Method is designated to create a jwt token with custom claims, expiration time.
     *
     * @param user user object where info about them comes from for the jwt token.
     * @return string representation of the jwt token.
     */
    public String generateToken(User user) {
        log.debug("Creating JWT Token with custom claims");
        Map<String, String> extraClaims = Map.of(
                "id", user.getId().toString(),
                "email", user.getEmail()
        );
        final Date now = new Date(System.currentTimeMillis());
        final Date expiration = new Date(
                System.currentTimeMillis() + jwtProperties.expiration() * 1000);

        return Jwts.builder().setClaims(extraClaims)
                .setSubject(JWT_TOKEN_SUBJECT)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secretkey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getSubject(String jwtToken) {
        return getClaims(jwtToken, Claims::getSubject);
    }

    public boolean isTokenExpired(String jwtToken) {
        return getExpirationDate(jwtToken).before(new Date());
    }

    /**
     * Retrieves all claims from a jwt token.
     *
     * @param jwtToken string representation of the jwt token.
     * @return claims that holds specific information about the user etc.
     */
    public Claims getAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private Date getExpirationDate(String jwtToken) {
        return getClaims(jwtToken, Claims::getExpiration);
    }

    private <T> T getClaims(String jwtToken, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }
}

