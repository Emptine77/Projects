package edu.dnu.movieplex.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * The main class for the payment module.
 */
@SpringBootApplication(scanBasePackages = {"edu.dnu.movieplex.payment",
        "edu.dnu.movieplex.movie", "edu.dnu.movieplex.common"})
public class PaymentApplication {
  public static void main(String[] args) {
    SpringApplication.run(PaymentApplication.class, args);
  }
}
