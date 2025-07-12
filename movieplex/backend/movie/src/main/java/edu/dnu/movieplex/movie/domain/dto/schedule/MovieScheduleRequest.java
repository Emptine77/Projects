package edu.dnu.movieplex.movie.domain.dto.schedule;

import edu.dnu.movieplex.movie.persistance.model.enums.MovieFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * A record representing a request to create a movie schedule.
 *
 * @param startTime The start time of the movie schedule.
 * @param movieFormat The format of the movie schedule.
 * @param cinemaRoomId The unique identifier of the cinema room associated with the schedule.
 */
public record MovieScheduleRequest(
        @Schema(example = "2023-12-30T18:00:00")
        @NotNull(message = "startTime cannot be null")
        LocalDateTime startTime,
        @Schema(example = "2D")
        @NotNull(message = "MovieFormat cannot be null")
        MovieFormat movieFormat,
        @Schema(example = "1")
        @NotNull(message = "CinemaRoomId cannot be null")
        @Positive(message = "CinemaRoomId must be a positive integer")
        Integer cinemaRoomId
) {
}

