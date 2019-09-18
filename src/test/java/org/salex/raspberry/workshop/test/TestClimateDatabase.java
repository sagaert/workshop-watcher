package org.salex.raspberry.workshop.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salex.raspberry.workshop.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestClimateDatabase {
	@Autowired
	ClimateDatabase database;

	@Test
	public void testReadSensors() {
		int sensorCount = this.database.getSensors().size();
		Assert.assertEquals(4,sensorCount);
	}

	@Test
	public void testWriteMeasurement() {
		Random rd = new Random();
		Measurement m = new Measurement();
		for(Sensor s : this.database.getSensors()) {
			Reading r = new Reading(rd.nextDouble() * 100, rd.nextDouble() * 30, s, m);
			m.getReadings().add(r);
		}
		Assert.assertEquals(-1, m.getId());
		Measurement result = this.database.addMeasurement(m);
		Assert.assertNotEquals(-1, result.getId());
	}
//	@Test
//	public void testReadMeasurements() {
//		Collection<Measurement> m = this.database.getMeasurements(24);
//		System.out.println("ID\t| Timestamp\t| Readings");
//		System.out.println("------------------------------------------------------------------");
//		for(Measurement em : m) {
//			System.out.print(em.getId());
//			System.out.print("\t| ");
//			System.out.print(em.getTimestamp());
//			System.out.print("\t|");
//			for(Reading er : em.getReadings()) {
//				System.out.print(" ");
//				System.out.print(er.getSensor().getId());
//				System.out.print(": ");
//				System.out.print(er.getTemperature());
//				System.out.print("°C, ");
//				System.out.print(er.getHumidity());
//				System.out.print("%");
//			}
//			System.out.println();
//		}
//	}
//
//	@Test
//	public void testReadHistory() {
//		final Sensor s = this.database.getSensors().get(3);
//		Collection<BoundaryReading> br = this.database.getBoundaryReading(30).get(s);
//		System.out.println("Day\t\t| Sensor\t| Temp (min)\t| Temp (max)\t| Hum (min)\t| Hum (max)\t| ");
//		System.out.println("------------------------------------------------------------------");
//		for(BoundaryReading ebr : br) {
//			System.out.print(ebr.getDay());
//			System.out.print("\t| ");
//			System.out.print(ebr.getSensor().getId());
//			System.out.print("\t| ");
//			System.out.print(ebr.getMinimalTemperature());
//			System.out.print("\t| ");
//			System.out.print(ebr.getMaximalTemperature());
//			System.out.print("\t| ");
//			System.out.print(ebr.getMinimalHumidity());
//			System.out.print("\t| ");
//			System.out.print(ebr.getMaximalHumidity());
//			System.out.println();
//		}
//	}
}
