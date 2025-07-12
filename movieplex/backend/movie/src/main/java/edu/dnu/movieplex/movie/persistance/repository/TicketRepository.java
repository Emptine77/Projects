package edu.dnu.movieplex.movie.persistance.repository;

import edu.dnu.movieplex.movie.persistance.model.entity.Ticket;
import edu.dnu.movieplex.movie.persistance.model.entity.TicketRefundCode;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository interface for managing {@link TicketRefundCode} entities in the movie catalog.
 */
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
  @Query("SELECT t FROM Ticket t JOIN FETCH MovieSchedule m ON t.movieSchedule = m "
          + "WHERE t.userId = :userId")
  List<Ticket> findAllByUserIdFetchMovieSchedule(UUID userId);

  List<Ticket> findAllByIdIn(List<Integer> ticketIds);

  @Modifying
  @Transactional
  @Query("UPDATE Ticket t SET t.ticketStatus = 'EXPIRED'"
          + "WHERE t.ticketStatus = 'TEMPORARY_BOOKED' AND t.purchaseTime > :thresholdTime")
  void updateStatusToExpiredForExpiredTickets(LocalDateTime thresholdTime);

  @Modifying
  @Query("UPDATE Ticket t SET t.ticketStatus = 'BOOKED' WHERE t.id IN :ticketIds")
  void setTicketsStatusToBookedByIds(@Param("ticketIds") List<Integer> ticketIds);
}
