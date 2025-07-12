package edu.dnu.movieplex.movie.domain.dto.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * A record representing information about a ticket response.
 *
 * @param id The unique identifier of the ticket.
 * @param ticketStatus The status of the ticket.
 * @param price The price of the ticket.
 * @param purchaseTime The date and time when the ticket was purchased.
 * @param seatRow The row number of the seat.
 * @param seatColumn The column number of the seat.
 */
public record TicketResponse(
        Integer id,
        String ticketStatus,
        BigDecimal price,
        LocalDateTime purchaseTime,
        Integer seatRow,
        Integer seatColumn
) {
} 
