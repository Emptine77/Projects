package edu.dnu.movieplex.movie.domain.dto.cinemaroom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * A record representing a request to create or update a cinema room.
 *
 * @param name The name of the cinema room.
 * @param rowCount The number of rows in the cinema room.
 * @param columnCount The number of columns in the cinema room.
 * @param roomType The type of the cinema room.
 */
public record CinemaRoomRequest(
        @Schema(example = "Room A")
        @NotBlank(message = "Name cannot be blank")
        String name,
        @Schema(example = "10")
        @NotNull(message = "RowCount cannot be null")
        @Positive(message = "RowCount must be a positive integer")
        Integer rowCount,
        @Schema(example = "8")
        @NotNull(message = "ColumnCount cannot be null")
        @Positive(message = "ColumnCount must be a positive integer")
        Integer columnCount,
        @Schema(example = "VIP")
        @NotNull(message = "RoomType cannot be null")
        String roomType
) {
}

