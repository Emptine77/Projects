package edu.dnu.movieplex.recommendationsystem.domain.dto;


import edu.dnu.movieplex.recommendationsystem.persistance.model.Rated;
import java.util.List;

public record UserResponse(
    Long id,
    String name,
    String userId,
    List<Rated> ratedList
) {
}