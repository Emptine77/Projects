package edu.dnu.movieplex.movie.service.booking;

import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link BookingService} interface providing
 * methods for managing movie ticket bookings.
 */
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
  private final TicketService ticketService;

  @Override
  public List<TicketResponse> getTicketsByIds(List<Integer> ticketIds) {
    return ticketService.getTicketsByIds(ticketIds);
  }

  @Override
  public void setTicketsToBookedByIds(List<Integer> ticketIds) {
    ticketService.setTicketsStatusToBookedByIds(ticketIds);
  }
}
