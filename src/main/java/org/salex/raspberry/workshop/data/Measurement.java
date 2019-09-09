package org.salex.raspberry.workshop.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Measurement {
    final private int id;
    final private Date timestamp;
    final private List<Reading> readings;

    public Measurement() {
        this(new Date());
    }

    public Measurement(Date timestamp) {
        this(-1, timestamp);
    }

    public Measurement(int id, Measurement measurement) {
        this(id, measurement.timestamp);
        this.readings.addAll(measurement.readings);
    }

    public Measurement(int id, java.sql.Date timestamp) {
        this(id, new Date(timestamp.getTime()));
    }

    public Measurement(int id, Date timestamp) {
        this.id = id;
        this.timestamp = timestamp;
        this.readings = new ArrayList<Reading>();
    }

    public int getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public List<Reading> getReadings() {
        return readings;
    }

    public Reading getReading(Sensor sensor) {
        Reading answer = null;
        for(Reading each : this.readings) {
            if(each.getSensor().getId() == sensor.getId()) {
                answer = each;
            }
        }
        return answer;
    }
}
