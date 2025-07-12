package edu.dnu.movieplex.movie.persistance.model.entity;

import edu.dnu.movieplex.movie.persistance.model.enums.Genre;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing movies table.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "movies", schema = "movie_catalog")
public class Movie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(name = "title")
  private String title;
  @Column(name = "realise_date")
  private LocalDate realiseDate;
  @Column(name = "duration")
  private Integer duration;
  @Column(name = "description")
  private String description;
  @Column(name = "age_limit")
  private Integer ageLimit;
  @Column(name = "country_of_production")
  private String countryOfProduction;
  @Column(name = "director")
  private String director;
  @Column(name = "rental_period_start")
  private LocalDate rentalPeriodStart;
  @Column(name = "rental_period_end")
  private LocalDate rentalPeriodEnd;
  @Column(name = "language")
  private String language;
  @Column(name = "rating")
  private Short rating;
  @Column(name = "poster_url")
  private String posterUrl;
  @Column(name = "trailer_url")
  private String trailerUrl;
  @Column(name = "base_price")
  private BigDecimal basePrice;
  @ElementCollection
  @CollectionTable(name = "movie_genres", schema = "movie_catalog",
            joinColumns = @JoinColumn(name = "movie_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "genres")
  private List<Genre> genres;
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "production_companies", schema = "movie_catalog",
            joinColumns = @JoinColumn(name = "movie_production_id"))
  @Column(name = "production_company")
  private List<String> productionCompany;
  @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<MovieReview> movieUsersReviews;
  @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<MovieSchedule> movieSchedules;

  public void addMovieSchedule(MovieSchedule movieSchedule) {
    movieSchedules.add(movieSchedule);
  }
}
