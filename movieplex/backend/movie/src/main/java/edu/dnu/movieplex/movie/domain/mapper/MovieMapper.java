package edu.dnu.movieplex.movie.domain.mapper;

import edu.dnu.movieplex.movie.domain.dto.movie.MovieBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieRequest;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieResponse;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.enums.Genre;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


/**
 * Mapper interface responsible for mapping between Movie entity and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface MovieMapper {
  MovieResponse entityToResponse(Movie movie);

  MovieBriefResponse entityToBriefResponse(Movie movie);

  Movie requestToEntity(MovieRequest movieRequest);

  void update(@MappingTarget Movie movie, MovieRequest movieRequest);

  /**
  * Maps a list of genre titles to a list of corresponding genre enums.
  *
  * @param titles The list of genre titles to be mapped.
  * @return The corresponding list of genre enums.
  */
  default List<Genre> mapGenreTitlesToGenres(List<String> titles) {
    return titles.stream()
           .map(Genre::getGenreByTitle)
           .toList();
  }

  /**
  * Maps a list of genre enums to a list of corresponding genre titles.
  *
  * @param genres The list of genre enums to be mapped.
  * @return The corresponding list of genre titles.
  */
  default List<String> mapGenresToGenreTitles(List<Genre> genres) {
    return genres.stream()
        .map(Genre::getTitle)
        .toList();
  }
}
