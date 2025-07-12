package edu.dnu.movieplex.movie.domain.dto.movie;

import java.time.LocalDate;

/**
 * Represents a brief details response for a movie.
 */
public record MovieBriefResponse(
        Integer id,
        String title,
        Short rating,
        Integer duration,
        Integer ageLimit,
        String director,
        LocalDate rentalPeriodStart,
        LocalDate rentalPeriodEnd,
        String posterUrl
) {
}
