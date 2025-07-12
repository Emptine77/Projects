package edu.dnu.movieplex.recommendationsystem.persistance.model;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Movie")
@AllArgsConstructor
@Getter
@Setter
public class Movie {
  @Id @GeneratedValue
  private Long id;
  private String title;
  private Short imdbRating;
  @Relationship(type = "AFFILIATION", direction = Relationship.Direction.OUTGOING)
  private List<Affiliation> genres;
}
