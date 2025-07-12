package edu.dnu.movieplex.movie.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.SecurityConfig;
import edu.dnu.movieplex.common.dto.UserDetails;
import edu.dnu.movieplex.common.security.utils.JwtClaimsExtractor;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieResponse;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieReviewContext;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRefundDto;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketRequest;
import edu.dnu.movieplex.movie.domain.dto.ticket.TicketResponse;
import edu.dnu.movieplex.movie.service.booking.MovieScheduleService;
import edu.dnu.movieplex.movie.service.booking.TicketService;
import edu.dnu.movieplex.movie.service.exception.MovieNotFoundException;
import edu.dnu.movieplex.movie.service.movie.MovieReviewService;
import edu.dnu.movieplex.movie.service.movie.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import util.TestDataGenerator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class CustomerControllerTest {
  private static final String BASIC_URL = "/api/v1";
  @MockBean
  private MovieService movieService;
  @MockBean
  private TicketService ticketService;
  @MockBean
  private MovieReviewService movieReviewService;
  @MockBean
  private MovieScheduleService movieScheduleService;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @WithMockUser(roles = "USER")
  @Test
  void addTickets() throws Exception {
    //Given
    TicketRequest ticketRequest = new TicketRequest(5, 5);
    TicketResponse ticketResponse = TestDataGenerator.createValidTicketResponse();
    List<TicketRequest> ticketRequests = List.of(ticketRequest);
    List<TicketResponse> ticketResponses = List.of(ticketResponse);
    try (var staticMock = Mockito.mockStatic(JwtClaimsExtractor.class)) {
      UserDetails userDetails = new UserDetails(UUID.randomUUID(), "user@gmail.com", null);
      Integer movieScheduleId = 1;
      //When
      when(JwtClaimsExtractor.extractUserDetails()).thenReturn(userDetails);
      when(ticketService.createTickets(ticketRequests, userDetails.userId(), movieScheduleId))
              .thenReturn(ticketResponses);
      //Then
      mockMvc.perform(post(BASIC_URL + "/movieSchedules/{movieScheduleId}/tickets", movieScheduleId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(ticketRequests)))
              .andExpect(status().isOk())
              .andExpect(content().string(objectMapper.writeValueAsString(ticketResponses)));
    }
  }

  @Test
  void getTicket() throws Exception {
    //Given
    int ticketId = 1;
    TicketResponse ticketResponse = TestDataGenerator.createValidTicketResponse();
    //When
    when(ticketService.getTicketById(ticketId)).thenReturn(ticketResponse);
    //Then
    mockMvc.perform(get(BASIC_URL + "/tickets/{ticketId}", ticketId)
                    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(ticketResponse)));
  }

  @Test
  void getAll() {
    //        MovieBriefResponse movieBriefResponse = new MovieBriefResponse(1,"test", (short) 4,90,18,"test",LocalDate.now(),LocalDate.MAX,"test");
//        ScheduleTicketDto test1 = new ScheduleTicketDto(1,5,5,BigDecimal.valueOf(10),"booked");
//        List<ScheduleTicketDto> testlist = List.of(test1);
//        TicketsGroupedBySchedule ticketsGroupedBySchedule = new TicketsGroupedBySchedule(movieBriefResponse,LocalDateTime.now(),"test",testlist);
//        List<TicketsGroupedBySchedule> ticketsGroupedBySchedules = List.of(ticketsGroupedBySchedule);
//        UUID uuid = UUID.randomUUID();
//        Ticket ticket = TestDataGenerator.createValidTicket(uuid, );
//        //When
//        when(ticketService.getAllTickets(uuid)).thenReturn(ticketsGroupedBySchedules);
//        //Then
//        mockMvc.perform(get(TICKET_CONTROLLER_URL)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string(objectMapper.writeValueAsString(ticketsGroupedBySchedules)));
  }

  @WithMockUser(roles = "USER")
  @Test
  void ticketRefund() throws Exception {
    // Given
    int ticketId = 1;
    UUID refundUUID = UUID.randomUUID();
    // When
    when(ticketService.refundTicket(ticketId)).thenReturn(refundUUID);
    MvcResult result = mockMvc.perform(post(BASIC_URL + "/tickets/{ticketId}/refund", ticketId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Then
    String contentAsString = result.getResponse().getContentAsString().replaceAll("\"", ""); // убрать кавычки
    assertThat(contentAsString).isEqualTo(refundUUID.toString());
  }

  @Test
  void getTicketRefund() throws Exception {
    // Given
    UUID refundUUID = UUID.randomUUID();
    TicketResponse ticketResponse = TestDataGenerator.createValidTicketResponse();
    TicketRefundDto ticketRefundDto = new TicketRefundDto(ticketResponse, LocalDate.MAX, Boolean.FALSE);
    //
    when(ticketService.getTicketRefund(refundUUID)).thenReturn(ticketRefundDto);
    //
    mockMvc.perform(get(BASIC_URL + "/tickets/refund")
                    .param("uuid", refundUUID.toString()))
            .andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(ticketRefundDto)));
  }

  @WithMockUser(roles = "USER")
  @Test
  void addReview() throws Exception {
    // Given
    int movieId = 1;
    try (var staticMock = Mockito.mockStatic(JwtClaimsExtractor.class)) {
      MovieReviewContext movieReviewContext = TestDataGenerator.createValidMovieReviewDTO();
      UserDetails userDetails = new UserDetails(UUID.randomUUID(), "user@gmail.com", null);
      // When
      when(JwtClaimsExtractor.extractUserDetails()).thenReturn(userDetails);
      when(movieReviewService.addMovieReview(any(), eq(userDetails.userId()), eq(movieId))).thenReturn(movieReviewContext);

      // Then
      mockMvc.perform(post(BASIC_URL + "/movies/{movieId}/reviews", movieId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(movieReviewContext)))
              .andExpect(status().isCreated())
              .andExpect(content().json(objectMapper.writeValueAsString(movieReviewContext)));

      // Verify that the service method was called with the correct parameters
      verify(movieReviewService, times(1)).addMovieReview(any(), eq(userDetails.userId()), eq(movieId));
    }
  }

  @Test
  void getAllReview() throws Exception {
    // Given
    int movieId = 1;
    Pageable pageable = PageRequest.of(0, 10); // Example pageable
    List<MovieReviewContext> reviewDTOList = Collections.singletonList(TestDataGenerator.createValidMovieReviewDTO());
    Page<MovieReviewContext> page = new PageImpl<>(reviewDTOList, pageable, 1);
    // When
    when(movieReviewService.getAllMovieReview(pageable, movieId)).thenReturn(page);
    // Then
    mockMvc.perform(get(BASIC_URL + "/movies/{movieId}/reviews", movieId)
                    .param("page", String.valueOf(pageable.getPageNumber()))
                    .param("size", String.valueOf(pageable.getPageSize()))
                    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1))) // Ensure there is a "content" array with one item
        .andExpect(content().json(objectMapper.writeValueAsString(page)));

    // Verify that the service method was called with the correct parameters
    verify(movieReviewService, times(1)).getAllMovieReview(pageable, movieId);
  }

  @Test
  void getMovie() throws Exception {
    //Given
    int movieId = 1;
    MovieResponse movieResponse = MovieResponse.builder().build();
    //When
    when(movieService.getMovieById(movieId)).thenReturn(movieResponse);
    //Then
    mockMvc.perform(get(BASIC_URL + "/movies/{movieId}", movieId)
                    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(movieResponse)));
  }
  @Test
  void testGetMovieWithInvalidId() throws Exception {
    // Given
    int invalidMovieId = 999;
    when(movieService.getMovieById(invalidMovieId))
        .thenThrow(new MovieNotFoundException("Movie not found"));

    // When-then
    mockMvc.perform(get(BASIC_URL + "/movies/{movieId}", invalidMovieId)
                    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", is("Movie not found")));

    // Verify that the service method was called with the correct parameter
    verify(movieService, times(1)).getMovieById(invalidMovieId);
  }
}