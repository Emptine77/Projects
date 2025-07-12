package edu.dnu.movieplex.recommendationsystem.persistance.repository;

import edu.dnu.movieplex.recommendationsystem.persistance.model.Movie;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends Neo4jRepository<Movie, Long> {

  Optional<Movie> findMovieByTitle(String title);
  @Query("MATCH (m:Movie) WHERE m.imdbRating >= $imdbRating RETURN m ORDER BY m.imdbRating DESC")
  List<Movie> findMoviesByImdbRatingGreaterThanEqualOrderByImdbRatingDesc(Short imdbRating);
  @Query("MATCH (u1:User {userId: $userId})-[:RATED]->(m:Movie)<-[:RATED]-(u2:User) " +
      "WITH u1, u2, COUNT(m) AS moviesRated " +
      "ORDER BY moviesRated DESC " +
      "MATCH (u2)-[r:RATED]->(m2:Movie) " +
      "WHERE NOT (u1)-[:RATED]->(m2) AND u1 <> u2 AND r.rating >= $rating " +
      "RETURN m2")
  List<Movie> findMoviesWithMostMatchesAndNotLikedByUser(String userId, Short rating);
  @Query("MATCH (u:User {userId: $userId})-[r:RATED]->() RETURN COUNT(r) AS ratedCount")
  Integer findRatingCounts(String userId);
  @Query("""
      MATCH (user:User {userId: $userId})-[:RATED]->(watched:Movie)-[:AFFILIATION]->(userGenre:Genre)
      WITH user, userGenre, COUNT(userGenre) AS genreCount
      ORDER BY genreCount DESC
      LIMIT $genreLimit
      WITH user, collect(userGenre) AS favoriteGenres

      UNWIND $uniqueMovies AS movieTitle
      MATCH (movie:Movie {title: movieTitle})-[:AFFILIATION]->(movieGenre:Genre)
      WITH movie, movieGenre, favoriteGenres
      WHERE movieGenre IN favoriteGenres OR NOT movieGenre IN favoriteGenres
      WITH movie, COUNT(CASE WHEN movieGenre IN favoriteGenres THEN 1 ELSE 0 END) AS matchCount
      RETURN movie, matchCount
      ORDER BY matchCount DESC, movie.imdbRating DESC             
        """)
  List<Movie> findRecommendedMoviesBasedOnGenres(List<String> uniqueMovies, String userId, Integer genreLimit);
  boolean existsByTitle(String title);

  void deleteAllByTitle(String movieTitle);
}
