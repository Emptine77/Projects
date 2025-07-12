package edu.dnu.movieplex.movie.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.SecurityConfig;
import edu.dnu.movieplex.common.dto.UserDetails;
import edu.dnu.movieplex.common.security.utils.JwtClaimsExtractor;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieRequest;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import edu.dnu.movieplex.movie.persistance.model.entity.MovieReview;
import edu.dnu.movieplex.movie.persistance.model.enums.Genre;
import edu.dnu.movieplex.movie.persistance.repository.MovieRepository;
import edu.dnu.movieplex.movie.persistance.repository.MovieReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.TestDataGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class MovieControllerIntegrationTest {
    private static final String CUSTOMER_MOVIE_CONTROLLER_URL = "/api/v1/movies";
    private static final String ADMIN_MOVIE_CONTROLLER_URL = "/api/v1/admin/movies";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieReviewRepository movieReviewRepository;

    @AfterEach
    public void cleanUp() {
        movieRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddMovie() throws Exception {
        MovieRequest movieRequest = TestDataGenerator.createValidMovieRequest();

        // Perform the request and assert the response
        mockMvc.perform(post(ADMIN_MOVIE_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        // Add other assertions based on your response structure
    }

    @Test
    void testGetMovie() throws Exception {
        // Given
        Movie movie = TestDataGenerator.createValidMovie();
        movieRepository.save(movie);
        // When-Then
        mockMvc.perform(get(CUSTOMER_MOVIE_CONTROLLER_URL + "/{movieId}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.title").value(movie.getTitle()));
    }

    @Test
    void testGetAllMovies() throws Exception {
        // Given
        Movie movie1 = TestDataGenerator.createValidMovie();
        Movie movie2 = Movie.builder()
                .title("Movie Title2")
                .realiseDate(LocalDate.now())
                .description("Description2")
                .genres(List.of(Genre.ACTION, Genre.DRAMA))
                .rentalPeriodStart(LocalDate.now())
                .rentalPeriodEnd(LocalDate.now().plusDays(7))
                .language("ENG")
                .productionCompany(List.of("Production Company"))
                .basePrice(BigDecimal.valueOf(150))
                .duration(122)
                .director("Director2")
                .countryOfProduction("USA")
                .rating(Short.valueOf("8"))
                .ageLimit(10)
                .build();

        movieRepository.saveAll(List.of(movie1, movie2));

        // When performing the request to get all movies
        mockMvc.perform(get(CUSTOMER_MOVIE_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title").value(movie1.getTitle()))
                .andExpect(jsonPath("$.content[1].title").value(movie2.getTitle()))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateMovie() throws Exception {
        // Given
        Movie savedMovie = TestDataGenerator.createValidMovie();
        movieRepository.save(savedMovie);
        MovieRequest updatedMovieRequest = new MovieRequest(
                "Movie Title2",
                LocalDate.now(),
                122,
                Short.valueOf("8"),
                "Description2",
                10,
                "USA",
                "Director2",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                "ENG",
                BigDecimal.valueOf(100),
                List.of(Genre.ACTION.getTitle(), Genre.DRAMA.getTitle()),
                List.of("Production Company"));

        // When performing the request to update the movie
        mockMvc.perform(put(ADMIN_MOVIE_CONTROLLER_URL + "/{movieId}", savedMovie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovieRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedMovie.getId()))
                .andExpect(jsonPath("$.title").value(updatedMovieRequest.title()));
        // Verify that the service method was called with the correct parameters
        Movie movie = movieRepository.findById(savedMovie.getId()).get();
        assertEquals(updatedMovieRequest.title(), movie.getTitle());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddReview() throws Exception {
        // Given
        Movie movie = TestDataGenerator.createValidMovie();
        movieRepository.save(movie);

        MovieReviewContext movieReviewContext = TestDataGenerator.createValidMovieReviewDTO();
        try (var MockedJwtClaimsExtractor = Mockito.mockStatic(JwtClaimsExtractor.class)) {
            UserDetails userDetails = new UserDetails(UUID.randomUUID(), "user@gmail.com", null);
            when(JwtClaimsExtractor.extractUserDetails()).thenReturn(userDetails);
            // When performing the request to add a review
            mockMvc.perform(post(CUSTOMER_MOVIE_CONTROLLER_URL + "/{movieId}/reviews", movie.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(movieReviewContext)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.comment").value(movieReviewContext.comment()))
                    .andExpect(jsonPath("$.rating").value(movieReviewContext.rating().intValue()));
        }
    }

    @Test
    void testGetAllReviews() throws Exception {
        // Given
        Movie movie = TestDataGenerator.createValidMovie();
        movieRepository.save(movie);

        try (var MockedJwtClaimsExtractor = Mockito.mockStatic(JwtClaimsExtractor.class)) {
            UserDetails userDetails = new UserDetails(UUID.randomUUID(), "user@gmail.com", null);
            when(JwtClaimsExtractor.extractUserDetails()).thenReturn(userDetails);

            MovieReview review1 = TestDataGenerator.createValidMovieReview(movie);
            MovieReview review2 = TestDataGenerator.createValidMovieReview(movie);

            movieReviewRepository.saveAll(List.of(review1, review2));

            // When performing the request to get all reviews for the movie
            mockMvc.perform(get(CUSTOMER_MOVIE_CONTROLLER_URL + "/{movieId}/reviews", movie.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)));
        }
    }
}
