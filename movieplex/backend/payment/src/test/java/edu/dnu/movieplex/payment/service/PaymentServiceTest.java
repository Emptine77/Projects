package edu.dnu.movieplex.payment.service;

import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import edu.dnu.movieplex.movie.persistance.model.exception.IllegalTicketStatusException;
import edu.dnu.movieplex.movie.service.booking.BookingService;
import edu.dnu.movieplex.payment.domain.dto.PaymentRequest;
import edu.dnu.movieplex.payment.service.exception.PaymentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import util.TestDataGenerator;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private PaymentService paymentServiceUnderTest;

    @Test
    void chargeForTickets_ShouldSetTicketsToBookedByIds() throws StripeException {
        // given
        PaymentRequest paymentRequest = TestDataGenerator.createValidPaymentRequest();

        try (var mockedToken = Mockito.mockStatic(Token.class);
             var mockedCharge = Mockito.mockStatic(Charge.class)) {
            Token mockToken = mock(Token.class);
            when(Token.create(any(Map.class))).thenReturn(mockToken);

            List<TicketResponse> mockTickets = TestDataGenerator
                    .createTicketResponsesByTicketIdsAndTicketStatus(paymentRequest.bookedTicketIds(), TicketStatus.TEMPORARY_BOOKED);
            Charge mockCharge = mock(Charge.class);
            when(Charge.create(any(Map.class))).thenReturn(mockCharge);
            when(mockCharge.getPaid()).thenReturn(true);
            when(bookingService.getTicketsByIds(paymentRequest.bookedTicketIds())).thenReturn(mockTickets);

            // when
            paymentServiceUnderTest.chargeForTickets(paymentRequest);

            // then
            verify(bookingService).setTicketsToBookedByIds(paymentRequest.bookedTicketIds());
        }
    }

    @Test
    void chargeForTickets_WhenTicketsHaveStatusBooked_ShouldThrowIllegalTicketStatusException() throws StripeException {
        // given
        PaymentRequest paymentRequest = TestDataGenerator.createValidPaymentRequest();

        try (var mockedToken = Mockito.mockStatic(Token.class)) {
            Token mockToken = mock(Token.class);
            when(Token.create(any(Map.class))).thenReturn(mockToken);

            List<TicketResponse> mockTickets = TestDataGenerator
                    .createTicketResponsesByTicketIdsAndTicketStatus(paymentRequest.bookedTicketIds(), TicketStatus.BOOKED);
            when(bookingService.getTicketsByIds(paymentRequest.bookedTicketIds())).thenReturn(mockTickets);

            // when
            Assertions.assertThrows(IllegalTicketStatusException.class, () -> paymentServiceUnderTest.chargeForTickets(paymentRequest));
        }
    }

    @Test
    void chargeForTickets_WhenCardExceptionIsThrown_ShouldThrowPaymentException() throws StripeException {
        // given
        PaymentRequest paymentRequest = TestDataGenerator.createValidPaymentRequest();

        try (var mockedToken = Mockito.mockStatic(Token.class);
             var mockedCharge = Mockito.mockStatic(Charge.class)) {
            Token mockToken = mock(Token.class);
            when(Token.create(any(Map.class))).thenReturn(mockToken);

            List<TicketResponse> mockTickets = TestDataGenerator
                    .createTicketResponsesByTicketIdsAndTicketStatus(paymentRequest.bookedTicketIds(), TicketStatus.TEMPORARY_BOOKED);
            when(Charge.create(any(Map.class))).thenThrow(new CardException("Insufficient funds", null, null, null, null, null, 402, null));
            when(bookingService.getTicketsByIds(paymentRequest.bookedTicketIds())).thenReturn(mockTickets);
            // then
            Assertions.assertThrows(PaymentException.class, () -> paymentServiceUnderTest.chargeForTickets(paymentRequest));
        }
    }
}