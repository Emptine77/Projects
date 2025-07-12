package edu.dnu.movieplex.movie.api.controller.admin;


import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleRequest;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleResponse;
import edu.dnu.movieplex.movie.service.booking.MovieScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller class for managing movie schedules and related ticket operations.
 */
@RestController
@RequestMapping("/api/v1/admin/schedules")
@AllArgsConstructor
@Tag(name = "Movie schedules")
@RolesAllowed("ADMIN")
public class MovieScheduleController {
  private MovieScheduleService movieScheduleService;

  @Operation(summary = "Get all movie schedules")
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<MovieScheduleResponse> getAll() {
    return movieScheduleService.getAllMovieSchedules();
  }

  @Operation(summary = "Update movie schedule.")
  @PutMapping("/{movieScheduleId}")
  @ResponseStatus(HttpStatus.OK)
  public MovieScheduleResponse updateMovieSchedule(
          @Valid @RequestBody MovieScheduleRequest movieScheduleRequest,
          @PathVariable Integer movieScheduleId) {
    return movieScheduleService.updateMovieSchedule(movieScheduleRequest, movieScheduleId);
  }
}

