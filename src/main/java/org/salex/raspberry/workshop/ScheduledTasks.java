package org.salex.raspberry.workshop;

import com.sun.activation.registries.MailcapFile;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ScheduledTasks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private final Photographer photographer;

    private final ClimateDatabase database;

    private final Blog blog;

    private final BlogGenerator blogGenerator;

    private final ChartGenerator chartGenerator;

    private final MailGenerator mailGenerator;

    private final Session mailSession;

    public ScheduledTasks(Photographer photographer, ClimateDatabase database, Blog blog, BlogGenerator blogGenerator, ChartGenerator chartGenerator, MailGenerator mailGenerator, @Value("${org.salex.mail.username}") String username,
                          @Value("${org.salex.mail.password}") String password) {
        this.photographer = photographer;
        this.database = database;
        this.blog = blog;
        this.blogGenerator = blogGenerator;
        this.chartGenerator = chartGenerator;
        this.mailGenerator = mailGenerator;
        this.mailSession = getMailSession(username, password);
    }

    private Session getMailSession(String username, String password) {
        final Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.smtp.host", "smtp.1und1.de");
        mailProperties.put("mail.smtp.auth", "true");
        final PasswordAuthentication authentication = new PasswordAuthentication(username, password);
        return Session.getDefaultInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return authentication;
            }
        });
    }

    // Alle 5 Minuten um 03,08,13,18,23,28,33,38,43,48,53 und 58
    @Scheduled(cron = "0 3/5 * * * *")
    public void photographer() {
        try {
            this.photographer.takePhotoForCache();
        } catch (Throwable e) {
            LOG.error("Error taking photo", e);
        }
    }

    // Alle 10 Minuten um 05,15,25,35,45 und 55
    @Scheduled(cron = "0 5/10 * * * *")
    public void measure() {
        try {
            final Measurement data = performMeasuring();
            this.database.addMeasurement(data);
            postOverviewOnBlog(data);
            postDetailsOnBlog(this.database.getMeasurements(24));
        } catch (Throwable e) {
            LOG.error("Error saving data input", e);
        }
    }

    // Jeden Tag um 00:00
    @Scheduled(cron = "0 0 0 * * *")
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

    // Jeden Tag um 06:00
    @Scheduled(cron = "0 0 6 * * *")
    public void alarmMailer() {
        LOG.info("Send alarm mail if necessary.");
    }

    // Jede Stunde
    @Scheduled(cron = "0 0 * * * *")
    public void photoMailer() {
        try {
            final Date now = new Date();
            final Map<String, String> photos = this.photographer.getPhotos();
            if (!photos.isEmpty()) {
                try {
                    final Message message = new MimeMessage(mailSession);
                    message.setFrom(new InternetAddress("noreply@salex.org", "Werkstatt"));
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress("sascha.gaertner@salex.org"));
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
                    Transport.send(message);
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

    private Measurement performMeasuring() throws IOException {
        final Measurement data = new Measurement();
        for(Sensor sensor : this.database.getSensors()) {
            if(sensor.getType().equals(Sensor.Type.CPU)) {
                data.getReadings().add(performMeasuringCPU(data, sensor));
            } else if(sensor.getType().equals(Sensor.Type.DHT22)) {
                data.getReadings().add(performMeasuringDHT22(data, sensor));
            } else {
                LOG.error("Error on measuring: Unknown sensor type " + sensor.getType());
            }
        }
        return data;
    }

    private Reading performMeasuringCPU(Measurement measurement, Sensor sensor) throws IOException {
        final Process p = Runtime.getRuntime().exec(new String[] { "/usr/bin/vcgencmd", "measure_temp" });
        final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
        final String[] result = reader.readLine().split("[=']");
        return new Reading(Double.parseDouble(result[1]), sensor, measurement);
    }

    private Reading performMeasuringDHT22(Measurement measurement, Sensor sensor) throws IOException {
        final Process p = Runtime.getRuntime()
                .exec(new String[] { "/home/pi/Sensors/ReadDHT22.py", sensor.getPort() });
        final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
        final String[] result = reader.readLine().split(";");
        return new Reading(Double.parseDouble(result[1]), Double.parseDouble(result[0]), sensor, measurement);
    }
}
