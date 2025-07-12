package edu.dnu.movieplex.movie.domain.dto.movie;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * A record representing the context for adding a review to a movie.
 *
 * @param rating The rating given to the movie (must be between 0 and 10).
 * @param comment The comment or feedback provided for the movie.
 */
public record MovieReviewContext(
        @NotNull(message = "Rating can`t be NULL")
        @Min(value = 0, message = "Rating can be only 0-10")
        @Max(value = 10, message = "Rating can be only 0-10")
        Short rating,
        String comment
) {
}
