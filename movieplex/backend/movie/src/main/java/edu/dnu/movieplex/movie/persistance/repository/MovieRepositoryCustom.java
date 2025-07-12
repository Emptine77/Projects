package edu.dnu.movieplex.movie.persistance.repository;

import edu.dnu.movieplex.movie.domain.dto.movie.MovieBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for querying movies.
 */
public interface MovieRepositoryCustom {
  Page<MovieBriefResponse> findAllByFilter(Pageable pageable, MovieFilter filter);
}
