package edu.dnu.movieplex.movie.domain.dto.ticket;

import java.math.BigDecimal;

/**
 * A record representing schedule-related ticket information.
 *
 * @param id The unique identifier of the ticket.
 * @param seatRow The row number of the seat.
 * @param seatColumn The column number of the seat.
 * @param price The price of the ticket.
 * @param ticketStatus The status of the ticket.
 */
public record ScheduleTicketDto(
        Integer id,
        Integer seatRow,
        Integer seatColumn,
        BigDecimal price,
        String ticketStatus
) {
}
