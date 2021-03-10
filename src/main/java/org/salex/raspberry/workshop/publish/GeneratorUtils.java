package org.salex.raspberry.workshop.publish;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.salex.raspberry.workshop.blog.Image;
import org.salex.raspberry.workshop.data.BoundaryReading;
import org.salex.raspberry.workshop.data.Measurement;
import org.salex.raspberry.workshop.data.Sensor;

public class GeneratorUtils {
	public static final SimpleDateFormat timestampFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
	public static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
	public static final NumberFormat temperatureFormatter = createNumberFormat("+##0.0;-");
	public static final NumberFormat humidityFormatter = createNumberFormat("##0.0");	
	
	public static String getTempColor(double temp) {
		if (temp <= 0.0) {
			return "#180795"; // dark blue
		} else if (temp < 10.0) {
			return "#0056d6"; // blue
		} else if (temp < 25.0) {
			return "#099f23"; // green
		} else if (temp < 35.0) {
			return "#dd7b1d"; // orange
		} else {
			return "#d60a13"; // red
		}
	}

	public static String getHumidityColor(double humidity) {
		if (humidity < 10.0 || humidity > 90.0) {
			return "#d60a13"; // red
		} else if (humidity < 25.0 || humidity > 75.0) {
			return "#dd7b1d"; // orange
		} else {
			return "#099f23"; // green
		}
	}
	
	private static NumberFormat createNumberFormat(String pattern) {
		final NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
		if(format instanceof DecimalFormat) {
			((DecimalFormat) format).applyPattern(pattern);
 		}
		return format;
	}

	public static void appendTable(StringBuffer text, Map<Sensor, List<BoundaryReading>> data, Map<Sensor, Map<String, Image>> diagrams, List<Sensor> sensors) {
		text.append("<table border=\"0\">");
		text.append("<tr>");
		text.append("<th rowspan=\"2\" width=\"80px\">&nbsp;</th>");
		text.append("<th colspan=\"2\" style=\"border-bottom-width: 2px; border-bottom-color: #999999; border-bottom-style: solid;\"><span style=\"font-size: 18px; color: #666666;\">Temperatur</span></th>");
		text.append("<th colspan=\"2\" style=\"border-bottom-width: 2px; border-bottom-color: #999999; border-bottom-style: dashed;\"><span style=\"font-size: 18px; color: #666666;\">Luftfeuchtigkeit</span></th>");
		text.append("</tr>");
		text.append("<tr>");
		text.append("<th width=\"120px\"><span style=\"font-size: 18px; color: #666666;\">Minimum</span></th>");
		text.append("<th width=\"120px\"><span style=\"font-size: 18px; color: #666666;\">Maximum</span></th>");
		text.append("<th width=\"120px\"><span style=\"font-size: 18px; color: #666666;\">Minimum</span></th>");
		text.append("<th width=\"120px\"><span style=\"font-size: 18px; color: #666666;\">Maximum</span></th>");
		text.append("</tr>");
		
		for(final Sensor sensor : sensors) {
			final List<BoundaryReading> list = data.get(sensor);

			if(list != null) {
				// Diagrams
				text.append("<tr>");
				text.append("<td rowspan=\"2\"><span style=\"font-size: 18px; color: ");
				text.append(sensor.getColorHex());
				text.append(";\">");
				text.append(sensor.getName());
				text.append("<span></td>");
				Image tempDiagram = diagrams.get(sensor).get("temperature");
				text.append("<td colspan=\"2\" style=\"padding: 20px 0px\"><a href=\"");
				text.append(tempDiagram.getFull());
				text.append("\"><img class=\"aligncenter wp-image-");
				text.append(tempDiagram.getId());
				text.append(" size-medium\" src=\"");
				text.append(tempDiagram.getThumbnail());
				text.append("\" alt=\"\" width=\"");
				text.append(tempDiagram.getThumbnailWidth());
				text.append("\" height=\"");
				text.append(tempDiagram.getThumbnailHeight());
				text.append("\" /></a></td>");
				text.append("<td colspan=\"2\" style=\"padding: 20px 0px\">");
				if(Sensor.Type.DHT22.equals(sensor.getType())) {
					Image humDiagram = diagrams.get(sensor).get("humidity");
					text.append("<a href=\"");
					text.append(humDiagram.getFull());
					text.append("\"><img class=\"aligncenter wp-image-");
					text.append(humDiagram.getId());
					text.append(" size-medium\" src=\"");
					text.append(humDiagram.getThumbnail());
					text.append("\" alt=\"\" width=\"");
					text.append(humDiagram.getThumbnailWidth());
					text.append("\" height=\"");
					text.append(humDiagram.getThumbnailHeight());
					text.append("\" /></a>");
				} else {
					text.append("&nbsp;");
				}
				text.append("</td></tr>");				
				
				// Temperature
				Collections.sort(list, new Comparator<BoundaryReading>() {
					public int compare(BoundaryReading one, BoundaryReading another) {
						return Double.compare(one.getMinimalTemperature(), another.getMinimalTemperature());
					}
				});
				final double minTemperature = list.get(0).getMinimalTemperature();
				final Date minTemperatureTimestamp = list.get(0).getDay();
				Collections.sort(list, new Comparator<BoundaryReading>() {
					public int compare(BoundaryReading one, BoundaryReading another) {
						return Double.compare(another.getMaximalTemperature(), one.getMaximalTemperature());
					}
				});
				final double maxTemperature = list.get(0).getMaximalTemperature();
				final Date maxTemperatureTimestamp = list.get(0).getDay();
	
				// Humidity
				Collections.sort(list, new Comparator<BoundaryReading>() {
					public int compare(BoundaryReading one, BoundaryReading another) {
						return Double.compare(one.getMinimalHumidity(), another.getMinimalHumidity());
					}
				});
				final double minHumidity = list.get(0).getMinimalHumidity();
				final Date minHumidityTimestamp = list.get(0).getDay();
				Collections.sort(list, new Comparator<BoundaryReading>() {
					public int compare(BoundaryReading one, BoundaryReading another) {
						return Double.compare(another.getMaximalHumidity(), one.getMaximalHumidity());
					}
				});
				final double maxHumidity = list.get(0).getMaximalHumidity();
				final Date maxHumidityTimestamp = list.get(0).getDay();
				
				// Output data
				text.append("<tr>");
				text.append("<td align=\"center\"><span style=\"font-size: 18px; color: ");
				if(sensor.getType().equals(Sensor.Type.DHT22)) {
					text.append(getTempColor(minTemperature));
				} else {
					text.append("#666666");
				}
				text.append(";\">");
				text.append(temperatureFormatter.format(minTemperature));
				text.append("&nbsp;&deg;C</span><br />");
				text.append("<span style=\"font-size: 10px; color: gray;\">");
				text.append(dateFormatter.format(minTemperatureTimestamp));
				text.append("</span></td>");
				text.append("<td align=\"center\"><span style=\"font-size: 18px; color: ");
				if(sensor.getType().equals(Sensor.Type.DHT22)) {
					text.append(getTempColor(maxTemperature));
				} else {
					text.append("#666666");
				}
				text.append(";\">");
				text.append(temperatureFormatter.format(maxTemperature));
				text.append("&nbsp;&deg;C</span><br />");
				text.append("<span style=\"font-size: 10px; color: gray;\">");
				text.append(dateFormatter.format(maxTemperatureTimestamp));
				text.append("</span></td>");
				text.append("<td align=\"center\"><span style=\"font-size: 18px; color: ");
				if(sensor.getType().equals(Sensor.Type.DHT22)) {
					text.append(getHumidityColor(minHumidity));
				} else {
					text.append("#666666");
				}
				text.append(";\">");
				text.append(humidityFormatter.format(minHumidity));
				text.append("&nbsp;%</span><br />");
				text.append("<span style=\"font-size: 10px; color: gray;\">");
				text.append(dateFormatter.format(minHumidityTimestamp));
				text.append("</span></td>");
				text.append("<td align=\"center\"><span style=\"font-size: 18px; color: ");
				if(sensor.getType().equals(Sensor.Type.DHT22)) {
					text.append(getHumidityColor(maxHumidity));
				} else {
					text.append("#666666");
				}
				text.append(";\">");
				text.append(humidityFormatter.format(maxHumidity));
				text.append("&nbsp;%</span><br />");
				text.append("<span style=\"font-size: 10px; color: gray;\">");
				text.append(dateFormatter.format(maxHumidityTimestamp));
				text.append("</span></td>");
				text.append("</tr>");
			}
		}
		text.append("</table>");
	}
	
	public static void appendTable(StringBuffer text, List<Measurement> data, List<Sensor> sensors) {
		final Measurement[] sorted = data.toArray(new Measurement[data.size()]);
		text.append("<table border=\"0\">");
		text.append("<tr>");
		text.append("<th rowspan=\"2\" width=\"80px\">&nbsp;</th>");
		text.append("<th colspan=\"2\" style=\"border-bottom-width: 2px; border-bottom-color: #999999; border-bottom-style: solid;\"><span style=\"font-size: 18px; color: #666666;\">Temperatur</span></th>");
		text.append("<th colspan=\"2\" style=\"border-bottom-width: 2px; border-bottom-color: #999999; border-bottom-style: dashed;\"><span style=\"font-size: 18px; color: #666666;\">Luftfeuchtigkeit</span></th>");
		text.append("</tr>");
		text.append("<tr>");
		text.append("<th width=\"120px\"><span style=\"font-size: 18px; color: #666666;\">Minimum</span></th>");
		text.append("<th width=\"120px\"><span style=\"font-size: 18px; color: #666666;\">Maximum</span></th>");
		text.append("<th width=\"120px\"><span style=\"font-size: 18px; color: #666666;\">Minimum</span></th>");
		text.append("<th width=\"120px\"><span style=\"font-size: 18px; color: #666666;\">Maximum</span></th>");
		text.append("</tr>");
	
		for(final Sensor sensor : sensors) {
			// Temperature
			Arrays.sort(sorted, new Comparator<Measurement>() {
				public int compare(Measurement one, Measurement another) {
					return Double.compare(one.getReading(sensor).getTemperature(), another.getReading(sensor).getTemperature());
				}
			});
			final double minTemperature = sorted[0].getReading(sensor).getTemperature();
			final Date minTemperatureTimestamp = sorted[0].getTimestamp();
			final double maxTemperature = sorted[sorted.length - 1].getReading(sensor).getTemperature();
			final Date maxTemperatureTimestamp = sorted[sorted.length - 1].getTimestamp();

			// Humidity
			Arrays.sort(sorted, new Comparator<Measurement>() {
				public int compare(Measurement one, Measurement another) {
					return Double.compare(one.getReading(sensor).getHumidity(), another.getReading(sensor).getHumidity());
				}
			});
			final double minHumidity = sorted[0].getReading(sensor).getHumidity();
			final Date minHumidityTimestamp = sorted[0].getTimestamp();
			final double maxHumidity = sorted[sorted.length - 1].getReading(sensor).getHumidity();
			final Date maxHumidityTimestamp = sorted[sorted.length - 1].getTimestamp();
			
			// Output
			text.append("<tr>");
			text.append("<td><span style=\"font-size: 18px; color: ");
			text.append(sensor.getColorHex());
			text.append(";\">");
			text.append(sensor.getName());
			text.append("<span></td>");
			text.append("<td align=\"center\"><span style=\"font-size: 18px; color: ");
			if(sensor.getType().equals(Sensor.Type.DHT22)) {
				text.append(getTempColor(minTemperature));
			} else {
				text.append("#666666");
			}
			text.append(";\">");
			text.append(temperatureFormatter.format(minTemperature));
			text.append("&nbsp;&deg;C</span><br />");
			text.append("<span style=\"font-size: 10px; color: gray;\">");
			text.append(timestampFormatter.format(minTemperatureTimestamp));
			text.append("</span></td>");
			text.append("<td align=\"center\"><span style=\"font-size: 18px; color: ");
			if(sensor.getType().equals(Sensor.Type.DHT22)) {
				text.append(getTempColor(maxTemperature));
			} else {
				text.append("#666666");
			}
			text.append(";\">");
			text.append(temperatureFormatter.format(maxTemperature));
			text.append("&nbsp;&deg;C</span><br />");
			text.append("<span style=\"font-size: 10px; color: gray;\">");
			text.append(timestampFormatter.format(maxTemperatureTimestamp));
			text.append("</span></td>");
			text.append("<td align=\"center\"><span style=\"font-size: 18px; color: ");
			if(sensor.getType().equals(Sensor.Type.DHT22)) {
				text.append(getHumidityColor(minHumidity));
			} else {
				text.append("#666666");
			}
			text.append(";\">");
			text.append(humidityFormatter.format(minHumidity));
			text.append("&nbsp;%</span><br />");
			text.append("<span style=\"font-size: 10px; color: gray;\">");
			text.append(timestampFormatter.format(minHumidityTimestamp));
			text.append("</span></td>");
			text.append("<td align=\"center\"><span style=\"font-size: 18px; color: ");
			if(sensor.getType().equals(Sensor.Type.DHT22)) {
				text.append(getHumidityColor(maxHumidity));
			} else {
				text.append("#666666");
			}
			text.append(";\">");
			text.append(humidityFormatter.format(maxHumidity));
			text.append("&nbsp;%</span><br />");
			text.append("<span style=\"font-size: 10px; color: gray;\">");
			text.append(timestampFormatter.format(maxHumidityTimestamp));
			text.append("</span></td>");
			text.append("</tr>");
		}
		text.append("</table>");
	}
}
