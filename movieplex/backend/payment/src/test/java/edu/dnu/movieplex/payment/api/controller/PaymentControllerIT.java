package edu.dnu.movieplex.payment.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.SecurityConfig;
import edu.dnu.movieplex.movie.persistance.model.entity.Ticket;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import edu.dnu.movieplex.movie.persistance.repository.TicketRepository;
import edu.dnu.movieplex.payment.domain.dto.CardDetails;
import edu.dnu.movieplex.payment.domain.dto.PaymentRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.TestDataGenerator;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class PaymentControllerIT {
    private static final String PAYMENT_CONTROLLER_URL = "/api/v1/payment";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TicketRepository ticketRepository;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void charge_ShouldReturnHttpStatusOK() throws Exception {
        // given
        Ticket bookedTicket = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getTicketStatus() == TicketStatus.TEMPORARY_BOOKED)
                .findFirst()
                .get();
        CardDetails cardDetails = TestDataGenerator.createValidCardDetails();
        PaymentRequest paymentRequest = new PaymentRequest(cardDetails, List.of(bookedTicket.getId()));

        //Then
        mockMvc.perform(post(PAYMENT_CONTROLLER_URL + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());
        Ticket updatedTicket = ticketRepository.findById(bookedTicket.getId()).get();
        Assertions.assertThat(updatedTicket.getTicketStatus()).isEqualTo(TicketStatus.BOOKED);
    }
}
