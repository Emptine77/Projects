package edu.dnu.movieplex.movie.domain.mapper;

import edu.dnu.movieplex.movie.domain.dto.schedule.BookedSeat;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleRequest;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleResponse;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieSchedule;
import edu.dnu.movieplex.movie.persistance.model.entity.Ticket;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


/**
 * Mapper interface for converting between different representations of movie schedules.
 */
@Mapper(componentModel = "spring")
public interface MovieScheduleMapper {
  MovieSchedule requestToEntity(MovieScheduleRequest movieSheduleRequest);

  void update(@MappingTarget MovieSchedule movieShedule, MovieScheduleRequest movieSheduleRequest);

  @Mapping(source = "tickets", target = "bookedSeats")
  MovieScheduleResponse entityToResponse(MovieSchedule movieShedule);

  /**
  * Maps a list of {@link Ticket} objects to a list of {@link BookedSeat} objects.
  *
  * @param tickets The list of tickets to be mapped.
  * @return The corresponding list of booked seats.
  */
  default List<BookedSeat> mapTicketsToBookedSeats(List<Ticket> tickets) {
    return tickets
        .stream()
        .map(ticket -> new BookedSeat(ticket.getSeatRow(), ticket.getSeatColumn()))
        .toList();
  }
}