package edu.dnu.movieplex.user.service;

import edu.dnu.movieplex.user.domain.dto.UserRegistrationRequest;
import edu.dnu.movieplex.user.domain.dto.UserResponse;

/**
 * AuthService interface for user authentication-related operations.
 */
public interface AuthService {

    UserResponse createUser(UserRegistrationRequest registrationRequest);

}
