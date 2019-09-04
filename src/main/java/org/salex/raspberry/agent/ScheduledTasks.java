package org.salex.raspberry.agent;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Scheduled(cron = "0 3/5 * * * *")
    public void photographer() {
        System.out.println("Taking a photo at " + dateTimeFormatter.format(LocalDateTime.now()));
    }
}
