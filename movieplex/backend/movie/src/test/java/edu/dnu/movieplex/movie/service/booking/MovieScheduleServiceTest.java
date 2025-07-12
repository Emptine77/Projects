package edu.dnu.movieplex.movie.service.booking;


import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleRequest;
import edu.dnu.movieplex.movie.domain.dto.schedule.MovieScheduleResponse;
import edu.dnu.movieplex.movie.domain.mapper.MovieScheduleMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.CinemaRoom;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieSchedule;
import edu.dnu.movieplex.movie.persistance.model.enums.MovieFormat;
import edu.dnu.movieplex.movie.persistance.repository.CinemaRoomRepository;
import edu.dnu.movieplex.movie.persistance.repository.MovieRepository;
import edu.dnu.movieplex.movie.persistance.repository.MovieScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.TestDataGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieScheduleServiceTest {
    @Mock
    private MovieScheduleRepository movieScheduleRepository;

    @Mock
    private MovieScheduleMapper movieScheduleMapper;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CinemaRoomRepository cinemaRoomRepository;

    @InjectMocks
    private MovieScheduleService movieScheduleService;

    @Test
    void getMovieScheduleById() {
        //GIVEN
        Integer movieScheduleId = 1;
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        Movie movie = TestDataGenerator.createValidMovie();
        MovieSchedule movieSchedule = TestDataGenerator.createValidMovieSchedule(cinemaRoom,movie);
        MovieScheduleResponse movieScheduleResponse = TestDataGenerator.createValidMovieScheduleResponse();
        // WHEN
        when(movieScheduleRepository.findMovieScheduleByIdFetchTicketsWithStatusBookedOrTemporaryBooked(movieScheduleId)).thenReturn(Optional.of(movieSchedule));
        when(movieScheduleMapper.entityToResponse(movieSchedule)).thenReturn(movieScheduleResponse);
        MovieScheduleResponse actualResponse = movieScheduleService.getMovieScheduleById(movieScheduleId);
        //THEN
        Assertions.assertEquals(movieScheduleResponse, actualResponse);
    }

    @Test
    void getAllMovieSchedules() {
        //GIVEN
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        Movie movie = TestDataGenerator.createValidMovie();
        MovieSchedule movieSchedule = TestDataGenerator.createValidMovieSchedule(cinemaRoom,movie);
        MovieScheduleResponse movieScheduleResponse = TestDataGenerator.createValidMovieScheduleResponse();
        List<MovieSchedule> movieScheduleList = List.of(movieSchedule);
        List<MovieScheduleResponse> movieScheduleResponseList = List.of(movieScheduleResponse);
        // WHEN
        when(movieScheduleRepository.findAll()).thenReturn(movieScheduleList);
        when(movieScheduleMapper.entityToResponse(any())).thenReturn(movieScheduleResponse);
        List<MovieScheduleResponse> actualResponse = movieScheduleService.getAllMovieSchedules();
        // THEN
        Assertions.assertEquals(movieScheduleResponseList, actualResponse);
    }

    @Test
    void getMovieSchedulesByMovieAndDate() {
        Integer movieId = 1;
        LocalDate date = LocalDate.now();
        MovieScheduleBriefResponse movieScheduleBriefResponse = new MovieScheduleBriefResponse(1,
            LocalTime.now(), MovieFormat.IMAX);
        List<MovieScheduleBriefResponse> movieScheduleBriefResponses = List.of(movieScheduleBriefResponse);
        //WHEN
        when(movieScheduleRepository.findAllMovieBriefScheduleByMovieIdAndDate(movieId,date)).thenReturn(movieScheduleBriefResponses);
        List<MovieScheduleBriefResponse> actualMovieScheduleBriefResponses = movieScheduleService.getMovieSchedulesByMovieAndDate(movieId,date);
        //THEN
        Assertions.assertEquals(movieScheduleBriefResponses,actualMovieScheduleBriefResponses);

    }

    @Test
    void addMovieSchedule() {
        //GIVEN
        MovieScheduleRequest movieScheduleRequest = TestDataGenerator.createValidMovieScheduleRequest();
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        Movie movieEntity = TestDataGenerator.createValidMovie();
        MovieSchedule movieSchedule = TestDataGenerator.createValidMovieSchedule(cinemaRoom, movieEntity);
        MovieScheduleResponse expectedResponse = TestDataGenerator.createValidMovieScheduleResponse();
        // WHEN
        when(movieRepository.findById(1)).thenReturn(Optional.ofNullable(movieEntity));
        when(cinemaRoomRepository.findById(1)).thenReturn(Optional.of(cinemaRoom));
        when(movieScheduleMapper.requestToEntity(movieScheduleRequest)).thenReturn(movieSchedule);
        when(movieScheduleRepository.save(movieSchedule)).thenReturn(movieSchedule);
        when(movieScheduleMapper.entityToResponse(movieSchedule)).thenReturn(expectedResponse);
        MovieScheduleResponse actualResponse = movieScheduleService.addMovieSchedule(movieScheduleRequest, 1);
        //THEN
        Assertions.assertEquals(expectedResponse,actualResponse);
    }


    @Test
    void updateMovieSchedule() {
        //GIVEN
        Integer movieScheduleId = 1;
        MovieScheduleRequest movieScheduleUpdateRequest = TestDataGenerator.createValidMovieScheduleRequest();
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        Movie movieEntity = TestDataGenerator.createValidMovie();
        MovieSchedule movieSchedule = TestDataGenerator.createValidMovieSchedule(cinemaRoom, movieEntity);
        MovieScheduleResponse expectedResponse = TestDataGenerator.createValidMovieScheduleResponse();
        //WHEN
        when(cinemaRoomRepository.findById(1)).thenReturn(Optional.of(cinemaRoom));
        when(movieScheduleRepository.findById(movieScheduleId)).thenReturn(Optional.of(movieSchedule));
        doNothing().when(movieScheduleMapper).update(movieSchedule, movieScheduleUpdateRequest);
        when(movieScheduleMapper.entityToResponse(movieSchedule)).thenReturn(expectedResponse);
        MovieScheduleResponse actualResponse = movieScheduleService.updateMovieSchedule(movieScheduleUpdateRequest, movieScheduleId);
        //THEN
        Assertions.assertEquals(expectedResponse,actualResponse);
    }
}
