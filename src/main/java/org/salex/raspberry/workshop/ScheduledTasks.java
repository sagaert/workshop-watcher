package org.salex.raspberry.workshop;

import org.salex.raspberry.workshop.blog.Blog;
import org.salex.raspberry.workshop.blog.Image;
import org.salex.raspberry.workshop.data.*;
import org.salex.raspberry.workshop.photo.Photographer;
import org.salex.raspberry.workshop.publish.BlogGenerator;
import org.salex.raspberry.workshop.publish.ChartGenerator;
import org.salex.raspberry.workshop.publish.MailGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;

@Component
public class ScheduledTasks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private final Gauger gauger;

    private final Photographer photographer;

    private final ClimateDatabase database;

    private final Blog blog;

    private final BlogGenerator blogGenerator;

    private final ChartGenerator chartGenerator;

    private final MailGenerator mailGenerator;

    private final JavaMailSender mailSender;

    private final String alertMailTarget;

    private final String photoMailTarget;

    public ScheduledTasks(Gauger gauger, Photographer photographer, ClimateDatabase database, Blog blog, BlogGenerator blogGenerator, ChartGenerator chartGenerator, MailGenerator mailGenerator, JavaMailSender mailSender,
                          @Value("${org.salex.mail.alert.target}") String alertMailTarget, @Value("${org.salex.mail.photo.target}") String phototMailTarget) {
        this.alertMailTarget = alertMailTarget;
        this.photoMailTarget = phototMailTarget;
        this.gauger = gauger;
        this.photographer = photographer;
        this.database = database;
        this.blog = blog;
        this.blogGenerator = blogGenerator;
        this.chartGenerator = chartGenerator;
        this.mailGenerator = mailGenerator;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "${org.salex.cron.photographer}")
    public void photographer() {
        try {
            this.photographer.takePhotoForCache();
        } catch (Throwable e) {
            LOG.error("Error taking photo", e);
        }
    }

    @Scheduled(cron = "${org.salex.cron.gauger}")
    public void measure() {
        try {
            final Measurement data = this.gauger.performMeasuring();
            this.database.addMeasurement(data);
            postOverviewOnBlog(data);
            postDetailsOnBlog(this.database.getMeasurements(24));
        } catch (Throwable e) {
            LOG.error("Error saving data input", e);
        }
    }

    @Scheduled(cron = "${org.salex.cron.historyPublisher}")
    public void history() {
        try {
            final Map<Sensor, List<BoundaryReading>> data = this.database.getBoundaryReading(365);
            final Map<Sensor, Map<String, Image>> diagrams = new HashMap<>();
            final List<Image> images = new ArrayList<Image>();
            for(Sensor sensor : this.database.getSensors()) {
                if(!diagrams.containsKey(sensor)) {
                    diagrams.put(sensor, new HashMap<String, Image>());
                }
                final Map<String, Image> diagramsForSensor = diagrams.get(sensor);
                Image image = this.blog.addPNGImage("temperature-", this.chartGenerator.create365DayTemperatureChart(data.get(sensor), sensor));
                diagramsForSensor.put("temperature", image);
                images.add(image);
                if(Sensor.Type.DHT22.equals(sensor.getType())) {
                    image = this.blog.addPNGImage("humidity-", this.chartGenerator.create365DayHumidityChart(data.get(sensor), sensor));
                    diagramsForSensor.put("humidity", image);
                    images.add(image);
                }
            }
            this.blog.updateHistory(this.blogGenerator.generateHistory(data, diagrams), images);
        } catch (Throwable e) {
            LOG.error("Error posting data on blog", e);
        }

    }

    @Scheduled(cron = "${org.salex.cron.alertMailer}")
    public void alarmMailer() {
        try {
            final List<Measurement> data = this.database.getMeasurements(24);
            boolean alert = false;
            for(Measurement measurement : data) {
                for(Reading reading : measurement.getReadings()) {
                    if(reading.getTemperature() < 3.0d) {
                        alert = true;
                    }
                }
            }
            if (alert) {
                try {
                    final MimeMessage message = this.mailSender.createMimeMessage();
                    message.setFrom(new InternetAddress("noreply@salex.org", "Werkstatt"));
                    message.setHeader("X-Priority", "1");
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(this.alertMailTarget));
                    message.setSubject("Temperaturalarm");
                    message.setContent(new MimeMultipart(this.mailGenerator.createAlertText(data)));
                    this.mailSender.send(message);
                } catch (MessagingException e) {
                    LOG.error("Error sending mail", e);
                }
            }
        } catch (Throwable e) {
            LOG.error("Error reading data", e);
        }
    }

    @Scheduled(cron = "${org.salex.cron.photoMailer}")
    public void photoMailer() {
        try {
            final Date now = new Date();
            final Map<String, String> photos = this.photographer.getPhotos();
            if (!photos.isEmpty()) {
                try {
                    final MimeMessage message = this.mailSender.createMimeMessage();
                    message.setFrom(new InternetAddress("noreply@salex.org", "Werkstatt"));
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(this.photoMailTarget));
                    message.setSubject("Werkstattfotos");
                    final MimeMultipart multipart = new MimeMultipart();
                    multipart.addBodyPart(this.mailGenerator.createPhotoText(now));
                    final List<String> mailedFiles = new ArrayList<String>();
                    for(String filename : photos.keySet()) {
                        final MimeBodyPart messageBodyPart = new MimeBodyPart();
                        messageBodyPart.setDataHandler(new DataHandler(new FileDataSource(photos.get(filename))));
                        messageBodyPart.setFileName(filename);
                        multipart.addBodyPart(messageBodyPart);
                        mailedFiles.add(filename);
                    }
                    message.setContent(multipart);
                    this.mailSender.send(message);
                    this.photographer.deletePhotos(mailedFiles);
                } catch (MessagingException e) {
                    LOG.error("Error sending mail", e);
                }
            }
        } catch (Throwable e) {
            LOG.error("Error reading data", e);
        }

    }

    private void postOverviewOnBlog(Measurement data) {
        try {
            this.blog.updateOverview(this.blogGenerator.generateOverview(data));
        } catch (Throwable e) {
            LOG.error("Error posting data on blog", e);
        }
    }

    private void postDetailsOnBlog(List<Measurement> data) {
        try {
            final Image diagram = this.blog.addPNGImage("verlauf-", this.chartGenerator.create24HourChart(data));
            final List<Image> images = new ArrayList<>();
            images.add(diagram);
            this.blog.updateDetails(this.blogGenerator.generateDetails(data, diagram), images);
        } catch (Throwable e) {
            LOG.error("Error posting data on blog", e);
        }
    }
}
