package edu.dnu.movieplex.movie.domain.mapper;

import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieReview;
import org.mapstruct.Mapper;

/**
 * Mapper interface responsible for mapping between MovieReview entity and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface MovieReviewMapper {
  MovieReviewContext mapToDto(MovieReview movieReview);

  MovieReview mapToEntity(MovieReviewContext movieReviewContext);
}
