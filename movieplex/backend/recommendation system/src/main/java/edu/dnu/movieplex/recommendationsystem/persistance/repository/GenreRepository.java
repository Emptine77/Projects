package edu.dnu.movieplex.recommendationsystem.persistance.repository;

import edu.dnu.movieplex.recommendationsystem.persistance.model.Genre;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends Neo4jRepository<Genre, Long> {
  Optional<Genre> findGenreByTitle(String title);
  void deleteAllByTitle(String title);
  boolean existsByTitle(String title);
}
