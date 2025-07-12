package edu.dnu.movieplex.movie.persistance.model.enums;

import edu.dnu.movieplex.movie.persistance.model.exception.GenreNotFoundException;
import lombok.Getter;

/**
 * Enumeration representing movie genres.
 */
@Getter
public enum Genre {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    ANIMATED("Animated"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    HISTORICAL("Historical"),
    HORROR("Horror"),
    MUSICAL("Musical"),
    NOIR("Noir"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Science Fiction"),
    THRILLER("Thriller"),
    WESTERN("Western");
  private final String title;

  Genre(String title) {
    this.title = title;
  }

  /**
  * Gets the genre by its title.
  *
  * @param title the title of the genre to retrieve.
  * @return the genre corresponding to the given title.
  * @throws GenreNotFoundException if the genre with the given title is not found.
  */
  public static Genre getGenreByTitle(String title) {
    for (Genre geoObjectType : values()) {
      if (geoObjectType.getTitle().equals(title)) {
        return geoObjectType;
      }
    }

    throw new GenreNotFoundException("Genre by title = " + title + " was not found");
  }
}
