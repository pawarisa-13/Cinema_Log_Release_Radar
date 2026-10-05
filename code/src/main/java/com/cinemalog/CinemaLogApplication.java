package com.cinemalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Cinema Log &amp; Release Radar — entry point.
 * Owner: Pawarisa (673380592-3)
 */
@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class CinemaLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaLogApplication.class, args);
    }
}
