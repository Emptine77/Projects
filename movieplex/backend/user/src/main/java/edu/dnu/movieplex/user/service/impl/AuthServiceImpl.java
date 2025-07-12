package edu.dnu.movieplex.user.service.impl;

import edu.dnu.movieplex.user.domain.dto.UserRegistrationRequest;
import edu.dnu.movieplex.user.domain.dto.UserResponse;
import edu.dnu.movieplex.user.domain.mapper.UserMapper;
import edu.dnu.movieplex.user.persistance.model.entity.User;
import edu.dnu.movieplex.user.service.AuthService;
import edu.dnu.movieplex.user.service.KeycloakService;
import edu.dnu.movieplex.user.service.UserService;
import edu.dnu.movieplex.user.service.exception.KeycloakException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService implementation responsible for authentication-related operations.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final KeycloakService keycloakService;
  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserResponse createUser(UserRegistrationRequest registrationRequest) {
    try (Response response = keycloakService.createUser(registrationRequest)) {
      if (response.getStatus() != 201) {
        throw new KeycloakException("Unable to create user in keycloak");
      }
    }
    User user = userService.createUser(registrationRequest);

    return userMapper.toResponse(user);
  }
}
