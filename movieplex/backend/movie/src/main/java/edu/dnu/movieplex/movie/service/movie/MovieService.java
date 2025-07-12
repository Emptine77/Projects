package edu.dnu.movieplex.movie.service.movie;

import edu.dnu.movieplex.movie.domain.dto.movie.MovieBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieFilter;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieRequest;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieResponse;
import edu.dnu.movieplex.movie.domain.mapper.MovieMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.repository.MovieRepository;
import edu.dnu.movieplex.movie.service.exception.MovieNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing movies.
 */
@Service
@AllArgsConstructor
public class MovieService {
  private final MovieRepository movieRepository;
  private final MovieMapper movieMapper;

  /**
  * Retrieves detailed information about a movie by its unique identifier.
  *
  * @param movieId The unique identifier of the movie.
  * @return A {@link MovieResponse} containing details of the specified movie.
  * @throws MovieNotFoundException if the movie with the given ID is not found.
  */
  public MovieResponse getMovieById(Integer movieId) {
    Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie by id " + movieId + " not found"));
    return movieMapper.entityToResponse(movie);
  }

  /**
  * Retrieves a paginated list of brief movie details based on the provided filter.
  *
  * @param pageable The pagination information.
  * @param filter   The filter criteria for movie retrieval.
  * @return A {@link Page} of {@link MovieBriefResponse} containing brief movie details.
  */
  public Page<MovieBriefResponse> getAllMovies(Pageable pageable, MovieFilter filter) {
    return movieRepository.findAllByFilter(pageable, filter);
  }

  /**
  * Retrieves a paginated list of brief movie details for movies scheduled on a specific date.
  *
  * @param pageable The pagination information.
  * @param date     The date for which scheduled movies are requested.
  * @return A {@link Page} of {@link MovieBriefResponse} containing brief movie details.
  */
  public Page<MovieBriefResponse> getAllMoviesByScheduleDate(Pageable pageable, LocalDate date) {
    Page<Movie> moviePage = movieRepository.findAllByScheduleDate(pageable, date);
    List<MovieBriefResponse> movies = moviePage.stream()
        .map(movieMapper::entityToBriefResponse)
        .toList();
    return new PageImpl<>(movies, moviePage.getPageable(), moviePage.getTotalElements());
  }

  /**
  * Adds a new movie based on the provided details.
  *
  * @param movieRequest The request containing details of the new movie.
  * @return A {@link MovieResponse} containing details of the added movie.
  */
  public MovieResponse addMovie(MovieRequest movieRequest) {
    Movie movieEntity = movieRepository.save(movieMapper.requestToEntity(movieRequest));
    return movieMapper.entityToResponse(movieEntity);
  }

  /**
  * Updates details of an existing movie based on the provided information.
  *
  * @param movieRequest The request containing updated details of the movie.
  * @param movieId      The id of the movie to be updated.
  * @return A {@link MovieResponse} containing details of the updated movie.
  * @throws MovieNotFoundException if the movie with the given ID is not found.
  */
  @Transactional
  public MovieResponse updateMovie(MovieRequest movieRequest, Integer movieId) {
    Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new MovieNotFoundException("Movie by id " + movieId + " not found"));
    movieMapper.update(movie, movieRequest);
    return movieMapper.entityToResponse(movie);
  }
}
