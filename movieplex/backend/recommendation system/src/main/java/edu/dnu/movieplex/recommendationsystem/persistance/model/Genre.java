package edu.dnu.movieplex.recommendationsystem.persistance.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Genre")
@AllArgsConstructor
@Getter
@Setter
public class Genre {
  @Id
  @GeneratedValue
  private Long id;
  private String title;
}
