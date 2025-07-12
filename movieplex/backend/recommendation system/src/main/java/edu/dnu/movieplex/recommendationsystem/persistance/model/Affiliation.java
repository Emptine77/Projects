package edu.dnu.movieplex.recommendationsystem.persistance.model;


import edu.dnu.movieplex.recommendationsystem.persistance.repository.GenreRepository;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Setter
@RelationshipProperties
public class Affiliation {
  @Id @GeneratedValue
  Long id;
  @TargetNode
  Genre genre;
  public static List<Affiliation> getAffiliationByTitle(List<String> genreNames, GenreRepository genreRepository){
    return genreNames.stream()
        .map(title -> {
          Genre genre = genreRepository.findGenreByTitle(title).orElseThrow(()-> new ResponseStatusException(HttpStatusCode.valueOf(404)));
          Affiliation affiliation = new Affiliation();
          affiliation.setGenre(genre);
          return affiliation;
        })
        .toList();
  }
  public static List<String> getTitlesByAffiliations(List<Affiliation> affiliations) {
    return affiliations.stream()
        .map(affiliation -> affiliation.getGenre().getTitle())
        .toList();
  }

}
