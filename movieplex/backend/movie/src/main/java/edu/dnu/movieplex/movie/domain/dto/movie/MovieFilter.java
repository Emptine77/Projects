package edu.dnu.movieplex.movie.domain.dto.movie;

import java.time.LocalDate;


/**
 * A record representing a filter for querying movies.
 *
 * @param date The date of schedule for which movies are to be filtered.
 */
public record MovieFilter(
        LocalDate date
) {
}
