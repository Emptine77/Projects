package edu.dnu.movieplex.movie.service.booking;

import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import java.util.List;

/**
 * An interface defining booking-related operations for movie tickets.
 */
public interface BookingService {
  List<TicketResponse> getTicketsByIds(List<Integer> ticketIds);

  void setTicketsToBookedByIds(List<Integer> ticketIds);
}
