package edu.dnu.movieplex.movie.persistance.repository;


import edu.dnu.movieplex.movie.persistance.model.entity.TicketRefundCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link TicketRefundCode} entities.
 */
public interface TicketRefundRepository extends JpaRepository<TicketRefundCode, String> {
}