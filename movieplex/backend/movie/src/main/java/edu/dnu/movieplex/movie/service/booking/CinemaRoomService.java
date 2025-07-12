package edu.dnu.movieplex.movie.service.booking;

import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomRequest;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomResponse;
import edu.dnu.movieplex.movie.domain.mapper.CinemaRoomMapper;
import edu.dnu.movieplex.movie.persistance.model.entity.CinemaRoom;
import edu.dnu.movieplex.movie.persistance.repository.CinemaRoomRepository;
import edu.dnu.movieplex.movie.service.exception.CinemaRoomNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing cinema room-related operations.
 */
@Service
@AllArgsConstructor
public class CinemaRoomService {
  private final CinemaRoomRepository cinemaRoomRepository;
  private final CinemaRoomMapper cinemaRoomMapper;

  /**
  * Retrieves details of a cinema room based on its unique identifier.
  *
  * @param cinemaRoomId The unique identifier for the cinema room.
  * @return A {@link CinemaRoomResponse} containing details of the specified cinema room.
  * @throws CinemaRoomNotFoundException if the cinema room with the given ID is not found.
  */
  public CinemaRoomResponse getCinemaRoomById(Integer cinemaRoomId) {
    CinemaRoom cinemaRoom = cinemaRoomRepository.findById(cinemaRoomId)
                .orElseThrow(() -> new CinemaRoomNotFoundException("CinemaRoom by id "
                        + cinemaRoomId + " not found"));
    return cinemaRoomMapper.entityToResponse(cinemaRoom);
  }

  /**
  * Retrieves details of all available cinema rooms.
  *
  * @return A list of {@link CinemaRoomResponse} containing details of all cinema rooms.
  */
  public List<CinemaRoomResponse> getAllCinemaRooms() {
    List<CinemaRoom> cinemaRoomList = cinemaRoomRepository.findAll();
    return cinemaRoomList.stream()
            .map(cinemaRoomMapper::entityToResponse)
            .toList();
  }

  /**
  * Adds a new cinema room with the provided details.
  *
  * @param cinemaRoomRequest The request containing details for the new cinema room.
  * @return A {@link CinemaRoomResponse} containing details of the newly added cinema room.
  */
  public CinemaRoomResponse addCinemaRoom(CinemaRoomRequest cinemaRoomRequest) {
    CinemaRoom cinemaRoomEntity = cinemaRoomRepository.save(
            cinemaRoomMapper.requestToEntity(cinemaRoomRequest));

    return cinemaRoomMapper.entityToResponse(cinemaRoomEntity);
  }

  /**
  * Updates the details of an existing cinema room based on its unique identifier.
  *
  * @param cinemaRoomRequest The request containing updated details for the cinema room.
  * @param cinemaRoomId The unique identifier for the cinema room to be updated.
  * @return A {@link CinemaRoomResponse} containing details of the updated cinema room.
  * @throws CinemaRoomNotFoundException if the cinema room with the given ID is not found.
  */
  @Transactional
  public CinemaRoomResponse updateCinemaRoom(CinemaRoomRequest cinemaRoomRequest,
                                             Integer cinemaRoomId) {
    CinemaRoom cinemaRoom = cinemaRoomRepository.findById(cinemaRoomId)
            .orElseThrow(() -> new CinemaRoomNotFoundException("CinemaRoom by id "
                    + cinemaRoomId + " not found"));
    cinemaRoomMapper.update(cinemaRoom, cinemaRoomRequest);
    return cinemaRoomMapper.entityToResponse(cinemaRoom);
  }

}
