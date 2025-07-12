package edu.dnu.movieplex.common.dto;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents user details.
 *
 * @param userId      The user's unique id.
 * @param email       The user's email address.
 * @param phoneNumber An optional phone number associated with the user.
 */
public record UserDetails(
        UUID userId,
        String email,
        Optional<String> phoneNumber
) {
}
