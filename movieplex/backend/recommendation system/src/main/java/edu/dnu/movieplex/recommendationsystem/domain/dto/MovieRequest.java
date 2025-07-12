package edu.dnu.movieplex.recommendationsystem.domain.dto;

import java.util.List;

public record MovieRequest(
    String title,
    Short imdbRating,
    List<String> genres
) {
}
