package edu.dnu.movieplex.movie.api.controller.admin;

import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomRequest;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomResponse;
import edu.dnu.movieplex.movie.service.booking.CinemaRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller class for managing cinema room related operations.
 */
@RestController
@RequestMapping("/api/v1/admin/cinemaRooms")
@AllArgsConstructor
@Tag(name = "Cinema rooms")
@RolesAllowed("ADMIN")
public class CinemaRoomController {
  private CinemaRoomService cinemaRoomService;

  @Operation(summary = "Add a new cinema room.")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CinemaRoomResponse addCinemaRoom(@Valid @RequestBody CinemaRoomRequest cinemaRoomRequest) {
    return cinemaRoomService.addCinemaRoom(cinemaRoomRequest);
  }

  @Operation(summary = "Get cinema room by ID.")
  @GetMapping("/{cinemaRoomId}")
  @ResponseStatus(HttpStatus.OK)
  public CinemaRoomResponse getCinemaRoom(@PathVariable Integer cinemaRoomId) {
    return cinemaRoomService.getCinemaRoomById(cinemaRoomId);
  }

  @Operation(summary = "Update a cinema room.")
  @PutMapping("/{cinemaRoomId}")
  @ResponseStatus(HttpStatus.OK)
  public CinemaRoomResponse updateCinemaRoom(
          @Valid @RequestBody CinemaRoomRequest cinemaRoomRequest,
          @PathVariable Integer cinemaRoomId) {
    return cinemaRoomService.updateCinemaRoom(cinemaRoomRequest, cinemaRoomId);
  }

  @Operation(summary = "Get all cinema rooms")
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<CinemaRoomResponse> getAll() {
    return cinemaRoomService.getAllCinemaRooms();
  }
}
