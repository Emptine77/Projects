package edu.dnu.movieplex.movie.domain.dto.cinemaroom;

/**
 * A record representing information about a cinema room in a response.
 *
 * @param name The name of the cinema room.
 * @param rowCount The number of rows in the cinema room.
 * @param columnCount The number of columns in the cinema room.
 * @param roomType The type of the cinema room.
 */
public record CinemaRoomResponse(
        Integer id,
        String name,
        Integer rowCount,
        Integer columnCount,
        String roomType
) {
} 