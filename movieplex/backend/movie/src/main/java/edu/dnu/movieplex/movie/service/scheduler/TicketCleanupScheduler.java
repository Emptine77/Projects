package edu.dnu.movieplex.movie.service.scheduler;

import edu.dnu.movieplex.movie.persistance.repository.TicketRepository;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A scheduled task for cleaning up expired tickets.
 */
@Slf4j
@Component
public class TicketCleanupScheduler {
  private static final int UPDATE_STATUS_FIXED_RATE = 6;
  @Value("${ticket.expiration-minutes}")
  private static int ticketExpirationMinutes;
  @Autowired
  private TicketRepository ticketRepository;

  /**
  * A scheduled method to update the status of expired tickets to 'EXPIRED'.
  */
  @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = UPDATE_STATUS_FIXED_RATE)
  public void updateStatusToExpiredForExpiredTickets() {
    LocalDateTime currentTime = LocalDateTime.now();
    LocalDateTime thresholdTime = currentTime.minusMinutes(ticketExpirationMinutes);
    ticketRepository.updateStatusToExpiredForExpiredTickets(thresholdTime);
    log.debug("Expired tickets were soft deleted");
  }
}
