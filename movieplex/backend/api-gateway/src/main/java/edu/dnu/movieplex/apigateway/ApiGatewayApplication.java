package edu.dnu.movieplex.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The {@code ApiGatewayApplication} class serves as the entry point for the MoviePlex application.
 * */
@EnableScheduling
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"edu.dnu.movieplex.apigateway",
  "edu.dnu.movieplex.movie", "edu.dnu.movieplex.user"})
public class ApiGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }

}
