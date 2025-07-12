package edu.dnu.movieplex.recommendationsystem.persistance.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
@Getter
@Setter
@RelationshipProperties
public class Rated {
  @Id @GeneratedValue
  private Long id;
  @Property("rating")
  private Short rating;
  @TargetNode
  private Movie movie;
}
