package edu.dnu.movieplex.payment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * A record representing card details used in payment transactions.
 */
public record CardDetails(
        @Schema(example = "4242424242424242")
        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "^[0-9]{16}$", message = "Invalid card number")
        String cardNumber,
        @Schema(example = "04")
        @NotBlank(message = "Expiration month is required")
        @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Invalid expiration month")
        String expMonth,
        @Schema(example = "25")
        @NotBlank(message = "Expiration year is required")
        @Pattern(regexp = "^[0-9]{2}$", message = "Invalid expiration year")
        String expYear,
        @Schema(example = "123")
        @NotBlank(message = "CVC is required")
        @Pattern(regexp = "^[0-9]{3}$", message = "Invalid CVC")
        String cvc
) {
}
