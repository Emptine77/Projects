package edu.dnu.movieplex.movie.persistance.repository;

import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleBriefResponse;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieSchedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing {@link MovieSchedule} entities.
 */
public interface MovieScheduleRepository extends JpaRepository<MovieSchedule, Integer> {
  @Query("SELECT new edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleBriefResponse( "
          + "m.id, m.startTime, m.movieFormat) "
          + "FROM MovieSchedule m "
          + "WHERE m.movie.id = :movieId AND date(m.startTime) = :date")
  List<MovieScheduleBriefResponse> findAllMovieBriefScheduleByMovieIdAndDate(
          Integer movieId, LocalDate date);

  @Query("SELECT DISTINCT m FROM MovieSchedule m "
          + "LEFT JOIN FETCH m.tickets t "
          + "WHERE m.id = :id "
          + "AND (t.ticketStatus = 'BOOKED' OR t.ticketStatus = 'TEMPORARY_BOOKED')")
  Optional<MovieSchedule> findMovieScheduleByIdFetchTicketsWithStatusBookedOrTemporaryBooked(
          @Param("id") Integer id);
}
