package edu.dnu.movieplex.user.service.impl;

import edu.dnu.movieplex.user.domain.dto.UserDetailsResponse;
import edu.dnu.movieplex.user.domain.dto.UserRegistrationRequest;
import edu.dnu.movieplex.user.domain.mapper.UserMapper;
import edu.dnu.movieplex.user.persistance.model.entity.User;
import edu.dnu.movieplex.user.persistance.model.enums.UserStatus;
import edu.dnu.movieplex.user.persistance.repository.UserRepository;
import edu.dnu.movieplex.user.service.UserService;
import edu.dnu.movieplex.user.service.exception.AlreadyActivatedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static edu.dnu.movieplex.user.service.impl.JwtService.JWT_TOKEN_SUBJECT;

/**
 * UserService implementation responsible for user-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final ActorService actorService;

    @Transactional
    @Override
    public User createUser(UserRegistrationRequest registrationRequest) {
        log.debug("Trying to save new user[email={}] into db", registrationRequest.getEmail());
        User user = userMapper.toUser(registrationRequest);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        log.debug("New user[email={}] saved into db", user.getEmail());
        return user;
    }

    @Override
    @Transactional
    public void verifyEmailByToken(String token) {
        log.debug("Verifying the user's email by token=[{}]",
                token.substring(0, token.indexOf(".")) + " ...");
        if (jwtService.isTokenExpired(token)) {
            throw new ExpiredJwtException(new DefaultJwsHeader(), jwtService.getAllClaims(token),
                    "Your token is expired. Please, regenerate a new one.");
        }
        Claims claims = jwtService.getAllClaims(token);
        String subject = jwtService.getSubject(token);
        Long id = Long.valueOf(claims.get("id", String.class));
        String email = claims.get("email", String.class);
        userRepository.findById(id).ifPresent(user -> {
            if (user.getEmail().equals(email) && subject.equals(JWT_TOKEN_SUBJECT)) {
                if (user.getStatus() == UserStatus.INACTIVE) {
                    user.setStatus(UserStatus.ACTIVE);
                } else {
                    throw new AlreadyActivatedException(
                            "You've already activated your account, proceed to login, please");
                }
            } else {
                throw new JwtException("Wrong claims in the jwt token");
            }
            log.debug("Verification is completed successfully! The user [id={}] is ACTIVE now",
                    user.getId());
        });
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetailsResponse getUserDetails() {
        User user = actorService.getCurrentUser();
        return userMapper.toUserDetailsResponse(user);
    }
}
