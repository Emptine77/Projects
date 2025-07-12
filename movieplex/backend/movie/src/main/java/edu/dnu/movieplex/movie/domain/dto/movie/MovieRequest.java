package edu.dnu.movieplex.movie.domain.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * A record representing a request to create or update a movie.
 */
public record MovieRequest(
        @Schema(example = "The Great Movie")
        @NotBlank(message = "title can`t be empty")
        String title,
        @Schema(example = "2023-12-28")
        @NotNull(message = "Date cannot be null")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate realiseDate,
        @Schema(example = "120")
        Integer duration,
        @Schema(example = "8")
        @NotNull(message = "Rating can`t be NULL")
        @Min(value = 0, message = "Rating can be only 0-10")
        @Max(value = 10, message = "Rating can be only 0-10")
        Short rating,
        @Schema(example = "An amazing movie about...")
        @NotBlank(message = "description can`t be empty")
        String description,
        @Schema(example = "18")
        Integer ageLimit,
        @Schema(example = "USA")
        @NotBlank(message = "country of production can`t be empty")
        String countryOfProduction,
        @Schema(example = "John Director")
        @NotBlank(message = "director can`t be empty")
        String director,
        @Schema(example = "2023-12-01")
        @NotNull(message = "Date cannot be null")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate rentalPeriodStart,
        @Schema(example = "2024-01-28")
        @NotNull(message = "Date cannot be null")
        @Future(message = "Date must be in the future")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate rentalPeriodEnd,
        @Schema(example = "English")
        @NotBlank(message = "language can`t be empty")
        String language,
        @Schema(example = "25.00")
        @NotNull(message = "Price can`t be null")
        @DecimalMin(value = "0.00", message = "Price must be greater than or equal to 0.00")
        BigDecimal basePrice,
        @Schema(example = "[Action, Drama]")
        @NotEmpty(message = "genres can`t be empty")
        List<String> genres,
        @Schema(example = "[Company A, Company B]")
        @NotEmpty(message = "production companies can`t be empty")
        List<String> productionCompany
) {
}
