package edu.dnu.movieplex.recommendationsystem.domain.dto;


import java.util.List;

public record MovieResponse(
    Long id,
    String title,
    Short imdbRating,
    List<String> genres
) {
}
