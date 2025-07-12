package edu.dnu.movieplex.movie.persistance.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing movie_reviews table.
 */
@Table(name = "movie_users_reviews", schema = "movie_catalog")
@Entity
@Setter
@Getter
public class MovieReview {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @ManyToOne
  @JoinColumn(name = "movie_id")
  private Movie movie;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "rating")
  private Short rating;

  @Column(name = "comment")
  private String comment;

  public void assignMovie(Movie movie) {
    this.movie = movie;
  }
}
