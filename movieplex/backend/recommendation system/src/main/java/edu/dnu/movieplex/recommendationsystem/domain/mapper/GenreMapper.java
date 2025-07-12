package edu.dnu.movieplex.recommendationsystem.domain.mapper;

import edu.dnu.movieplex.recommendationsystem.domain.dto.GenreRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.GenreResponse;
import edu.dnu.movieplex.recommendationsystem.persistance.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {
  GenreResponse entityToDto(Genre genre);
  Genre DtoToEntity(GenreRequest genreRequest);
  void updateGenre(@MappingTarget Genre genre, GenreRequest genreRequest);
}
