package edu.dnu.movieplex.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main class of the movie module.
 */
@SpringBootApplication(scanBasePackages = {"edu.dnu.movieplex.common",
  "edu.dnu.movieplex.movie"})
@EnableScheduling
public class MovieInfoApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieInfoApplication.class, args);
  }

}
