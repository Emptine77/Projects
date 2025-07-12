package edu.dnu.movieplex.recommendationsystem.persistance.repository;

import edu.dnu.movieplex.recommendationsystem.persistance.model.User;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {
  Optional<User> findUserByUserId(String userId);
  boolean existsByName(String name);
}
