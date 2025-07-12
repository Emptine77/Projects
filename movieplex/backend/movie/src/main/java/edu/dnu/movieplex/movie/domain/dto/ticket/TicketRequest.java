package edu.dnu.movieplex.movie.domain.dto.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents the request for creating a movie ticket.
 *
 * @param seatRow    The row number of the seat.
 * @param seatColumn The column number of the seat.
 */
public record TicketRequest(
        @Schema(example = "1")
        @NotNull(message = "SeatRow cannot be null")
        @Positive(message = "SeatRow must be a positive integer")
        Integer seatRow,
        @Schema(example = "3")
        @NotNull(message = "SeatColumn cannot be null")
        @Positive(message = "SeatColumn must be a positive integer")
        Integer seatColumn
) {
}
