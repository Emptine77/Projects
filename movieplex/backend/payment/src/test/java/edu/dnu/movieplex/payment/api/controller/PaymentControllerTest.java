package edu.dnu.movieplex.payment.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.SecurityConfig;
import edu.dnu.movieplex.movie.persistance.model.exception.IllegalTicketStatusException;
import edu.dnu.movieplex.payment.domain.dto.PaymentRequest;
import edu.dnu.movieplex.payment.service.PaymentService;
import edu.dnu.movieplex.payment.service.exception.PaymentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import util.TestDataGenerator;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
class PaymentControllerTest {
    private static final String PAYMENT_CONTROLLER_URL = "/api/v1/payment";
    @MockBean
    private PaymentService paymentService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void charge_ShouldReturnHttpStatusOK() throws Exception {
        // given
        PaymentRequest paymentRequest = TestDataGenerator.createValidPaymentRequest();

        Mockito.doNothing().when(paymentService).chargeForTickets(paymentRequest);

        //Then
        mockMvc.perform(post(PAYMENT_CONTROLLER_URL + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void charge_WhenPaymentRequestIsInvalid_ShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        PaymentRequest paymentRequest = TestDataGenerator.createInvalidPaymentRequest();

        //Then
        mockMvc.perform(post(PAYMENT_CONTROLLER_URL + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void charge_WhenPaymentExceptionIsThrown_ShouldReturnHttpStatusPaymentRequired() throws Exception {
        // given
        PaymentRequest paymentRequest = TestDataGenerator.createValidPaymentRequest();

        doThrow(new PaymentException("Insufficient funds")).when(paymentService).chargeForTickets(paymentRequest);

        //Then
        mockMvc.perform(post(PAYMENT_CONTROLLER_URL + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isPaymentRequired());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void charge_WhenIllegalTicketStatusExceptionIsThrown_ShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        PaymentRequest paymentRequest = TestDataGenerator.createValidPaymentRequest();

        doThrow(new IllegalTicketStatusException("Expected ticket status = temporary booked, but was status = booked"))
                .when(paymentService).chargeForTickets(paymentRequest);

        //Then
        mockMvc.perform(post(PAYMENT_CONTROLLER_URL + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }
}