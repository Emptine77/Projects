package edu.dnu.movieplex.movie.domain.dto.ticket;

import java.time.LocalDate;

/**
 * A record representing ticket refund information.
 *
 * @param ticket The ticket response associated with the refund.
 * @param expirationDate The expiration date of the refund.
 * @param isUsed A boolean indicating whether the refund has been used.
 */
public record TicketRefundDto(
        TicketResponse ticket,
        LocalDate expirationDate,
        Boolean isUsed
) {
}
