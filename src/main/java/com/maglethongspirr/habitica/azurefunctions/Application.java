package com.maglethongspirr.habitica.azurefunctions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  /**
   * Spring Application loader.
   *
   * @param args Application arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
