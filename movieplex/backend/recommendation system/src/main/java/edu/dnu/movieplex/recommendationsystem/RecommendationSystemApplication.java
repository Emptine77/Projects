package edu.dnu.movieplex.recommendationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jAuditing
@EnableNeo4jRepositories(basePackages = {"edu.dnu.movieplex.recommendationsystem.persistance.repository"})
public class RecommendationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecommendationSystemApplication.class, args);
	}

}
