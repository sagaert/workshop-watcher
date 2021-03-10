package org.salex.raspberry.workshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJdbcRepositories
public class WorkshopWatcherAgent {
    public static void main(String[] args) {
        SpringApplication.run(WorkshopWatcherAgent.class, args);
    }
}
