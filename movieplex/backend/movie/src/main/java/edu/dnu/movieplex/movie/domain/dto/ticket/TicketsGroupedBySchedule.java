package edu.dnu.movieplex.movie.domain.dto.ticket;

import edu.dnu.movieplex.movie.domain.dto.movie.MovieBriefResponse;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A record representing a grouping of tickets based on a movie schedule.
 *
 * @param movie The brief response of the associated movie.
 * @param scheduleStartDateTime The start date and time of the schedule.
 * @param cinemaRoomName The name of the cinema room.
 * @param tickets The list of tickets associated with the schedule.
 */
public record TicketsGroupedBySchedule(
        MovieBriefResponse movie,
        LocalDateTime scheduleStartDateTime,
        String cinemaRoomName,
        List<ScheduleTicketDto> tickets
) {
}
