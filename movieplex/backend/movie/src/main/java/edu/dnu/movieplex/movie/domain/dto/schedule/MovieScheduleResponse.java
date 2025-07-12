package edu.dnu.movieplex.movie.domain.dto.schedule;

import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomResponse;
import edu.dnu.movieplex.movie.persistance.model.enums.MovieFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A record representing detailed information about a movie schedule in a response.
 *
 * @param id The unique identifier of the movie schedule.
 * @param startTime The start time of the movie schedule.
 * @param movieFormat The format of the movie schedule.
 * @param ticketPrice The price of the movie ticket for the schedule.
 * @param cinemaRoom The detailed information about the associated cinema room.
 * @param bookedSeats The list of booked seats for the schedule.
 */
public record MovieScheduleResponse(
        Integer id,
        LocalDateTime startTime,
        MovieFormat movieFormat,
        BigDecimal ticketPrice,
        CinemaRoomResponse cinemaRoom,
        List<BookedSeat> bookedSeats
) {
}
