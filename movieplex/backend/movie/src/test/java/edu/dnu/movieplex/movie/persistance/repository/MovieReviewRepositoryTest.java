package edu.dnu.movieplex.movie.persistance.repository;


import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.TestDataGenerator;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MovieReviewRepositoryTest {
    @Autowired
    private MovieReviewRepository movieReviewRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindAllByPageableAsDTO() {
        // Given
        Movie movie = TestDataGenerator.createValidMovie();

        movieRepository.save(movie);
        entityManager.refresh(movie);

        MovieReview movieReview = new MovieReview();
        movieReview.setUserId(UUID.randomUUID());
        movieReview.setRating((short) 5);
        movieReview.setComment("TestComment");
        movieReview.assignMovie(movie);
        movieReviewRepository.save(movieReview);


        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MovieReviewContext> result = movieReviewRepository.findAllByPageableAsDto(movie.getId(), pageable);

        // Then
        assertEquals(1, result.getContent().size());
        MovieReviewContext retrievedReview = result.getContent().get(0);
        assertEquals(movieReview.getRating().intValue(), retrievedReview.rating().intValue());
        assertEquals(movieReview.getComment(), retrievedReview.comment());
    }
}