package edu.dnu.movieplex.user.persistance.repository;

import edu.dnu.movieplex.user.persistance.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserByEmail(String email);
}
