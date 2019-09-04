package org.salex.raspberry.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WorkshopAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkshopAgentApplication.class, args);
    }
}
