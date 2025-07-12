package edu.dnu.movieplex.user.service.impl;

import edu.dnu.movieplex.user.persistance.model.entity.User;
import edu.dnu.movieplex.user.persistance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class which the purpose is to get the current user from security context.
 *
 * @author osynelnyk
 */
@Service
@RequiredArgsConstructor
public class ActorService {

    private final UserRepository userRepository;

    /**
     * Retrieves the optional user from security context.
     *
     * @return optional user, if present. Otherwise, empty optional.
     */
    public Optional<User> getOptionalCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> currentUser = Optional.empty();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            String username = jwt.getClaim("preferred_username").toString();
            currentUser = userRepository.findUserByEmail(username);
        }
        return currentUser;
    }

    /**
     * Retrieves the current user from security context.
     *
     * @return the user, if present. Otherwise, throws EntityNotFoundException.
     */
    public User getCurrentUser() {
        return getOptionalCurrentUser()
                .orElseThrow(() -> new EntityNotFoundException("No user found in security context"));
    }
}
