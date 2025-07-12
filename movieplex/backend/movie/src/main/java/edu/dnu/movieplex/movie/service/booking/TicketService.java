package edu.dnu.movieplex.movie.service.booking;

import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRefundDto;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRequest;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketsGroupedBySchedule;
import edu.dnu.movieplex.movie.domain.mapper.MovieMapper;
import edu.dnu.movieplex.movie.domain.mapper.TicketMapper;
import edu.dnu.movieplex.movie.domain.mapper.TicketRefundMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieSchedule;
import edu.dnu.movieplex.movie.persistance.model.entity.Ticket;
import edu.dnu.movieplex.movie.persistance.model.entity.TicketRefundCode;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import edu.dnu.movieplex.movie.persistance.repository.MovieScheduleRepository;
import edu.dnu.movieplex.movie.persistance.repository.TicketRefundRepository;
import edu.dnu.movieplex.movie.persistance.repository.TicketRepository;
import edu.dnu.movieplex.movie.service.exception.IllegalRefundCodeStatusException;
import edu.dnu.movieplex.movie.service.exception.MovieScheduleNotFoundException;
import edu.dnu.movieplex.movie.service.exception.TicketNotFoundException;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

/**
 * Service class providing methods for managing movie tickets, including retrieval,
 * booking, refund, and retrieval of ticket-related information.
 */
@Service
@AllArgsConstructor
public class TicketService {
  private final TicketRepository ticketRepository;
  private final TicketMapper ticketMapper;
  private final TicketRefundMapper ticketRefundMapper;
  private final MovieScheduleRepository movieScheduleRepository;
  private final TicketRefundRepository ticketRefundRepository;
  private final MovieMapper movieMapper;

  public TicketResponse getTicketById(Integer ticketId) {
    Ticket movie = getTicketEntityById(ticketId);
    return ticketMapper.entityToResponse(movie);
  }

  /**
  * Retrieves all tickets grouped by movie schedule for a specific user.
  *
  * @param userId The unique identifier of the user.
  * @return List of {@link TicketsGroupedBySchedule} representing tickets grouped by movie schedule.
  */
  public List<TicketsGroupedBySchedule> getAllTickets(UUID userId) {
    List<Ticket> ticketList = ticketRepository.findAllByUserIdFetchMovieSchedule(userId);

    return ticketList.stream()
            .collect(Collectors.groupingBy(Ticket::getMovieSchedule))
            .entrySet().stream()
            .map(entry -> {
              MovieSchedule movieSchedule = entry.getKey();
              List<Ticket> tickets = entry.getValue();

              return new TicketsGroupedBySchedule(
                movieMapper.entityToBriefResponse(movieSchedule.getMovie()),
                movieSchedule.getStartTime(),
                movieSchedule.getCinemaRoom().getName(),
                tickets.stream()
                .map(ticketMapper::mapEntityToScheduleTicketDto)
                .toList());
            })
            .collect(Collectors.toList());
  }

  /**
  * Retrieves a list of tickets based on their unique identifiers.
  *
  * @param ticketIds List of ticket identifiers to retrieve.
  * @return List of {@link TicketResponse} objects representing the requested tickets.
  */
  public List<TicketResponse> getTicketsByIds(List<Integer> ticketIds) {
    return ticketRepository.findAllByIdIn(ticketIds)
        .stream()
        .map(ticketMapper::entityToResponse)
        .toList();
  }

  /**
  * Sets the status of tickets with the specified IDs to "Booked."
  *
  * @param ticketIds List of unique identifiers of tickets to be set to "Booked" status.
  * @throws TicketNotFoundException if any of the specified ticket IDs is not found.
  */
  @Transactional
  public void setTicketsStatusToBookedByIds(List<Integer> ticketIds) {
    ticketRepository.findAllByIdIn(ticketIds)
        .forEach(Ticket::setStatusToBooked);
  }

  /**
  * Creates and saves a list of movie tickets based on the provided ticket requests.
  *
  * @param ticketRequests List of {@link TicketRequest}.
  * @param userId         The id of the user for whom the tickets are being created.
  * @param movieScheduleId The id of the movie schedule for which the tickets are being created.
  * @return List of {@link TicketResponse} representing the created tickets.
  * @throws MovieScheduleNotFoundException if the movie schedule with the specified ID is not found.
  */
  public List<TicketResponse> createTickets(List<TicketRequest> ticketRequests, UUID userId,
                                            Integer movieScheduleId) {
    MovieSchedule movieSchedule = movieScheduleRepository.findById(movieScheduleId).orElseThrow(
        () -> new MovieScheduleNotFoundException(
                "MovieSchedule by id " + movieScheduleId + " not found"));
    List<Ticket> tickets = ticketMapper.mapTicketRequestsToTickets(ticketRequests);
    tickets.forEach(ticket -> {
      ticket.setUserId(userId);
      ticket.setPurchaseTime(LocalDateTime.now());
      ticket.setPrice(movieSchedule.getTicketPrice());
      ticket.setTicketStatus(TicketStatus.TEMPORARY_BOOKED);
    });

    movieSchedule.addTickets(tickets);
    return ticketRepository.saveAll(tickets).stream()
            .map(ticketMapper::entityToResponse)
            .toList();
  }

  /**
  * Initiates the refund process for a ticket with the specified ID.
  *
  * @param ticketId The unique identifier of the ticket to be refunded.
  * @return The generated refund code as a {@link UUID}.
  * @throws IllegalRefundCodeStatusException if the ticket refund code has expired.
  * @throws TicketNotFoundException          if the ticket with the specified ID is not found.
  */
  @Transactional
  public UUID refundTicket(Integer ticketId) {
    Ticket ticket = getTicketEntityById(ticketId);
    MovieSchedule movieSchedule = ticket.getMovieSchedule();
    LocalDateTime currentTime = LocalDateTime.now();
    if (currentTime.isBefore(movieSchedule.getStartTime())) {
      TicketRefundCode ticketRefundCode = new TicketRefundCode();
      UUID refundCode = UUID.randomUUID();
      ticketRefundCode.setExpirationDate(movieSchedule.getMovie().getRentalPeriodEnd());
      ticketRefundCode.setRefundCode(hashBlake2(refundCode));
      ticket.setTicketStatus(TicketStatus.CANCELED);
      ticket.assignTicketRefundCode(ticketRefundCode);
      return refundCode;
    } else {
      throw new IllegalRefundCodeStatusException("Ticket Refund code was expired");
    }
  }

  /**
  * Retrieves information about a ticket refund based on the provided refund code.
  *
  * @param uuid The refund code as a {@link UUID}.
  * @return The {@link TicketRefundDto} representing the ticket refund details.
  */
  public TicketRefundDto getTicketRefund(UUID uuid) {
    Blake2bDigest blake2bDigest = new Blake2bDigest(256); // 256-bit hash size
    byte[] uuidBytes = uuid.toString().getBytes(StandardCharsets.UTF_8);
    blake2bDigest.update(uuidBytes, 0, uuidBytes.length);
    byte[] hash = new byte[blake2bDigest.getDigestSize()];
    blake2bDigest.doFinal(hash, 0);
    String hashingUuid = Hex.toHexString(hash);
    TicketRefundCode ticketRefund = ticketRefundRepository.findById(hashingUuid)
            .orElseThrow(() -> new TicketNotFoundException("Ticket by id " + uuid + " not found"));
    return ticketRefundMapper.entityToResponse(ticketRefund);
  }

  // Method to hash a UUID using Blake2
  private String hashBlake2(UUID uuid) {
    Blake2bDigest blake2bDigest = new Blake2bDigest(256); // 256-bit hash size
    byte[] uuidBytes = uuid.toString().getBytes(StandardCharsets.UTF_8);
    blake2bDigest.update(uuidBytes, 0, uuidBytes.length);
    byte[] hash = new byte[blake2bDigest.getDigestSize()];
    blake2bDigest.doFinal(hash, 0);
    return Hex.toHexString(hash);
  }

  private Ticket getTicketEntityById(Integer ticketId) {
    return ticketRepository.findById(ticketId)
      .orElseThrow(() ->
        new TicketNotFoundException("Ticket by id " + ticketId + " not found"));
  }
}
