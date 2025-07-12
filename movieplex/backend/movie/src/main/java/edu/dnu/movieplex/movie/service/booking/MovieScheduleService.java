package edu.dnu.movieplex.movie.service.booking;

import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleRequest;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleResponse;
import edu.dnu.movieplex.movie.domain.mapper.MovieScheduleMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieSchedule;
import edu.dnu.movieplex.movie.persistance.repository.CinemaRoomRepository;
import edu.dnu.movieplex.movie.persistance.repository.MovieRepository;
import edu.dnu.movieplex.movie.persistance.repository.MovieScheduleRepository;
import edu.dnu.movieplex.movie.service.exception.CinemaRoomNotFoundException;
import edu.dnu.movieplex.movie.service.exception.MovieNotFoundException;
import edu.dnu.movieplex.movie.service.exception.MovieScheduleNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling operations related to movie schedules.
 */
@Service
@AllArgsConstructor
public class MovieScheduleService {
  private final MovieScheduleRepository movieScheduleRepository;
  private final MovieScheduleMapper movieScheduleMapper;
  private final MovieRepository movieRepository;
  private final CinemaRoomRepository cinemaRoomRepository;

  /**
  * Retrieves a movie schedule by its id, including associated booked tickets.
  *
  * @param movieScheduleId The unique identifier of the movie schedule.
  * @return A {@link MovieScheduleResponse} representing the movie schedule.
  * @throws MovieScheduleNotFoundException If the movie schedule with the specified id is not found.
  */
  public MovieScheduleResponse getMovieScheduleById(Integer movieScheduleId) {
    MovieSchedule movie = movieScheduleRepository
            .findMovieScheduleByIdFetchTicketsWithStatusBookedOrTemporaryBooked(movieScheduleId)
                .orElseThrow(() ->
                        new MovieScheduleNotFoundException("MovieSchedule by id "
                                + movieScheduleId + " not found"));
    return movieScheduleMapper.entityToResponse(movie);
  }

  /**
  * Retrieves all movie schedules.
  *
  * @return A list of {@link MovieScheduleResponse} representing all movie schedules.
  */
  public List<MovieScheduleResponse> getAllMovieSchedules() {
    List<MovieSchedule> movieScheduleList = movieScheduleRepository.findAll();
    return movieScheduleList
                .stream()
                .map(movieScheduleMapper::entityToResponse)
                .toList();
  }

  /**
  * Retrieves movie schedules for a specific movie and date.
  *
  * @param movieId The id of the movie.
  * @param date    The date for which movie schedules are to be retrieved.
  * @return A list of {@link MovieScheduleBriefResponse} representing movie schedules.
  */
  public List<MovieScheduleBriefResponse> getMovieSchedulesByMovieAndDate(
          Integer movieId, LocalDate date) {
    return movieScheduleRepository.findAllMovieBriefScheduleByMovieIdAndDate(movieId, date);
  }

  /**
  * Adds a new movie schedule to the system.
  *
  * @param movieScheduleRequest The request containing details about the new movie schedule.
  * @param movieId              The unique identifier of the associated movie.
  * @return A {@link MovieScheduleResponse} representing the newly added movie schedule.
  * @throws MovieNotFoundException      If the movie with the specified id is not found.
  * @throws CinemaRoomNotFoundException If the cinema room with the specified id is not found.
  */
  public MovieScheduleResponse addMovieSchedule(MovieScheduleRequest movieScheduleRequest,
                                                Integer movieId) {
    Movie movie = movieRepository.findById(movieId)
        .orElseThrow(() -> new MovieNotFoundException("Movie by id " + movieId + " not found"));
    MovieSchedule movieScheduleEntity = movieScheduleMapper.requestToEntity(movieScheduleRequest);
    movieScheduleEntity.setCinemaRoom(
            cinemaRoomRepository.findById(movieScheduleRequest.cinemaRoomId())
            .orElseThrow(() -> new CinemaRoomNotFoundException("Cinema Room by id "
                    + movieScheduleRequest.cinemaRoomId() + " not found")));
    movieScheduleEntity.assignMovie(movie);
    movieScheduleEntity.setTicketPrice(movieScheduleEntity.getMovieFormat()
            .getAdditionalCost().add(movie.getBasePrice()));
    movieScheduleEntity = movieScheduleRepository.save(movieScheduleEntity);
    return movieScheduleMapper.entityToResponse(movieScheduleEntity);
  }

  /**
  * Updates an existing movie schedule.
  *
  * @param movieScheduleRequest The request containing updated details about the movie schedule.
  * @param movieScheduleId      The unique identifier of the movie schedule to be updated.
  * @return A {@link MovieScheduleResponse} representing the updated movie schedule.
  * @throws MovieScheduleNotFoundException If the movie schedule with the specified id is not found.
  * @throws CinemaRoomNotFoundException    If the cinema room with the specified id is not found.
  */
  @Transactional
  public MovieScheduleResponse updateMovieSchedule(MovieScheduleRequest movieScheduleRequest,
                                                   Integer movieScheduleId) {
    MovieSchedule movieSchedule = movieScheduleRepository.findById(movieScheduleId)
                .orElseThrow(() ->
                        new MovieScheduleNotFoundException("MovieSchedule by id "
                                + movieScheduleId + " not found"));
    Movie movie = movieSchedule.getMovie();
    movieSchedule.setCinemaRoom(cinemaRoomRepository.findById(movieScheduleRequest.cinemaRoomId())
            .orElseThrow(() ->
                    new CinemaRoomNotFoundException("Cinema Room by id "
                            + movieScheduleRequest.cinemaRoomId() + " not found")));
    movieScheduleMapper.update(movieSchedule, movieScheduleRequest);
    movieSchedule.setTicketPrice(movieSchedule.getMovieFormat()
            .getAdditionalCost().add(movie.getBasePrice()));
    return movieScheduleMapper.entityToResponse(movieSchedule);
  }
}
