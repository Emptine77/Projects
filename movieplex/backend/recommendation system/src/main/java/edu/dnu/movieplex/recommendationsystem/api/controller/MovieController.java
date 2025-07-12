package edu.dnu.movieplex.recommendationsystem.api.controller;

import edu.dnu.movieplex.recommendationsystem.domain.dto.MovieRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.MovieResponse;
import edu.dnu.movieplex.recommendationsystem.service.MovieService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/movie")
@RequiredArgsConstructor
public class MovieController {
  private final MovieService movieService;
  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  public List<MovieResponse> getAllMovies(){
    return movieService.getAllMovie();
  }
  @GetMapping("/recommendation/rating/{imdbRating}")
  @ResponseStatus(HttpStatus.OK)
  public List<MovieResponse> getMoviesByRatingAlgorithm(@PathVariable Short imdbRating){
    return movieService.getMovieByRating(imdbRating);
  }
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public MovieResponse addMovie(@RequestBody MovieRequest movieRequest){
    return movieService.addMovie(movieRequest);
  }
  @GetMapping("/{movieTitle}")
  @ResponseStatus(HttpStatus.OK)
  public MovieResponse getMovieByTitle(@PathVariable String movieTitle){
    return movieService.getMovieByTitle(movieTitle);
  }
  @GetMapping("/recommendation/collaborative/{userId}/{rating}")
  @ResponseStatus(HttpStatus.OK)
  public List<MovieResponse> getMovieRecommendationByCollaborativeAlgorithm(@PathVariable String userId, @PathVariable Short rating){
    return movieService.collaborationRecommendation(userId, rating);
  }
  @GetMapping("/recommendation/hybrid")
  @ResponseStatus(HttpStatus.OK)
  public List<MovieResponse> getMovieRecommendationByHybridAlgorithm(@RequestParam String userId, @RequestParam Short rating, @RequestParam Integer countsRate, @RequestParam Integer genreLimit){
    return movieService.hybridRecommendation(userId, rating, countsRate, genreLimit);
  }
  @PutMapping("/{movieTitle}")
  @ResponseStatus(HttpStatus.OK)
  public MovieResponse updateMovie(@RequestBody MovieRequest movieRequest, @PathVariable String movieTitle){
    return movieService.updateMovie(movieRequest, movieTitle);
  }
  @DeleteMapping("/{movieTitle}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteMovie(@PathVariable String movieTitle){
    movieService.deleteMovie(movieTitle);
  }
}
