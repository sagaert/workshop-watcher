package org.salex.raspberry.workshop;

import org.salex.raspberry.workshop.blog.Blog;
import org.salex.raspberry.workshop.photo.Photographer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private final Photographer photgrapher;

    private final Blog blog;

    public ScheduledTasks(Photographer photgrapher, Blog blog) {
        this.photgrapher = photgrapher;
        this.blog = blog;
    }

    // Alle 5 Minuten um 03,08,13,18,23,28,33,38,43,48,53 und 58
    @Scheduled(cron = "0 3/5 * * * *")
    public void photographer() throws IOException, InterruptedException {
        this.photgrapher.takePhotoForCache();
    }

    // Alle 10 Minuten um 05,15,25,35,45 und 55
    @Scheduled(cron = "0 0/10 * * * *")
    public void measure() {
        LOG.info("Measure, persist and publish");
    }

    // Jeden Tag um 00:00
    @Scheduled(cron = "0 0 0 * * *")
    public void history() {
        LOG.info("Update history");
    }

    // Jeden Tag um 06:00
    @Scheduled(cron = "0 0 6 * * *")
    public void alarmMailer() {
        LOG.info("Send alarm mail if necessary.");
    }

    // Jede Stunde
    @Scheduled(cron = "0 0 * * * *")
    public void photoMailer() {
        LOG.info("Send photo mail if necessary.");
    }
}
