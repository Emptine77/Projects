package edu.dnu.movieplex.payment.service;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import edu.dnu.movieplex.movie.persistance.model.exception.IllegalTicketStatusException;
import edu.dnu.movieplex.movie.service.booking.BookingService;
import edu.dnu.movieplex.payment.domain.dto.CardDetails;
import edu.dnu.movieplex.payment.domain.dto.PaymentRequest;
import edu.dnu.movieplex.payment.service.exception.PaymentException;
import edu.dnu.movieplex.payment.service.exception.UnsuccessfulChargeException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for handling payments related to movie tickets.
 */
@Service
@Slf4j
public class PaymentService {
  @Value("${stripe.secret-key}")
  private String stripeApiSecretKey;
  @Value("${stripe.public-key}")
  private String stripeApiPublicKey;
  @Autowired
  private BookingService bookingService;

  /**
  * Charges the user for the specified movie tickets using the provided payment information.
  * Retrieves the tickets, calculates the total price, validates each ticket,
  * If the charge is successful, updates the status of the booked tickets.
  *
  * @param paymentRequest The payment request containing card details and booked ticket IDs.
  * @throws PaymentException If an error occurs during the payment process.
  */
  public void chargeForTickets(PaymentRequest paymentRequest) {
    try {
      Token token = createToken(paymentRequest.cardDetails());
      List<TicketResponse> tickets = bookingService
              .getTicketsByIds(paymentRequest.bookedTicketIds());

      BigDecimal totalPrice = tickets
            .stream()
            .peek(this::validateTicket)
            .map(TicketResponse::price)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
      charge(totalPrice, token);
      bookingService.setTicketsToBookedByIds(paymentRequest.bookedTicketIds());
    } catch (StripeException e) {
      throw new PaymentException(e.getMessage());
    }
  }

  private Token createToken(CardDetails cardDetails) throws StripeException {
    Stripe.apiKey = stripeApiPublicKey;
    Map<String, Object> card = new HashMap<>();
    card.put("number", cardDetails.cardNumber());
    card.put("exp_month", Integer.parseInt(cardDetails.expMonth()));
    card.put("exp_year", Integer.parseInt(cardDetails.expYear()));
    card.put("cvc", cardDetails.cvc());
    return Token.create(Map.of("card", card));
  }

  private void charge(BigDecimal amount, Token token) throws StripeException {
    Stripe.apiKey = stripeApiSecretKey;
    Map<String, Object> chargeParams = new HashMap<>();
    chargeParams.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
    chargeParams.put("currency", "USD");
    chargeParams.put("description", "Payment for ticket");
    chargeParams.put("source", token.getId());
    Charge charge = Charge.create(chargeParams);

    if (!charge.getPaid()) {
      throw new UnsuccessfulChargeException(charge.getOutcome().getSellerMessage());
    }
  }

  private void validateTicket(TicketResponse ticket) {
    if (!ticket.ticketStatus()
            .equals(TicketStatus.TEMPORARY_BOOKED.getName())) {
      throw new IllegalTicketStatusException(
              String.format(
                "The ticket with ID=%d must have status='temporary booked', but was status='%s'",
                ticket.id(), ticket.ticketStatus()));
    }
  }
}
