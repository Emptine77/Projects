package edu.dnu.movieplex.movie.persistance.repository;


import edu.dnu.movieplex.movie.persistance.model.entity.CinemaRoom;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieSchedule;
import edu.dnu.movieplex.movie.persistance.model.entity.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import util.TestDataGenerator;

import java.util.List;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private  CinemaRoomRepository cinemaRoomRepository;


    @Test
    public void testFindAllByUserIdFetchMovieSchedule() {
        // Given
        UUID userId = UUID.randomUUID();
        Movie movie = TestDataGenerator.createValidMovie();
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        MovieSchedule movieSchedule = TestDataGenerator.createValidMovieSchedule(cinemaRoom, movie); // Создайте MovieSchedule с нужными данными
        movie.addMovieSchedule(movieSchedule);
        cinemaRoomRepository.save(cinemaRoom);
        movieRepository.save(movie);
        Ticket ticket = TestDataGenerator.createValidTicket(userId, movieSchedule);
        ticketRepository.save(ticket);
        // When
        List<Ticket> result = ticketRepository.findAllByUserIdFetchMovieSchedule(userId);

        // Then
        Assertions.assertEquals(1, result.size());
        Ticket retrievedTicket = result.get(0);
        Assertions.assertNotNull(retrievedTicket.getMovieSchedule());
    }
}
