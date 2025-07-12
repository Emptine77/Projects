package edu.dnu.movieplex.recommendationsystem.service;



import edu.dnu.movieplex.recommendationsystem.domain.dto.MovieRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.MovieResponse;
import edu.dnu.movieplex.recommendationsystem.domain.mapper.MovieMapper;
import edu.dnu.movieplex.recommendationsystem.persistance.model.Movie;
import edu.dnu.movieplex.recommendationsystem.persistance.repository.MovieRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class MovieService {
  private final MovieRepository movieRepository;
  private final MovieMapper movieMapper;

  public List<MovieResponse> getAllMovie(){
    List<Movie> movies = movieRepository.findAll();
    return movies.stream()
        .map(movieMapper::entityToDTO)
        .toList();
  }
  @Transactional
  public MovieResponse addMovie(MovieRequest movieRequest){
    if(movieRepository.existsByTitle(movieRequest.title())){
      throw new RuntimeException("Фільм з такою назвою вже існує");
    }
    Movie movie = movieRepository.save(movieMapper.DTOtoEntity(movieRequest));
    return movieMapper.entityToDTO(movie);
  }

  public MovieResponse getMovieByTitle(String movieTitle) {
    Movie movie = movieRepository.findMovieByTitle(movieTitle).orElseThrow(() -> new ResponseStatusException(
        HttpStatusCode.valueOf(404)));
    return movieMapper.entityToDTO(movie);
  }

  public List<MovieResponse> getMovieByRating(Short imdbRating) {
    List<Movie> movies = movieRepository.findMoviesByImdbRatingGreaterThanEqualOrderByImdbRatingDesc(imdbRating);
    return movies.stream()
        .map(movieMapper::entityToDTO)
        .toList();
  }
  public List<MovieResponse> collaborationRecommendation(String userId, Short rating){
    List<Movie> movies = movieRepository.findMoviesWithMostMatchesAndNotLikedByUser(userId, rating);
    return movies.stream().map(movieMapper::entityToDTO).toList();
  }
  @Transactional
  public List<MovieResponse> hybridRecommendation(String userId, Short rating, Integer countsRate, Integer genreLimit){
    Integer UserCountsRate = movieRepository.findRatingCounts(userId);
    List<Movie> movies;
    if(UserCountsRate <= countsRate) {
      movies = movieRepository.findMoviesByImdbRatingGreaterThanEqualOrderByImdbRatingDesc(rating);
      return movies.stream().map(movieMapper::entityToDTO).toList();
    }
    else movies = movieRepository.findMoviesWithMostMatchesAndNotLikedByUser(userId, rating);
    List<String> movieTitles = new ArrayList<>();
    List<Movie> uniqueMovies = movies.stream()
        .filter(movie -> movieTitles.add(movie.getTitle()))
        .toList();
    List<Movie> finalMovies = movieRepository.findRecommendedMoviesBasedOnGenres(movieTitles, userId, genreLimit);
    return finalMovies.stream().map(movieMapper::entityToDTO).toList();
  }
  @Transactional
  public MovieResponse updateMovie(MovieRequest movieRequest, String movieTitle) {
    Movie movie = movieRepository.findMovieByTitle(movieTitle)
        .orElseThrow(()-> new RuntimeException("Фільм з назвою " + movieTitle + " не знайден"));
    movieMapper.updateMovie(movie, movieRequest);
    movieRepository.save(movie);
    return movieMapper.entityToDTO(movie);
  }

  public void deleteMovie(String movieTitle) {
    movieRepository.deleteAllByTitle(movieTitle);
  }
}
