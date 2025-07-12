package edu.dnu.movieplex.movie.domain.dto.movie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

/**
 * A record representing detailed information about a movie in a response.
 *
**/
@Builder
public record MovieResponse(
        Integer id,
        String title,
        LocalDate realiseDate,
        Short rating,
        Integer duration,
        String description,
        Integer ageLimit,
        String countryOfProduction,
        String director,
        LocalDate rentalPeriodStart,
        LocalDate rentalPeriodEnd,
        String language,
        BigDecimal basePrice,
        String posterUrl,
        String trailerUrl,
        List<String> genres,
        List<String> productionCompany
) {
}
