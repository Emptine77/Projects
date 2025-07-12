package edu.dnu.movieplex.movie.domain.mapper;

import edu.dnu.movieplex.movie.domain.dto.ticket.ScheduleTicketDto;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRequest;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.persistance.model.entity.Ticket;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


/**
 * Mapper interface for converting between different representations of entity Ticket.
 */
@Mapper(componentModel = "spring")
public interface TicketMapper {
  TicketResponse entityToResponse(Ticket ticket);

  ScheduleTicketDto mapEntityToScheduleTicketDto(Ticket ticket);

  Ticket requestToEntity(TicketRequest ticketRequest);

  void update(@MappingTarget Ticket ticket, TicketRequest ticketRequest);

  List<Ticket> mapTicketRequestsToTickets(List<TicketRequest> ticketRequests);

  default String mapTicketStatusToTicketStatusName(TicketStatus ticketStatus) {
    return ticketStatus.getName();
  }
}
