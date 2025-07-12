package edu.dnu.movieplex.movie.service.movie;

import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.domain.mapper.MovieReviewMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieReview;
import edu.dnu.movieplex.movie.persistance.repository.MovieRepository;
import edu.dnu.movieplex.movie.persistance.repository.MovieReviewRepository;
import edu.dnu.movieplex.movie.service.exception.MovieNotFoundException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service class responsible for managing movie reviews.
 */
@AllArgsConstructor
@Service
public class MovieReviewService {
  private final MovieReviewRepository movieReviewRepository;
  private final MovieReviewMapper movieReviewMapper;
  private final MovieRepository movieRepository;

  /**
  * Adds a new movie review for a specified movie and user.
  *
  * @param movieReviewContext The context containing review details and rating.
  * @param userId The id of the user submitting the review.
  * @param movieId The id of the movie for which the review is submitted.
  * @return A {@link MovieReviewContext} containing details of the added review.
  * @throws MovieNotFoundException if the movie with the given ID is not found.
  */
  public MovieReviewContext addMovieReview(MovieReviewContext movieReviewContext,
                                           UUID userId, Integer movieId) {
    Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new MovieNotFoundException("Movie by id " + movieId + " not found"));
    MovieReview newMovieReview = movieReviewMapper.mapToEntity(movieReviewContext);
    newMovieReview.setUserId(userId);
    newMovieReview.assignMovie(movie);

    return movieReviewMapper.mapToDto(movieReviewRepository.save(newMovieReview));
  }

  /**
  * Retrieves all movie reviews for a specified movie using pagination.
  *
  * @param pageable The pagination information.
  * @param movieId The unique identifier of the movie for which reviews are requested.
  * @return A {@link Page} of {@link MovieReviewContext} containing reviews for the specified movie.
  * @throws MovieNotFoundException if the movie with the given ID is not found.
  */
  public Page<MovieReviewContext> getAllMovieReview(Pageable pageable, Integer movieId) {
    if (!movieRepository.existsById(movieId)) {
      throw new MovieNotFoundException("Movie by id " + movieId + " not found");
    }
    return movieReviewRepository.findAllByPageableAsDto(movieId, pageable);
  }
}
