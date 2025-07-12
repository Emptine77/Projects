package edu.dnu.movieplex.movie.persistance.repository;

import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleResponse;
import edu.dnu.movieplex.movie.persistance.model.entity.CinemaRoom;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieSchedule;
import edu.dnu.movieplex.movie.persistance.model.entity.Ticket;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import util.TestDataGenerator;

@DataJpaTest
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class MovieScheduleRepositoryTest {
  @Autowired
  private  MovieScheduleRepository movieScheduleRepository;
  @Autowired
  private  MovieRepository movieRepository;
  @Autowired
  private  CinemaRoomRepository cinemaRoomRepository;
  @Autowired
  private  TicketRepository ticketRepository;

  @Test
  void findAllMovieBriefScheduleByMovieIdAndDate() {
    //GIVEN
    Movie movie = TestDataGenerator.createValidMovie();
    CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
    MovieSchedule movieSchedule = TestDataGenerator.createValidMovieSchedule(cinemaRoom, movie);
    cinemaRoomRepository.save(cinemaRoom);
    movie = movieRepository.save(movie);
    movieScheduleRepository.save(movieSchedule);
    //WHEN
    List<MovieScheduleBriefResponse> result = movieScheduleRepository
            .findAllMovieBriefScheduleByMovieIdAndDate(movie.getId(), LocalDate.now());
    //THEN
    Assertions.assertEquals(1, result.size());
    MovieScheduleBriefResponse retrievedMovieSchedule = result.get(0);
    Assertions.assertNotNull(retrievedMovieSchedule.movieFormat());
  }
}
