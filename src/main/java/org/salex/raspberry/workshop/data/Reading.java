package org.salex.raspberry.workshop.data;

import java.util.Date;

public class Reading {
    private final double humidity;
    private final double temperature;
    private final Sensor sensor;
    private final Measurement measurement;

    public Reading(double temperature, Sensor sensor, Measurement measurement) {
        this(0.0, temperature, sensor, measurement);
    }

    public Reading(double humidity, double temperature, Sensor sensor, Measurement measurement) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.sensor = sensor;
        this.measurement = measurement;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public Date getTimestamp() {
        return this.measurement.getTimestamp();
    }

}
