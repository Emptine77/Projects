package edu.dnu.movieplex.movie.persistance.model.enums;

import edu.dnu.movieplex.movie.persistance.model.exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import lombok.Getter;

/**
 * Enumeration representing different types of cinema rooms.
 */
@Getter
public enum RoomType {
    STANDARD("Standard", new BigDecimal("0.00")),
    VIP("VIP", new BigDecimal("10.00")),
    DELUXE("Deluxe", new BigDecimal("20.00"));

  private final String name;
  private final BigDecimal additionalCost;

  RoomType(String name, BigDecimal additionalCost) {
    this.name = name;
    this.additionalCost = additionalCost;
  }

  /**
  * Retrieves the cinema room type based on the given name.
  *
  * @param name the name of the cinema room type.
  * @return the corresponding cinema room type.
  * @throws RoomTypeNotFoundException if the cinema room type with the given name is not found.
  */
  public static RoomType getRoomTypeByName(String name) {
    for (RoomType roomType : values()) {
      if (roomType.getName().equals(name)) {
        return roomType;
      }
    }
    throw new RoomTypeNotFoundException("RoomType by name" + name + " not found");
  }
}
