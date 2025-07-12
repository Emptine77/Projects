package edu.dnu.movieplex.payment.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * A record representing a payment request for booking tickets.
 *
 * @param cardDetails The card details associated with the payment.
 * @param bookedTicketIds A non-empty list of integers representing
 *                        the IDs of the booked tickets for payment.
 */
public record PaymentRequest(
        @Valid CardDetails cardDetails,
        @NotEmpty
        List<Integer> bookedTicketIds
) {
}
