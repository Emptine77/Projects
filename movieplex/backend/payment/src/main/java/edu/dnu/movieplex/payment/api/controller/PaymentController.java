package edu.dnu.movieplex.payment.api.controller;

import edu.dnu.movieplex.payment.domain.dto.PaymentRequest;
import edu.dnu.movieplex.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling payment-related API requests.
 */
@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
@Tag(name = "Payment")
public class PaymentController {
  private final PaymentService paymentService;

  @Operation(summary = "Pay for temporary booked tickets.")
  @PostMapping("/charge")
  @RolesAllowed("USER")
  @ResponseStatus(HttpStatus.OK)
  public void charge(@Valid @RequestBody PaymentRequest paymentRequest) {
    paymentService.chargeForTickets(paymentRequest);
  }
}
