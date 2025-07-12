package edu.dnu.movieplex.movie.domain.dto.schedule;

import edu.dnu.movieplex.movie.persistance.model.enums.MovieFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A record representing brief information about a movie schedule in a response.
 *
 * @param id The unique identifier of the movie schedule.
 * @param startTime The start time of the movie schedule.
 * @param movieFormat The format of the movie schedule.
 */
public record MovieScheduleBriefResponse(
        Integer id,
        LocalTime startTime,
        MovieFormat movieFormat
) {
  public MovieScheduleBriefResponse(Integer id, LocalDateTime startTime, MovieFormat movieFormat) {
    this(id, startTime.toLocalTime(), movieFormat);
  }
}
