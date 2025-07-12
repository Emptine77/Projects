package edu.dnu.movieplex.movie.persistance.repository;


import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for managing {@link Movie} entities.
 */
public interface MovieRepository extends MovieRepositoryCustom, JpaRepository<Movie, Integer> {
  @Query("SELECT m FROM Movie m "
        + "JOIN MovieSchedule schedule ON m.id = schedule.movie.id "
        + "WHERE date(schedule.startTime) = :date")
  Page<Movie> findAllByScheduleDate(Pageable pageable, LocalDate date);
}