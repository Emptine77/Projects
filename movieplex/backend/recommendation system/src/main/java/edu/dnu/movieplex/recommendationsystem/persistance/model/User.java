package edu.dnu.movieplex.recommendationsystem.persistance.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("User")
@AllArgsConstructor
@Getter
@Setter
public class User {
  @Id @GeneratedValue
  private Long id;
  private String name;
  @GeneratedValue
  private String userId;
  @Relationship(type = "RATED", direction = Relationship.Direction.OUTGOING)
  private List<Rated> ratedList = new ArrayList<>();
  public void rateMovie(Movie movie, Short rating){
    Rated rated = new Rated();
    rated.setRating(rating);
    rated.setMovie(movie);
    ratedList.add(rated);
  }
}
