package edu.dnu.movieplex.movie.domain.mapper;

import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRefundDto;
import edu.dnu.movieplex.movie.persistance.model.entity.TicketRefundCode;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting {@link TicketRefundCode} entities to DTOs and vice versa.
 * Uses a {@link TicketMapper} to handle mapping of associated entities.
 */
@Mapper(componentModel = "spring", uses = TicketMapper.class)
public interface TicketRefundMapper {
  TicketRefundDto entityToResponse(TicketRefundCode ticketRefundCode);
}
