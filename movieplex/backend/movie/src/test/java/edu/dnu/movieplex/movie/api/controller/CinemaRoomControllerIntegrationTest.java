package edu.dnu.movieplex.movie.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.SecurityConfig;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomRequest;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomResponse;
import edu.dnu.movieplex.movie.persistance.model.entity.CinemaRoom;
import edu.dnu.movieplex.movie.persistance.repository.CinemaRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class CinemaRoomControllerIntegrationTest {
    private static final String ROOM_CONTROLLER_ADMIN_URL = "/api/v1/admin/cinemaRooms";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;
    @AfterEach
    public  void cleanUp() {cinemaRoomRepository.deleteAll();}
    @Test

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addCinemaRoom() throws Exception{
        CinemaRoomRequest cinemaRoomRequest = TestDataGenerator.createValidCinemaRoomRequest();
        CinemaRoomResponse cinemaRoomResponse = TestDataGenerator.createValidCinemaRoomResponseEx1();
        mockMvc.perform(post(ROOM_CONTROLLER_ADMIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cinemaRoomRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCinemaRoom() throws Exception{
        //Given
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        cinemaRoomRepository.save(cinemaRoom);
        //When-Then
        mockMvc.perform(get(ROOM_CONTROLLER_ADMIN_URL + "/{cinemaRoomId}", cinemaRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cinemaRoom.getId()))
                .andExpect(jsonPath("$.name").value(cinemaRoom.getName()));

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAll() throws Exception {
        //Given
        CinemaRoom cinemaRoom1 = TestDataGenerator.createValidCinemaRoom();
        cinemaRoomRepository.saveAll(List.of(cinemaRoom1));

        mockMvc.perform(get(ROOM_CONTROLLER_ADMIN_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCinemaRoom() throws  Exception{
        //Given
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        cinemaRoomRepository.save(cinemaRoom);
        CinemaRoomRequest updatedCinemaRoomRequest = new CinemaRoomRequest("name2",6,6,"VIP");
        //When-Then
        mockMvc.perform(get(ROOM_CONTROLLER_ADMIN_URL + "/{cinemaRoomId}", cinemaRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cinemaRoom.getId()))
                .andExpect(jsonPath("$.name").value(cinemaRoom.getName()));
    }
}