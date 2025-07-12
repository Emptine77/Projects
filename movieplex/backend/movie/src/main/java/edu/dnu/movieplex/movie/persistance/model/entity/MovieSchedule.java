package edu.dnu.movieplex.movie.persistance.model.entity;

import edu.dnu.movieplex.movie.persistance.model.enums.MovieFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing movie_catalog table.
 */
@Table(name = "movie_schedule", schema = "movie_catalog")
@Entity
@Setter
@Getter
public class MovieSchedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "movie_id")
  private Movie movie;

  @OneToMany(mappedBy = "movieSchedule", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<Ticket> tickets = new ArrayList<>();

  @Column(name = "start_datetime")
  private LocalDateTime startTime;
  @Enumerated(EnumType.STRING)
  @Column(name = "movie_format")
  private MovieFormat movieFormat;
  @ManyToOne
  @JoinColumn(name = "cinema_room_id")
  private CinemaRoom cinemaRoom;
  @Column(name = "ticket_price")
  private BigDecimal ticketPrice;

  /**
  * Adds a list of tickets to the movie schedule.
  *
  * @param ticketList the list of tickets to be added to the movie schedule.
  */
  public void addTickets(List<Ticket> ticketList) {
    for (Ticket ticket : ticketList) {
      ticket.setMovieSchedule(this);
    }
    tickets.addAll(ticketList);
  }

  public void assignMovie(Movie movie) {
    this.movie = movie;
    movie.addMovieSchedule(this);
  }
}
