package edu.dnu.movieplex.recommendationsystem.domain.mapper;

import edu.dnu.movieplex.recommendationsystem.domain.dto.MovieRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.MovieResponse;
import edu.dnu.movieplex.recommendationsystem.persistance.model.Affiliation;
import edu.dnu.movieplex.recommendationsystem.persistance.model.Movie;
import edu.dnu.movieplex.recommendationsystem.persistance.repository.GenreRepository;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class MovieMapper {
  @Autowired
  private GenreRepository genreRepository;
  public abstract MovieResponse entityToDTO(Movie movie);
  public abstract Movie DTOtoEntity(MovieRequest movieRequest);
  public abstract void updateMovie(@MappingTarget Movie movie, MovieRequest movieRequest);
  List<String> affiliationsToGenreNames(List<Affiliation> affiliations) {
    return Affiliation.getTitlesByAffiliations(affiliations);
  }
  List<Affiliation> genreNamesToAffiliations(List<String> genreNames) {
    return Affiliation.getAffiliationByTitle(genreNames, genreRepository);
  }
}
