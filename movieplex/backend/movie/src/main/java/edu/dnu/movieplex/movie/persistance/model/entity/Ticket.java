package edu.dnu.movieplex.movie.persistance.model.entity;

import edu.dnu.movieplex.movie.persistance.model.enums.TicketStatus;
import edu.dnu.movieplex.movie.persistance.model.exception.IllegalTicketStatusException;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing tickets table.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tickets", schema = "movie_catalog")
public class Ticket {
  @Column(name = "user_id")
  UUID userId;
  @Column(name = "purchase_time")
  LocalDateTime purchaseTime;
  @Column(name = "seat_row")
  Integer seatRow;
  @Column(name = "seat_column")
  Integer seatColumn;
  @Column(name = "price")
  BigDecimal price;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private TicketStatus ticketStatus;
  @ManyToOne
  @JoinColumn(name = "movie_schedule_id")
  private MovieSchedule movieSchedule;
  @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL)
  private TicketRefundCode ticketRefundCode;

  public void addTicketRefundCode(TicketRefundCode ticketRefundCode) {
    this.ticketRefundCode = ticketRefundCode;
  }

  /**
  * Sets the status of the ticket to 'CANCELED'.
  * Throws an IllegalTicketStatusException if the ticket does not have the status 'BOOKED'.
  */
  public void setStatusToCanceled() {
    if (ticketStatus != TicketStatus.BOOKED) {
      throw new IllegalTicketStatusException("Unable to set ticket status to 'CANCELED',"
              + " since it must have status 'BOOKED'");
    }
    ticketStatus = TicketStatus.CANCELED;
  }

  public void assignTicketRefundCode(TicketRefundCode ticketRefundCode) {
    this.ticketRefundCode = ticketRefundCode;
    ticketRefundCode.setTicket(this);
  }

  public void setStatusToBooked() {
    ticketStatus = TicketStatus.BOOKED;
  }
}
