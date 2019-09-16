package org.salex.raspberry.workshop.publish;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.salex.raspberry.workshop.data.ClimateDatabase;
import org.salex.raspberry.workshop.data.Measurement;
import org.salex.raspberry.workshop.data.Sensor;
import org.springframework.stereotype.Component;

@Component
public class MailGenerator {
	private final List<Sensor> sensors;
	
	public MailGenerator(ClimateDatabase db) {
		this.sensors = db.getSensors();
	}
	
	public MimeBodyPart createPhotoText(Date now) throws MessagingException {
		final StringBuffer text = new StringBuffer();
		text.append("<p>Anbei die Fotos vom ");
		text.append(GeneratorUtils.timestampFormatter.format(now));
		text.append("</p>");
		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(text.toString(), "text/html");
		return textPart;
	}
	
	public MimeBodyPart createAlertText(List<Measurement> data) throws MessagingException {
		final Measurement[] sorted = data.toArray(new Measurement[data.size()]);
		
		// Period
		Arrays.sort(sorted, new Comparator<Measurement>() {
			public int compare(Measurement one, Measurement another) {
				return one.getTimestamp().compareTo(another.getTimestamp());
			}
		});
		final Date periodStart = sorted[0].getTimestamp();
		final Date periodEnd = sorted[sorted.length - 1].getTimestamp();

		// Here comes the new stuff
		final StringBuffer text = new StringBuffer();
		text.append("<p>Im Zeitraum von ");
		text.append(GeneratorUtils.timestampFormatter.format(periodStart));
		text.append(" bis ");
		text.append(GeneratorUtils.timestampFormatter.format(periodEnd));
		text.append(" ist die Temperatur bei mindestens einem Sensor unter 3.0 Grad gesunken!<p>");
		GeneratorUtils.appendTable(text, data, sensors);
		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(text.toString(), "text/html");
		return textPart;

	}

}
