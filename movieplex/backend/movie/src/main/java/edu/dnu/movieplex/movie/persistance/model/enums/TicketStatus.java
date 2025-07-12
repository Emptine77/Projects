package edu.dnu.movieplex.movie.persistance.model.enums;


import lombok.Getter;

/**
 * Enumeration representing the status of a movie ticket.
 */
@Getter
public enum TicketStatus {
    CANCELED("canceled"),
    EXPIRED("expired"),
    TEMPORARY_BOOKED("temporary booked"),
    BOOKED("booked"),
    USED("used");

  private final String name;

  TicketStatus(String name) {
    this.name = name;
  }
}
