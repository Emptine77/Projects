package edu.dnu.movieplex.movie.api.controller.admin;


import edu.dnu.movieplex.movie.domain.dto.movie.MovieRequest;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieResponse;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleRequest;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleResponse;
import edu.dnu.movieplex.movie.service.booking.MovieScheduleService;
import edu.dnu.movieplex.movie.service.movie.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing movie-related operations.
 */
@RestController
@RequestMapping("/api/v1/admin/movies")
@AllArgsConstructor
@Tag(name = "Movies")
@RolesAllowed("ADMIN")
public class MovieController {
  private MovieService movieService;
  private MovieScheduleService movieScheduleService;

  @Operation(summary = "Add a new movie.")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MovieResponse addMovie(@Valid @RequestBody MovieRequest movieRequest) {
    return movieService.addMovie(movieRequest);
  }

  @Operation(summary = "Add movie schedule.")
  @PostMapping("/{movieId}/schedules")
  @ResponseStatus(HttpStatus.CREATED)
  public MovieScheduleResponse addMovieSchedule(
          @PathVariable Integer movieId,
          @Valid @RequestBody MovieScheduleRequest movieScheduleRequest) {
    return movieScheduleService.addMovieSchedule(movieScheduleRequest, movieId);
  }

  @Operation(summary = "Update movie.")
  @PutMapping("/{movieId}")
  @ResponseStatus(HttpStatus.OK)
  public MovieResponse updateMovie(@Valid @RequestBody MovieRequest movieRequest,
                                   @PathVariable Integer movieId) {
    return movieService.updateMovie(movieRequest, movieId);
  }
}

