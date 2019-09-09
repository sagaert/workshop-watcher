package org.salex.raspberry.workshop.publish;

import java.util.*;

import org.salex.raspberry.workshop.blog.Image;
import org.salex.raspberry.workshop.data.BoundaryReading;
import org.salex.raspberry.workshop.data.Measurement;
import org.salex.raspberry.workshop.data.Reading;
import org.salex.raspberry.workshop.data.Sensor;

public class BlogGenerator {
	private final List<Sensor> sensors;
	
	public BlogGenerator(List<Sensor> sensors) {
		this.sensors = sensors;
	}
	
	public String generateOverview(Measurement data) {
		final StringBuffer content = new StringBuffer();
		content.append("<span class=\"salex_no-series-meta-information\">");
		for(Reading reading : data.getReadings()) {
			if(reading.getSensor().getType().equals(Sensor.Type.DHT22)) {
				content.append("<p style=\"text-align: left;\">");
				content.append(reading.getSensor().getName());
				content.append(": <strong><span style=\"color: ");
				content.append(GeneratorUtils.getTempColor(reading.getTemperature()));
				content.append(";\">");
				content.append(GeneratorUtils.temperatureFormatter.format(reading.getTemperature()));
				content.append(" °C</span></strong> bei <span style=\"color: ");
				content.append(GeneratorUtils.getHumidityColor(reading.getHumidity()));
				content.append(";\">");
				content.append(GeneratorUtils.humidityFormatter.format(reading.getHumidity()));
				content.append(" %</span></p>");
			}
		}
		content.append("<p style=\"text-align: left;\"><span style=\"color: #808080;\">Gemessen am ");
		content.append(GeneratorUtils.dateFormatter.format(data.getTimestamp()));
		content.append(" um ");
		content.append(GeneratorUtils.timeFormatter.format(data.getTimestamp()));
		content.append("</span></p>");
		content.append("<p style=\"text-align: left;\"><a href=\"https://holzwerken.salex.org/werkstattklima\">Details ansehen</a></p>");
		content.append("<p style=\"text-align: left;\"><a href=\"https://holzwerken.salex.org/werkstattklima-entwicklung\">Jahresverlauf ansehen</a></p>");
		content.append("</span>");
		return content.toString();
	}
	
	public String generateDetails(List<Measurement> data, Image diagram) {
		final StringBuffer content = new StringBuffer();
		Collections.sort(data, new Comparator<Measurement>() {
			public int compare(Measurement one, Measurement another) {
				return one.getTimestamp().compareTo(another.getTimestamp());
			}
		});
		if(!data.isEmpty()) {
			final Date periodStart = data.get(0).getTimestamp();
			final Date periodEnd = data.get(data.size()-1).getTimestamp();		
			content.append("<h3>Zeitraum von ");
			content.append(GeneratorUtils.timestampFormatter.format(periodStart));
			content.append(" bis ");
			content.append(GeneratorUtils.timestampFormatter.format(periodEnd));
			content.append("</h3>");
			content.append("<img class=\"aligncenter size-full wp-image-");
			content.append(diagram.getId());
			content.append("\" src=\"");
			content.append(diagram.getFull());
			content.append("\" alt=\"\" width=\"600\" height=\"300\" />");
			GeneratorUtils.appendTable(content, data, sensors);
		} else {
			content.append("<h3>Keine Daten vorhanden</h3>");
		}
		return content.toString();
	}
	
	public String generateHistory(Map<Sensor, List<BoundaryReading>> data, Map<Sensor, Map<String, Image>> diagrams) {
		final StringBuffer content = new StringBuffer();
		final List<BoundaryReading> all = new ArrayList<BoundaryReading>();
		for(List<BoundaryReading> each : data.values()) {
			all.addAll(each);
		}
		Collections.sort(all, new Comparator<BoundaryReading>() {
			public int compare(BoundaryReading one, BoundaryReading another) {
				return one.getDay().compareTo(another.getDay());
			}
		});
		if(!data.isEmpty()) {
			final Date periodStart = all.get(0).getDay();
			final Date periodEnd = all.get(all.size()-1).getDay();		
			content.append("<h3>Zeitraum von ");
			content.append(GeneratorUtils.dateFormatter.format(periodStart));
			content.append(" bis ");
			content.append(GeneratorUtils.dateFormatter.format(periodEnd));
			content.append("</h3>");
			GeneratorUtils.appendTable(content, data, diagrams, this.sensors);
		} else {
			content.append("<h3>Keine Daten vorhanden</h3>");
		}
		return content.toString();
	}
}
