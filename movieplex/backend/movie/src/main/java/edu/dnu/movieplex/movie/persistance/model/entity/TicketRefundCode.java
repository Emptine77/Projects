package edu.dnu.movieplex.movie.persistance.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing ticket_refund_codes table.
 */
@Entity
@Getter
@Setter
@Table(name = "ticket_refund_codes", schema = "movie_catalog")
public class TicketRefundCode {
  @Id
  @Column(name = "refund_code")
  private String refundCode;

  @Column(name = "expiration_date")
  private LocalDate expirationDate;

  @Column(name = "is_used")
  private Boolean isUsed;

  @OneToOne
  @JoinColumn(name = "ticket_id")
  private Ticket ticket;
}

