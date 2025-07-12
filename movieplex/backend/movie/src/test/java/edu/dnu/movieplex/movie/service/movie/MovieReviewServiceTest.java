package edu.dnu.movieplex.movie.service.movie;


import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.domain.mapper.MovieReviewMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieReview;
import edu.dnu.movieplex.movie.persistance.repository.MovieRepository;
import edu.dnu.movieplex.movie.persistance.repository.MovieReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import util.TestDataGenerator;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieReviewServiceTest {
    @Mock
    private MovieReviewRepository movieReviewRepository;
    @Mock
    private MovieReviewMapper movieReviewMapper;
    @Mock
    private MovieRepository movieRepository;
    @InjectMocks
    private MovieReviewService movieReviewService;

    @Test
    void addMovieReviewTest() {
        // GIVEN
        UUID userId = UUID.randomUUID();
        Integer movieId = 1;
        MovieReviewContext inputDTO = TestDataGenerator.createValidMovieReviewDTO();

        Movie movie = mock(Movie.class);
        MovieReview newMovieReview = mock(MovieReview.class);
        newMovieReview.setUserId(userId);
        newMovieReview.setMovie(movie);
        //WHEN
        when(movieRepository.findById(movieId)).thenReturn(java.util.Optional.of(movie));
        when(movieReviewMapper.mapToEntity(inputDTO)).thenReturn(newMovieReview);
        when(movieReviewRepository.save(newMovieReview)).thenReturn(newMovieReview);
        when(movieReviewMapper.mapToDto(newMovieReview)).thenReturn(inputDTO);

        MovieReviewContext result = movieReviewService.addMovieReview(inputDTO, userId, movieId);

        // THEN
        assertNotNull(result);
        assertEquals(inputDTO, result);
    }

    @Test
    void getAllMovieReviewNotNullTest() {
        // GIVEN
        Integer movieId = 1;
        Pageable pageable = mock(Pageable.class);

        //WHEN
        when(movieRepository.existsById(movieId)).thenReturn(true);
        when(movieReviewRepository.findAllByPageableAsDto(movieId, pageable))
                .thenReturn(mock(Page.class));

        Page<MovieReviewContext> result = movieReviewService.getAllMovieReview(pageable, movieId);

        //THEN
        assertNotNull(result);
    }

}
