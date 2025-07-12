package edu.dnu.movieplex.user.service;

import edu.dnu.movieplex.user.domain.dto.UserDetailsResponse;
import edu.dnu.movieplex.user.domain.dto.UserRegistrationRequest;
import edu.dnu.movieplex.user.persistance.model.entity.User;

/**
 * UserService interface for managing user-related operations.
 */
public interface UserService {

    User createUser(UserRegistrationRequest registrationRequest);

    void verifyEmailByToken(String token);

    UserDetailsResponse getUserDetails();
}
