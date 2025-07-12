package edu.dnu.movieplex.movie.persistance.repository;

import edu.dnu.movieplex.movie.persistance.model.entity.CinemaRoom;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link CinemaRoom} entities.
 */
public interface CinemaRoomRepository extends JpaRepository<CinemaRoom, Integer> {

}
