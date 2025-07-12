package edu.dnu.movieplex.movie.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import config.SecurityConfig;
import edu.dnu.movieplex.movie.api.controller.admin.MovieController;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieRequest;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieResponse;
import edu.dnu.movieplex.movie.service.booking.MovieScheduleService;
import edu.dnu.movieplex.movie.service.movie.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import util.TestDataGenerator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(MovieController.class)
@Import(SecurityConfig.class)
class MovieControllerTest {
    private static final String ADMIN_MOVIE_CONTROLLER_URL = "/api/v1/admin/movies";
    @MockBean
    private MovieService movieService;
    @MockBean
    private MovieScheduleService movieScheduleService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddMovie() throws Exception {
        //Given
        MovieRequest movieRequest = TestDataGenerator.createValidMovieRequest();
        MovieResponse movieResponse = MovieResponse.builder().build();
        //When
        when(movieService.addMovie(any())).thenReturn(movieResponse);

        //Then
        mockMvc.perform(post(ADMIN_MOVIE_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(movieResponse)));
    }



//     @Test
//     void testGetAllMovies() throws Exception {
//     // Given
//     Pageable pageable = PageRequest.of(0, 10);
//     List<MovieResponse> movieResponses = Collections.singletonList(MovieResponse.builder().build());
//     Page<MovieResponse> page = new PageImpl<>(movieResponses, pageable, 1);
//     // When
//     when(movieService.getAllMovies(pageable)).thenReturn(page);
//     // Then
//     mockMvc.perform(get(MOVIE_CONTROLLER_URL)
//                     .param("page", String.valueOf(pageable.getPageNumber()))
//                     .param("size", String.valueOf(pageable.getPageSize()))
//                     .contentType(MediaType.APPLICATION_JSON))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.content", hasSize(1))) 
//             .andExpect(content().json(objectMapper.writeValueAsString(page)));

//     // Verify that the service method was called with the correct parameter
//     verify(movieService, times(1)).getAllMovies(pageable);
//     }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateMovie() throws Exception {
        // Given
        int movieId = 1;
        MovieRequest movieRequest = TestDataGenerator.createValidMovieRequest();
        MovieResponse movieResponse = MovieResponse.builder().build();
        // When
        when(movieService.updateMovie(any(), eq(movieId))).thenReturn(movieResponse);

        // Then
        mockMvc.perform(put(ADMIN_MOVIE_CONTROLLER_URL + "/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(movieResponse)));

        // Verify that the service method was called with the correct parameters
        verify(movieService, times(1)).updateMovie(any(), eq(movieId));
    }
}