package org.salex.raspberry.workshop.publish;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.salex.raspberry.workshop.data.*;
import org.springframework.stereotype.Component;

@Component
public class ChartGenerator {
	private final List<Sensor> sensors;
	
	public ChartGenerator(ClimateDatabase db) {
		this.sensors = db.getSensors();
	}
	
	public byte[] create365DayTemperatureChart(List<BoundaryReading> data, Sensor sensor) throws IOException {
		// Sort data by timestamp ascending
		Collections.sort(data, new Comparator<BoundaryReading>() {
			public int compare(BoundaryReading one, BoundaryReading another) {
				return one.getDay().compareTo(another.getDay());
			}
		});
		
		// Create plot with axis
		final XYPlot plot = new XYPlot();
		final NumberAxis temperatureAxis = new NumberAxis("Temperatur in °C");
		if(Sensor.Type.CPU.equals(sensor.getType())) {
			temperatureAxis.setRange(0, 80);
		} else {
			temperatureAxis.setRange(-15, 40);
		}
		temperatureAxis.setTickUnit(new NumberTickUnit(5));
		final DateAxis timeAxis = new DateAxis("Monat");
		if(!data.isEmpty()) {
			timeAxis.setRange(data.get(0).getDay(), data.get(data.size()-1).getDay());
		}
		timeAxis.setDateFormatOverride(new SimpleDateFormat("MM.YYYY"));
		timeAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1));
		plot.setRangeAxis(temperatureAxis);
		plot.setDomainAxis(timeAxis);
		
		// Create time series with collection
		final TimeSeries maxSeries = new TimeSeries("Maximum");
		final TimeSeries minSeries = new TimeSeries("Minimum");
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(maxSeries);
        dataset.addSeries(minSeries);
        plot.setDataset(dataset);
        
        // Transfer boundary data into time series
        for(BoundaryReading reading : data) {
        	final Day day = new Day(reading.getDay());
        	maxSeries.add(day, reading.getMaximalTemperature());
        	minSeries.add(day, reading.getMinimalTemperature());
        }
        
		// generate the chart and return png as byte array
        final XYDifferenceRenderer renderer = new XYDifferenceRenderer(Color.gray, Color.gray, false);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart("Temperaturverlauf " + sensor.getName(), null, plot, false);
        chart.setBackgroundPaint(null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsPNG(baos, chart, 800, 600);
		return baos.toByteArray();
	}
	
	public byte[] create365DayHumidityChart(List<BoundaryReading> data, Sensor sensor) throws IOException {
		// Sort data by timestamp ascending
		Collections.sort(data, new Comparator<BoundaryReading>() {
			public int compare(BoundaryReading one, BoundaryReading another) {
				return one.getDay().compareTo(another.getDay());
			}
		});
		
		// Create plot with axis
		final XYPlot plot = new XYPlot();
		final NumberAxis humidityAxis = new NumberAxis("Relative Luftfeuchtigkeit in %");
		humidityAxis.setRange(0, 100);
		humidityAxis.setTickUnit(new NumberTickUnit(10));
		final DateAxis timeAxis = new DateAxis("Monat");
		if(!data.isEmpty()) {
			timeAxis.setRange(data.get(0).getDay(), data.get(data.size()-1).getDay());
		}
		timeAxis.setDateFormatOverride(new SimpleDateFormat("MM.YYYY"));
		timeAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1));
		plot.setRangeAxis(humidityAxis);
		plot.setDomainAxis(timeAxis);
		
		// Create time series with collection
		final TimeSeries maxSeries = new TimeSeries("Maximum");
		final TimeSeries minSeries = new TimeSeries("Minimum");
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(maxSeries);
        dataset.addSeries(minSeries);
        plot.setDataset(dataset);
        
        // Transfer boundary data into time series
        for(BoundaryReading reading : data) {
        	final Day day = new Day(reading.getDay());
        	maxSeries.add(day, reading.getMaximalHumidity());
        	minSeries.add(day, reading.getMinimalHumidity());
        }
        
		// generate the chart and return png as byte array
        final XYDifferenceRenderer renderer = new XYDifferenceRenderer(Color.gray, Color.gray, false);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart("Verlauf der relative Luftfeuchtigkeit " + sensor.getName(), null, plot, false);
        chart.setBackgroundPaint(null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsPNG(baos, chart, 800, 600);
		return baos.toByteArray();
	}
	
	public byte[] create24HourChart(List<Measurement> data) throws IOException {
		// Sort data by timestamp ascending
		Collections.sort(data, new Comparator<Measurement>() {
			public int compare(Measurement one, Measurement another) {
				return one.getTimestamp().compareTo(another.getTimestamp());
			}
		});

		// Create plot with axis
		final XYPlot plot = new XYPlot();
		final NumberAxis temperatureAxis = new NumberAxis("Temperatur in °C");
		temperatureAxis.setRange(-15, 40);
		temperatureAxis.setTickUnit(new NumberTickUnit(5));
		final NumberAxis humidityAxis = new NumberAxis("Relative Luftfeuchtigkeit in %");
		humidityAxis.setRange(0, 100);
		humidityAxis.setTickUnit(new NumberTickUnit(10));
		final DateAxis timeAxis = new DateAxis("Zeit in Stunden");
		if(!data.isEmpty()) {
			timeAxis.setRange(data.get(0).getTimestamp(), data.get(data.size()-1).getTimestamp());
		}
		timeAxis.setDateFormatOverride(new SimpleDateFormat("HH"));
		timeAxis.setTickUnit(new DateTickUnit(DateTickUnitType.HOUR, 1));
		plot.setRangeAxis(0, temperatureAxis);
		plot.setRangeAxis(1, humidityAxis);
		plot.setDomainAxis(timeAxis);
				
		// Create time series with collection and plot with renderers
		final Map<Sensor, TimeSeries> tempSeries = new HashMap<Sensor, TimeSeries>();
		final Map<Sensor, TimeSeries> humSeries = new HashMap<Sensor, TimeSeries>();
		int datasetNumber = 0;
		for(Sensor sensor : this.sensors) {
			if(sensor.getType().equals(Sensor.Type.DHT22)) {
				// Create temperature series for sensor
				TimeSeries series = new TimeSeries(sensor.getName());
				tempSeries.put(sensor, series);
				plot.setDataset(datasetNumber, createTimeSeriesCollection(series));
				plot.setRenderer(datasetNumber, createRenderer(sensor.getColor(), false));
				plot.mapDatasetToRangeAxis(datasetNumber, 0);
				datasetNumber++;
				
				// Create humidity series for sensor
				series = new TimeSeries(sensor.getName());
				humSeries.put(sensor, series);
				plot.setDataset(datasetNumber, createTimeSeriesCollection(series));
				plot.setRenderer(datasetNumber, createRenderer(sensor.getColor(), true));
				plot.mapDatasetToRangeAxis(datasetNumber, 1);
				datasetNumber++;
			}
		}
		
		// Transfer reading data into time series
		for(Measurement measurement : data) {
			for(Reading reading : measurement.getReadings()) {
				if(reading.getSensor().getType().equals(Sensor.Type.DHT22)) {
					tempSeries.get(reading.getSensor()).add(new Minute(measurement.getTimestamp()), reading.getTemperature());
					humSeries.get(reading.getSensor()).add(new Minute(measurement.getTimestamp()), reading.getHumidity());
				}
			}
		}
		
		// generate the chart and return png as byte array
		JFreeChart chart = new JFreeChart(null, null, plot, false);
		chart.setBackgroundPaint(null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsPNG(baos, chart, 600, 300);
		return baos.toByteArray();
	}

	private XYDataset createTimeSeriesCollection(TimeSeries series) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(series);
		return dataset;
	}

	private XYSplineRenderer createRenderer(final Paint paint, final boolean dashed) {
		final XYSplineRenderer renderer = new XYSplineRenderer();
		renderer.setSeriesPaint(0, paint);
		renderer.setSeriesShapesVisible(0, false);
		if (dashed) {
			renderer.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
					new float[] { 2.0f, 5.0f }, 0.0f));
		} else {
			renderer.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f));
		}
		return renderer;
	}
}
