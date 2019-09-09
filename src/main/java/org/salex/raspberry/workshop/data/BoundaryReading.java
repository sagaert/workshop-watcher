package org.salex.raspberry.workshop.data;

import java.util.Date;

public class BoundaryReading {
    private final double minimalHumidity;
    private final double maximalHumidity;
    private final double minimalTemperature;
    private final double maximalTemperature;
    private final Sensor sensor;
    private final Date day;

    public BoundaryReading(double minimalHumidity, double maximalHumidity, double minimalTemperature, double maximalTemperature, Sensor sensor, Date day) {
        this.minimalHumidity = minimalHumidity;
        this.maximalHumidity = maximalHumidity;
        this.minimalTemperature = minimalTemperature;
        this.maximalTemperature = maximalTemperature;
        this.sensor = sensor;
        this.day = day;
    }

    public double getMinimalHumidity() {
        return minimalHumidity;
    }

    public double getMaximalHumidity() {
        return maximalHumidity;
    }

    public double getMinimalTemperature() {
        return minimalTemperature;
    }

    public double getMaximalTemperature() {
        return maximalTemperature;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Date getDay() {
        return day;
    }
}
