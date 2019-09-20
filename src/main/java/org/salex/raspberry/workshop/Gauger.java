package org.salex.raspberry.workshop;

import org.salex.raspberry.workshop.data.ClimateDatabase;
import org.salex.raspberry.workshop.data.Measurement;
import org.salex.raspberry.workshop.data.Reading;
import org.salex.raspberry.workshop.data.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class Gauger {
    private static final Logger LOG = LoggerFactory.getLogger(Gauger.class);

    private final ClimateDatabase database;

    private final String cpuMeasureScript;

    private final String dhtMeasureScript;

    public Gauger(@Value("${org.salex.raspberry.cpu.script}") String cpuMeasureScript, @Value("${org.salex.raspberry.dht.script}") String dhtMeasureScript, ClimateDatabase database) {
        this.database = database;
        this.cpuMeasureScript = cpuMeasureScript;
        this.dhtMeasureScript = dhtMeasureScript;
    }

    public Measurement performMeasuring() throws IOException {
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
        final Process p = Runtime.getRuntime().exec(new String[] { this.cpuMeasureScript, "measure_temp" });
        final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
        final String[] result = reader.readLine().split("[=']");
        return new Reading(Double.parseDouble(result[1]), sensor, measurement);
    }

    private Reading performMeasuringDHT22(Measurement measurement, Sensor sensor) throws IOException {
        final Process p = Runtime.getRuntime()
                .exec(new String[] { this.dhtMeasureScript, sensor.getPort() });
        final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
        final String[] result = reader.readLine().split(";");
        return new Reading(Double.parseDouble(result[1]), Double.parseDouble(result[0]), sensor, measurement);
    }

}
