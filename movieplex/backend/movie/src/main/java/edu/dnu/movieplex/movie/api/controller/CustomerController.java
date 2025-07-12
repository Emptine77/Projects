package edu.dnu.movieplex.movie.api.controller;

import edu.dnu.movieplex.common.dto.UserDetails;
import edu.dnu.movieplex.common.security.utils.JwtClaimsExtractor;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieFilter;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieResponse;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleResponse;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRefundDto;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRequest;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketsGroupedBySchedule;
import edu.dnu.movieplex.movie.service.booking.MovieScheduleService;
import edu.dnu.movieplex.movie.service.booking.TicketService;
import edu.dnu.movieplex.movie.service.movie.MovieReviewService;
import edu.dnu.movieplex.movie.service.movie.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller class that handles operations related to customer and/or anonymous endpoints with
 * non-admin rights.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CustomerController {

  private final MovieScheduleService movieScheduleService;
  private final TicketService ticketService;
  private final MovieReviewService movieReviewService;
  private final MovieService movieService;

  @Operation(summary = "Get movie schedule details by ID")
  @GetMapping("/movieSchedules/{movieScheduleId}")
  @ResponseStatus(HttpStatus.OK)
  public MovieScheduleResponse getMovieSchedule(@PathVariable Integer movieScheduleId) {
    return movieScheduleService.getMovieScheduleById(movieScheduleId);
  }

  @RolesAllowed("USER")
  @Operation(summary = "Temporary book tickets by a movie schedule")
  @PostMapping("/movieSchedules/{movieScheduleId}/tickets")
  public List<TicketResponse> addTickets(@RequestBody List<TicketRequest> ticketRequest,
                                           @PathVariable Integer movieScheduleId) {
    UserDetails userDetails = JwtClaimsExtractor.extractUserDetails();
    return ticketService.createTickets(ticketRequest, userDetails.userId(), movieScheduleId);
  }

  @Operation(summary = "Get ticket details by ID")
  @GetMapping("/tickets/{ticketId}")
  @ResponseStatus(HttpStatus.OK)
  public TicketResponse getTicket(@PathVariable Integer ticketId) {
    return ticketService.getTicketById(ticketId);
  }

  @RolesAllowed("USER")
  @Operation(summary = "Get all user tickets")
  @GetMapping("/tickets")
  @ResponseStatus(HttpStatus.OK)
  public List<TicketsGroupedBySchedule> getAll() {
    UserDetails userDetails = JwtClaimsExtractor.extractUserDetails();
    return ticketService.getAllTickets(userDetails.userId());
  }

  @RolesAllowed("USER")
  @Operation(summary = "Refund a ticket")
  @PostMapping("/tickets/{ticketId}/refund")
  @ResponseStatus(HttpStatus.OK)
  public UUID ticketRefund(@PathVariable Integer ticketId) {
    return ticketService.refundTicket(ticketId);
  }

  @GetMapping("/tickets/refund")
  @ResponseStatus(HttpStatus.OK)
  public TicketRefundDto getTicketRefund(@RequestParam UUID uuid) {
    return ticketService.getTicketRefund(uuid);
  }

  @RolesAllowed("USER")
  @Operation(summary = "Add review to a movie.")
  @PostMapping("/movies/{movieId}/reviews")
  @ResponseStatus(HttpStatus.CREATED)
  public MovieReviewContext addReview(@Valid @RequestBody MovieReviewContext movieReviewContext,
                                        @PathVariable Integer movieId) {
    UserDetails userDetails = JwtClaimsExtractor.extractUserDetails();
    return movieReviewService.addMovieReview(movieReviewContext, userDetails.userId(), movieId);
  }

  @Operation(summary = "Get all reviews for a movie")
  @GetMapping("/movies/{movieId}/reviews")
  @ResponseStatus(HttpStatus.OK)
  public Page<MovieReviewContext> getAllReview(Pageable pageable, @PathVariable Integer movieId) {
    return movieReviewService.getAllMovieReview(pageable, movieId);
  }

  @Operation(summary = "Get movie details by ID")
  @GetMapping("/movies/{movieId}")
  @ResponseStatus(HttpStatus.OK)
  public MovieResponse getMovie(@PathVariable Integer movieId) {
    return movieService.getMovieById(movieId);
  }

  @Operation(summary = "Get all movies.")
  @GetMapping("/movies")
  @ResponseStatus(HttpStatus.OK)
  public Page<MovieBriefResponse> getAllMovies(Pageable pageable,
           @ModelAttribute MovieFilter movieFilter) {
    return movieService.getAllMovies(pageable, movieFilter);
  }

  @Operation(summary = "Get movie schedules by movie ID and date")
  @GetMapping("/movies/{movieId}/schedules")
  @ResponseStatus(HttpStatus.OK)
  public List<MovieScheduleBriefResponse> getMovieSchedulesByMovieIdAndDate(
            @PathVariable Integer movieId, @RequestParam LocalDate date) {
    return movieScheduleService.getMovieSchedulesByMovieAndDate(movieId, date);
  }
}
