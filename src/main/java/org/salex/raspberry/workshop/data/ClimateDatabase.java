package org.salex.raspberry.workshop.data;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Repository
public class ClimateDatabase {
    private static final Logger LOG = LoggerFactory.getLogger(ClimateDatabase.class);

    private JdbcTemplate jdbcTemplate;

    public ClimateDatabase(JdbcTemplate jdbcTemplate) throws SQLException {
        this.jdbcTemplate = jdbcTemplate;

        if(tableExists("sensors")) {
            LOG.info("Table sensors already exists.");
        } else {
            LOG.info("Creating table sensors...");
            this.jdbcTemplate.execute("create table sensors(id int not null, name varchar(32) not null, color varchar(16) not null, kind varchar(32) not null, port int, primary key (id))");
            LOG.info("Initializing sensor data...");
            this.jdbcTemplate.execute("insert into sensors(id, name, color, kind) values (1, 'Raspberry', '#666666', 'CPU')");
            this.jdbcTemplate.execute("insert into sensors(id, name, color, kind, port) values (2, 'Maschinenraum', '#71893F', 'DHT22', 5)");
            this.jdbcTemplate.execute("insert into sensors(id, name, color, kind, port) values (3, 'Werkraum', '#517199', 'DHT22', 4)");
            this.jdbcTemplate.execute("insert into sensors(id, name, color, kind, port) values (4, 'Außen', '#7B219F', 'DHT22', 17)");
        }

        if(tableExists("measurements")) {
            LOG.info("Table measurements already exists.");
        } else {
            LOG.info("Creating table measurements...");
            this.jdbcTemplate.execute("create table measurements(id int not null primary key generated always as identity (start with 1, increment by 1), moment timestamp not null)");
        }

        if(tableExists("readings")) {
            LOG.info("Table readings already exists.");
        } else {
            LOG.info("Creating table readings...");
            this.jdbcTemplate.execute("create table readings(measurement int not null, sensor int not null, temperature double, humidity double, primary key(measurement, sensor), foreign key (measurement) references measurements(id), foreign key (sensor) references sensors(id) )");
        }

        LOG.info("Loading sensor data...");
        this.sensors = loadSensors();

        LOG.info("Database ready to rumble!");
    }

    private boolean tableExists(String name) throws SQLException {
        DatabaseMetaData meta = this.jdbcTemplate.getDataSource().getConnection().getMetaData();
        return meta.getTables(null, null, name.toUpperCase(), null).next();
    }

    private List<Sensor> sensors;

    private List<Sensor> loadSensors() {
        return this.jdbcTemplate.query("select * from sensors order by id", (row, rowNum) -> new Sensor(row.getInt("id"), row.getString("name"), row.getString("color"), row.getString("kind"), row.getString("port")));
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public Measurement addMeasurement(Measurement measurement) {
        if(measurement.getId() == -1) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(connection -> {
                PreparedStatement measurementStatement = connection.prepareStatement("insert into measurements(moment) values (?)");
                measurementStatement.setTimestamp(1, new Timestamp(measurement.getTimestamp().getTime()));
                return measurementStatement;
            }, keyHolder);
            final int measurementId = (int) keyHolder.getKey();
            for(Reading reading : measurement.getReadings()) {
                this.jdbcTemplate.update(connection -> {
                    PreparedStatement readingStatement = connection.prepareStatement("insert into readings (measurement, sensor, temperature, humidity) values (?, ?, ?, ?)");
                    readingStatement.setInt(1, measurementId);
                    readingStatement.setInt(2, reading.getSensor().getId());
                    readingStatement.setDouble(3,reading.getTemperature());
                    readingStatement.setDouble(4, reading.getHumidity());
                    return readingStatement;
                });
            }
            return new Measurement(measurementId, measurement);
        } else {
            return measurement;
        }
    }

    public List<Measurement> getMeasurements(int hours) {
        return this.jdbcTemplate.query(connection -> {
            PreparedStatement statement = connection.prepareStatement("select r.TEMPERATURE as temp, r.HUMIDITY as hum, m.MOMENT as moment, m.ID as mid, r.sensor as sensor from MEASUREMENTS m, READINGS r where r.measurement = m.id and timestamp(m.moment) >= ?");
            statement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis() - (3600000L * hours))); // Die letzten 'hours' Stunden
            return statement;
        }, (row, rowNum) -> {
            // TODO: Es darf nicht für jede Zeile ein Measurement erstellt werden, da für jedes Reasing eine Zeile geliefert wird!
            Measurement measurement = new Measurement(row.getInt("mid"), row.getTimestamp("moment"));
            measurement.getReadings().add(new Reading(row.getDouble("hum"), row.getDouble("temp"), getSensor(this.sensors, row.getInt("sensor")), measurement));
            return measurement;
        });
    }

//    public List<Measurement> getMeasurements(int hours, Sensor sensor) {
//        try {
//            PreparedStatement statement = this.connection.prepareStatement("select r.TEMPERATURE as temp, r.HUMIDITY as hum, m.MOMENT as moment, m.ID as mid, r.sensor as sensor from MEASUREMENTS m, READINGS r where r.measurement = m.id and r.sensor = ? and timestamp(m.moment) >= ?");
//            statement.setInt(1, sensor.getId()); // Sensor-ID
//            statement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis() - (3600000L * hours))); // Die letzten "hours" Stunden
//            return transferToMeasurements(statement.executeQuery());
//        } catch(SQLException e) {
//            LOG.error("Error reading measurements", e);
//            return new ArrayList<Measurement>();
//        }
//    }

    private Measurement getMeasurement(List<Measurement> measurements, int mid) {
        for(Measurement measurement : measurements) {
            if(measurement.getId() == mid) {
                return measurement;
            }
        }
        return null;
    }

    private Sensor getSensor(List<Sensor> sensors, int sid) {
        for(Sensor sensor : sensors) {
            if(sensor.getId() == sid) {
                return sensor;
            }
        }
        return null;
    }

//    public Map<Sensor, List<BoundaryReading>> getBoundaryReading(int days) {
//        try {
//            PreparedStatement statement = this.connection.prepareStatement("select max(r.TEMPERATURE) as max_temp, min(r.TEMPERATURE) as min_temp, max(r.HUMIDITY) as max_hum, min(r.HUMIDITY) as min_hum, date(m.MOMENT) as day, r.sensor as sensor from MEASUREMENTS m, READINGS r where r.measurement = m.id and date(m.moment) >= ? group by date(m.MOMENT), r.sensor");
//            statement.setDate(1, new java.sql.Date(System.currentTimeMillis() - (86400000L * days))); // Die letzten days Tage
//            return transferToBoundaryReading(statement.executeQuery());
//        } catch (SQLException e) {
//            LOG.error("Error reading measurements", e);
//            return new HashMap<Sensor, List<BoundaryReading>>();
//        }
//    }
//
//    public List<BoundaryReading> getBoundaryReading(int days, Sensor sensor) {
//        try {
//            PreparedStatement statement = this.connection.prepareStatement("select max(r.TEMPERATURE) as max_temp, min(r.TEMPERATURE) as min_temp, max(r.HUMIDITY) as max_hum, min(r.HUMIDITY) as min_hum, date(m.MOMENT) as day, r.sensor as sensor from MEASUREMENTS m, READINGS r where r.measurement = m.id and r.sensor = ? and date(m.moment) >= ? group by date(m.MOMENT), r.sensor");
//            statement.setInt(1, sensor.getId()); // Sensor-ID
//            statement.setDate(2, new java.sql.Date(System.currentTimeMillis() - (86400000L * days))); // Die letzten days Tage
//            return transferToBoundaryReading(statement.executeQuery()).get(sensor);
//        } catch (SQLException e) {
//            LOG.error("Error reading measurements", e);
//            return new ArrayList<BoundaryReading>();
//        }
//    }

    private Map<Sensor, List<BoundaryReading>> transferToBoundaryReading(ResultSet result) throws SQLException {
        Map<Sensor, List<BoundaryReading>> boundaryReadings = new HashMap<Sensor, List<BoundaryReading>>();
        while(result.next()) {
            final Sensor sensor = getSensor(this.sensors, result.getInt("sensor"));
            List<BoundaryReading> list = boundaryReadings.get(sensor);
            if(list == null) {
                list = new ArrayList<BoundaryReading>();
                boundaryReadings.put(sensor, list);
            }
            list.add(new BoundaryReading(result.getDouble("min_hum"), result.getDouble("max_hum"), result.getDouble("min_temp"), result.getDouble("max_temp"), sensor, result.getDate("day")));
        }
        return boundaryReadings;
    }
}
