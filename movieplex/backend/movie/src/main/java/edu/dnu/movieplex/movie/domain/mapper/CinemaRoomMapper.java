package edu.dnu.movieplex.movie.domain.mapper;

import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomRequest;
import edu.dnu.movieplex.movie.domain.dto.cinemaroom.CinemaRoomResponse;
import edu.dnu.movieplex.movie.persistance.model.entity.CinemaRoom;
import edu.dnu.movieplex.movie.persistance.model.enums.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface responsible for mapping between CinemaRoom entity and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface CinemaRoomMapper {
  CinemaRoomResponse entityToResponse(CinemaRoom cinemaRoom);

  CinemaRoom requestToEntity(CinemaRoomRequest cinemaRoomRequest);

  void update(@MappingTarget CinemaRoom cinemaRoom, CinemaRoomRequest cinemaRoomRequest);

  default RoomType mapRoomTypeNameToRoomType(String name) {
    return RoomType.getRoomTypeByName(name);
  }

  default String mapGenresToGenreTitles(RoomType roomType) {
    return roomType.getName();
  }
}