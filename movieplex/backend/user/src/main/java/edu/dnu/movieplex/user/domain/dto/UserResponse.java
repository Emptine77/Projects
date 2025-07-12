package edu.dnu.movieplex.user.domain.dto;

import edu.dnu.movieplex.user.persistance.model.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response object containing user information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "john.conor@gmail.com")
    private String email;
    @Schema(example = "John")
    private String firstName;
    @Schema(example = "Conor")
    private String lastName;
    @Schema(example = "INACTIVE")
    private UserStatus status;
    @Schema(example = "2023-12-25T20:23:59.722369")
    private LocalDateTime createdAt;
}

