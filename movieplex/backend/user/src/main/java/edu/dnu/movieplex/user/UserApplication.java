package edu.dnu.movieplex.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * The main class for the payment module.
 */
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"edu.dnu.movieplex.user"})
public class UserApplication {
  public static void main(String[] args) {
    SpringApplication.run(UserApplication.class, args);
  }
}
