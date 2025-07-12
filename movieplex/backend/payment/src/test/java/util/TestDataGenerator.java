package util;

import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import edu.dnu.movieplex.payment.domain.dto.CardDetails;
import edu.dnu.movieplex.payment.domain.dto.PaymentRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TestDataGenerator {
    public static CardDetails createValidCardDetails() {
        return new CardDetails("4242424242424242", "04", "25", "123");
    }

    public static PaymentRequest createValidPaymentRequest() {
        CardDetails cardDetails = new CardDetails("4242424242424242", "04", "25", "123");
        List<Integer> bookedTicketIds = List.of(1, 2);
        return new PaymentRequest(cardDetails, bookedTicketIds);
    }

    public static PaymentRequest createInvalidPaymentRequest() {
        CardDetails cardDetails = new CardDetails("4242424242424", "13", "2", "1234");
        List<Integer> bookedTicketIds = List.of(1, 2);
        return new PaymentRequest(cardDetails, bookedTicketIds);
    }

    public static List<TicketResponse> createTicketResponsesByTicketIdsAndTicketStatus(List<Integer> ticketIds, TicketStatus ticketStatus) {
        return ticketIds
                .stream()
                .map(ticketId -> new TicketResponse(ticketId, ticketStatus.getName(), BigDecimal.valueOf(10.0), LocalDateTime.now(), 2, 1))
                .toList();
    }
}
