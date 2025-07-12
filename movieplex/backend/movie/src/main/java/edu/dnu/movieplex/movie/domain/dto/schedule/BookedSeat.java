package edu.dnu.movieplex.movie.domain.dto.schedule;

/**
 * A record representing information about a booked seat.
 *
 * @param seatRow The row number of the booked seat.
 * @param seatColumn The column number of the booked seat.
 */
public record BookedSeat(
        Integer seatRow,
        Integer seatColumn
) {
}
