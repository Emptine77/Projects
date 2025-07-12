package edu.dnu.movieplex.movie.persistance.repository;

import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Repository interface for managing {@link MovieReview} entities.
 */
public interface MovieReviewRepository extends JpaRepository<MovieReview, Integer> {
  @Query("SELECT new edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext("
          + "review.rating, review.comment) "
          + "FROM MovieReview review "
          + "WHERE review.movie.id = ?1")
  Page<MovieReviewContext> findAllByPageableAsDto(Integer movieId, Pageable pageable);
}
