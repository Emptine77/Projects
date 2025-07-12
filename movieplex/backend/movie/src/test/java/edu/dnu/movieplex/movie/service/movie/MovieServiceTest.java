package edu.dnu.movieplex.movie.service.movie;

import edu.dnu.movieplex.movie.domain.dto.movie.MovieRequest;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieResponse;
import edu.dnu.movieplex.movie.domain.mapper.MovieMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.repository.MovieRepository;
import edu.dnu.movieplex.movie.service.exception.MovieNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.TestDataGenerator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieMapper movieMapper;
    @InjectMocks
    private MovieService movieService;

    @Test
    void testGetMovieById() {
        // GIVEN
        Integer movieId = 1;
        Movie movie = TestDataGenerator.createValidMovie();
        MovieResponse expectedResponse = MovieResponse.builder().build();
        // WHEN
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieMapper.entityToResponse(movie)).thenReturn(expectedResponse);
        MovieResponse actualResponse = movieService.getMovieById(movieId);

        // THEN
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetMovieByIdNotFound() {
        // GIVEN
        Integer movieId = 1;
        // WHEN-THEN
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieById(movieId));
    }

    @Test
    void testAddMovie() {
        //GIVEN
        MovieRequest movieRequest = TestDataGenerator.createValidMovieRequest();
        Movie movieEntity = TestDataGenerator.createValidMovie();
        MovieResponse expectedResponse = MovieResponse.builder().build();
        // WHEN
        when(movieMapper.requestToEntity(movieRequest)).thenReturn(movieEntity);
        when(movieRepository.save(movieEntity)).thenReturn(movieEntity);
        when(movieMapper.entityToResponse(movieEntity)).thenReturn(expectedResponse);
        MovieResponse actualResponse = movieService.addMovie(movieRequest);

        // THEN
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testUpdateMovie() {
        // GIVEN
        Integer movieId = 1;
        MovieRequest movieRequest = TestDataGenerator.createValidMovieRequest();
        Movie existingMovie = TestDataGenerator.createValidMovie();
        MovieResponse expectedResponse = MovieResponse.builder().build();
        // WHEN
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        doNothing().when(movieMapper).update(any(), eq(movieRequest));
        when(movieMapper.entityToResponse(existingMovie)).thenReturn(expectedResponse);
        MovieResponse actualResponse = movieService.updateMovie(movieRequest, movieId);

        // THEN
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testUpdateMovieNotFound() {
        // GIVEN
        Integer movieId = 1;
        // WHEN
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        //THEN
        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(TestDataGenerator.createValidMovieRequest(), movieId));
    }
}
