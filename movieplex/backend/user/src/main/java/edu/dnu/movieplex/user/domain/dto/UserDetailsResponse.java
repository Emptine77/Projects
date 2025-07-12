package edu.dnu.movieplex.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response used for user details info.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    @Schema(example = "John")
    private String firstName;
    @Schema(example = "John")
    private String lastName;
    @Schema(example = "john.john@gmail.com")
    @JsonProperty("email")
    private String email;
}
