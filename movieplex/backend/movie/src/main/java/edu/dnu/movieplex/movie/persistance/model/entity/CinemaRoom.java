package edu.dnu.movieplex.movie.persistance.model.entity;


import edu.dnu.movieplex.movie.persistance.model.enums.RoomType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a cinema room table.
 */
@Table(name = "cinema_rooms", schema = "movie_catalog")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CinemaRoom {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  @Column(name = "name")
  String name;
  @Column(name = "row_count")
  Integer rowCount;
  @Column(name = "column_count")
  Integer columnCount;
  @OneToMany(mappedBy = "cinemaRoom", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<MovieSchedule> movieShedules;
  @Enumerated(EnumType.STRING)
  @Column(name = "room_type")
  private RoomType roomType;
}
