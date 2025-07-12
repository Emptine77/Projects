package edu.dnu.movieplex.movie.service.booking;

import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRequest;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.domain.mapper.TicketMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieSchedule;
import edu.dnu.movieplex.movie.persistance.model.entity.Ticket;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import edu.dnu.movieplex.movie.persistance.repository.MovieScheduleRepository;
import edu.dnu.movieplex.movie.persistance.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;




@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @Mock
    TicketRepository ticketRepository;
    @Mock
    TicketMapper ticketMapper;
    @Mock
    MovieScheduleRepository movieScheduleRepository;
    @InjectMocks
    TicketService ticketService;
    @Test
    void testGetTicketById() {
        // Arrange
        Integer ticketId = 1;
        Ticket ticket = new Ticket(null, null, 5, 5, null, ticketId, null, null, null);
        TicketResponse expectedResponse = new TicketResponse(ticketId, null, null, null, 5, 5);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketMapper.entityToResponse(ticket)).thenReturn(expectedResponse);

        // Act
        TicketResponse result = ticketService.getTicketById(ticketId);

        // Assert
        assertEquals(expectedResponse, result);
    }
    @Test
    void testCreateTickets() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Integer movieScheduleId = 1;
        List<TicketRequest> ticketRequests = Arrays.asList(
                new TicketRequest(1, 1),
                new TicketRequest(2, 2)
        );

        MovieSchedule movieSchedule = new MovieSchedule();
        movieSchedule.setTicketPrice(BigDecimal.TEN);

        when(movieScheduleRepository.findById(movieScheduleId)).thenReturn(java.util.Optional.of(movieSchedule));
        when(ticketMapper.mapTicketRequestsToTickets(ticketRequests)).thenReturn(Arrays.asList(new Ticket(), new Ticket()));
        when(ticketRepository.saveAll(anyList())).thenReturn(Arrays.asList(new Ticket(), new Ticket()));
        when(ticketMapper.entityToResponse(any())).thenReturn(new TicketResponse(movieScheduleId, null, null, null, 2, 2)); // Set a sample response

        // Act
        List<TicketResponse> result = ticketService.createTickets(ticketRequests, userId, movieScheduleId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSetTicketsStatusToBookedByIds() {
        // Arrange
        List<Integer> ticketIds = Arrays.asList(1, 2, 3);
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket(), new Ticket());

        when(ticketRepository.findAllByIdIn(ticketIds)).thenReturn(tickets);

        // Act
        ticketService.setTicketsStatusToBookedByIds(ticketIds);

        // Assert
        verify(ticketRepository).findAllByIdIn(ticketIds);
        tickets.forEach(ticket -> assertEquals(TicketStatus.BOOKED, ticket.getTicketStatus()));
    }
}
