package edu.dnu.movieplex.movie.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.SecurityConfig;
import edu.dnu.movieplex.movie.api.controller.admin.CinemaRoomController;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomRequest;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomResponse;
import edu.dnu.movieplex.movie.service.booking.CinemaRoomService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import util.TestDataGenerator;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(CinemaRoomController.class)
@Import(SecurityConfig.class)
class CinemaRoomControllerTest {
    private static final String ROOM_CONTROLLER_URL = "/api/v1/admin/cinemaRooms";

    @MockBean
    private CinemaRoomService cinemaRoomService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addCinemaRoom() throws Exception {
        //Given
        CinemaRoomRequest cinemaRoomRequest = TestDataGenerator.createValidCinemaRoomRequest();
        CinemaRoomResponse cinemaRoomResponse = TestDataGenerator.createValidCinemaRoomResponseEx1();
        //When
        when(cinemaRoomService.addCinemaRoom(any())).thenReturn(cinemaRoomResponse);

        //Then
        mockMvc.perform(MockMvcRequestBuilders.post(ROOM_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cinemaRoomRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(cinemaRoomResponse)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCinemaRoom()  throws Exception{
        //Given
        int roomId = 1;
        CinemaRoomResponse cinemaRoomResponse = TestDataGenerator.createValidCinemaRoomResponseEx1();
        //When
        when(cinemaRoomService.getCinemaRoomById(roomId)).thenReturn(cinemaRoomResponse);
        //Then
        mockMvc.perform(get(ROOM_CONTROLLER_URL + "/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(cinemaRoomResponse)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAll() throws Exception {
        //Given
        CinemaRoomResponse cinemaRoomResponse1 = TestDataGenerator.createValidCinemaRoomResponseEx1();
        CinemaRoomResponse cinemaRoomResponse2 = TestDataGenerator.createValidCinemaRoomResponseEx2();
        List<CinemaRoomResponse> cinemaRoomResponses = List.of(cinemaRoomResponse1, cinemaRoomResponse2);
        //When
        when(cinemaRoomService.getAllCinemaRooms()).thenReturn(cinemaRoomResponses);
        //Then
        mockMvc.perform(get(ROOM_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(cinemaRoomResponses)));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCinemaRoom() throws Exception {
        //Given
        int roomId = 1;
        CinemaRoomResponse cinemaRoomResponse = TestDataGenerator.createValidCinemaRoomResponseEx1();
        CinemaRoomRequest cinemaRoomRequest = TestDataGenerator.createValidCinemaRoomRequest();
        //When
        when(cinemaRoomService.updateCinemaRoom(any(), eq(roomId))).thenReturn(cinemaRoomResponse);
        //Then
        mockMvc.perform(put(ROOM_CONTROLLER_URL + "/{roomId}", roomId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cinemaRoomRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(cinemaRoomResponse)));

    }
}