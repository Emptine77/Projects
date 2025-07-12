package util;

import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomRequest;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomResponse;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieRequest;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.domain.dto.schedule.BookedSeat;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleRequest;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleResponse;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.persistance.model.entity.*;
import edu.dnu.movieplex.movie.persistance.model.enums.Genre;
import edu.dnu.movieplex.movie.persistance.model.enums.MovieFormat;
import edu.dnu.movieplex.movie.persistance.model.enums.RoomType;
import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestDataGenerator {
    public static Movie createValidMovie() {
        return Movie.builder()
                .description("testDescription")
                .title("testTitle")
                .director("director")
                .genres(List.of(Genre.ACTION))
                .duration(10)
                .rating(Short.valueOf("10"))
                .ageLimit(10)
                .basePrice(BigDecimal.ONE)
                .language("ENG")
                .countryOfProduction("USA")
                .movieSchedules(new ArrayList<>())
                .realiseDate(LocalDate.now().plusDays(1))
                .rentalPeriodStart(LocalDate.now())
                .rentalPeriodEnd(LocalDate.now().plusDays(1))
                .build();
    }

    public static MovieRequest createValidMovieRequest() {
        return new MovieRequest("Movie Title", LocalDate.now(), 120, Short.valueOf("10"), "Description", 18,
                "USA", "Director", LocalDate.now(), LocalDate.now().plusDays(7),
                "English", new BigDecimal("10.00"), List.of("Action", "Drama"),
                List.of("Production Company"));
    }

    public static MovieReviewContext createValidMovieReviewDTO() {
        return new MovieReviewContext((short) 5, "great");
    }

    public static MovieReview createValidMovieReview(Movie movie) {
        MovieReview movieReview = new MovieReview();
        movieReview.setMovie(movie);
        movieReview.setComment("Great");
        movieReview.setRating((short) 5);
        movieReview.setUserId(UUID.randomUUID());
        return movieReview;
    }

    public static CinemaRoom createValidCinemaRoom() {
        return new CinemaRoom(1, "test", 5, 5, null, RoomType.VIP);
    }

    public static CinemaRoomRequest createValidCinemaRoomRequest() {
        return new CinemaRoomRequest("test", 5, 5, "VIP");
    }

    public static CinemaRoomResponse createValidCinemaRoomResponseEx1() {
        return new CinemaRoomResponse(1, "test", 5, 5, "VIP");
    }

    public static CinemaRoomResponse createValidCinemaRoomResponseEx2() {
        return new CinemaRoomResponse(2, "test2", 5, 5, "VIP");
    }

    public static MovieScheduleRequest createValidMovieScheduleRequest() {
        return new MovieScheduleRequest(LocalDateTime.of(2024, Month.FEBRUARY,10,5,4), MovieFormat.IMAX,1);
    }

    public static MovieScheduleResponse createValidMovieScheduleResponse() {
        CinemaRoomResponse cinemaRoom;
        List<BookedSeat> bookedSeat = new ArrayList<>();
        return new MovieScheduleResponse(1,LocalDateTime.of(2024, Month.FEBRUARY,10,5,4),MovieFormat.IMAX, BigDecimal.valueOf(10), cinemaRoom = createValidCinemaRoomResponseEx1(), bookedSeat);
    }

    public static MovieSchedule createValidMovieSchedule(CinemaRoom cinemaRoom, Movie movie) {
        MovieSchedule movieSchedule = new MovieSchedule();
        movieSchedule.setCinemaRoom(cinemaRoom); // Set the cinema room id as needed
        movieSchedule.setMovieFormat(MovieFormat.IMAX); // Set the movie format as needed
        movieSchedule.setStartTime(LocalDateTime.now()); // Set the start datetime as needed
        movieSchedule.setTicketPrice(BigDecimal.valueOf(10.0)); // Set the ticket price as needed
        movieSchedule.assignMovie(movie);
        return movieSchedule;
    }
    public  static TicketResponse createValidTicketResponse(){
        return new TicketResponse(1,"booked",BigDecimal.valueOf(10),LocalDateTime.now(),5,5);
    }

    public  static  Ticket createValidTicket(UUID userID, MovieSchedule movieSchedule){
        return new Ticket(userID, LocalDateTime.of(2024, Month.FEBRUARY,10,5,4),5,5, BigDecimal.valueOf(10),1, TicketStatus.BOOKED, movieSchedule, null);
    }
}
