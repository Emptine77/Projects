package edu.dnu.movieplex.movie.service.booking;

import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomRequest;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomResponse;
import edu.dnu.movieplex.movie.domain.mapper.CinemaRoomMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.CinemaRoom;
import edu.dnu.movieplex.movie.persistance.model.enums.RoomType;
import edu.dnu.movieplex.movie.persistance.repository.CinemaRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.TestDataGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CinemaRoomServiceTest{
    @Mock
    private CinemaRoomRepository cinemaRoomRepository;
    @Mock
    private CinemaRoomMapper cinemaRoomMapper;
    @InjectMocks
    private CinemaRoomService cinemaRoomService;
    @Test
    void addCinemaRoomTest() {
        // GIVEN
        CinemaRoomRequest cinemaRoomRequest = new CinemaRoomRequest("test", 5, 5, "VIP");
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        CinemaRoomResponse cinemaRoomResponse = TestDataGenerator.createValidCinemaRoomResponseEx1();
        //WHEN
        when(cinemaRoomMapper.requestToEntity(cinemaRoomRequest)).thenReturn(cinemaRoom);
        when(cinemaRoomRepository.save(cinemaRoom)).thenReturn(cinemaRoom);
        when(cinemaRoomMapper.entityToResponse(cinemaRoom)).thenReturn(cinemaRoomResponse);

        CinemaRoomResponse result = cinemaRoomService.addCinemaRoom(cinemaRoomRequest);

        // THEN
        assertNotNull(result);
        assertEquals(cinemaRoomResponse, result);
    }
    @Test
    void getCinemaRoomTest(){
        // GIVEN
        Integer cinemaRoomId = 1;
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        CinemaRoomResponse cinemaRoomResponse = TestDataGenerator.createValidCinemaRoomResponseEx1();
        //WHEN
        when(cinemaRoomRepository.findById(cinemaRoomId)).thenReturn(Optional.of(cinemaRoom));
        when(cinemaRoomMapper.entityToResponse(cinemaRoom)).thenReturn(cinemaRoomResponse);
        CinemaRoomResponse actualResponse = cinemaRoomService.getCinemaRoomById(cinemaRoomId);
        // THEN
        assertEquals(cinemaRoomResponse, actualResponse);
    }
     @Test
     void getAllCinemaRoomTest(){
        // GIVEN
        CinemaRoomResponse cinemaRoomResponse1 = TestDataGenerator.createValidCinemaRoomResponseEx1();
        CinemaRoomResponse cinemaRoomResponse2 = TestDataGenerator.createValidCinemaRoomResponseEx2();
        List<CinemaRoomResponse> cinemaRoomResponses = Arrays.asList(cinemaRoomResponse1, cinemaRoomResponse2);
        CinemaRoom cinemaRoom1 = TestDataGenerator.createValidCinemaRoom();
        CinemaRoom cinemaRoom2 = new CinemaRoom(2, "test2", 5,5, null, RoomType.VIP);
        List<CinemaRoom> cinemaRoomList = Arrays.asList(cinemaRoom1, cinemaRoom2);
        
        // WHEN
        when(cinemaRoomRepository.findAll()).thenReturn(cinemaRoomList);
        when(cinemaRoomMapper.entityToResponse(any())).thenAnswer(invocation -> {
        CinemaRoom cinemaRoomArgument = invocation.getArgument(0);
        return cinemaRoomResponses.stream()
                .filter(response -> response.id() == cinemaRoomArgument.getId())
                .findFirst()
                .orElse(null);
        });

        // THEN
        List<CinemaRoomResponse> actualResponses = cinemaRoomService.getAllCinemaRooms();
        assertEquals(cinemaRoomResponses, actualResponses);
    }
    @Test
    void updateCinemaRoomTest() {
        // GIVEN
        Integer cinemaRoomId = 1;
        CinemaRoomRequest cinemaRoomRequest = new CinemaRoomRequest("updatedTest", 6, 6, "Standard");
        CinemaRoom cinemaRoom = TestDataGenerator.createValidCinemaRoom();
        CinemaRoomResponse cinemaRoomResponse = TestDataGenerator.createValidCinemaRoomResponseEx1();

        // WHEN
        when(cinemaRoomRepository.findById(cinemaRoomId)).thenReturn(Optional.of(cinemaRoom));
        doNothing().when(cinemaRoomMapper).update(cinemaRoom, cinemaRoomRequest);
        when(cinemaRoomMapper.entityToResponse(cinemaRoom)).thenReturn(cinemaRoomResponse);
        CinemaRoomResponse result = cinemaRoomService.updateCinemaRoom(cinemaRoomRequest, cinemaRoomId);

        // THEN
        assertNotNull(result);
        assertEquals(cinemaRoomResponse, result);
    }

}