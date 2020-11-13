package com.maglethong.habitica.utility.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.maglethong.habitica.utility"})
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
