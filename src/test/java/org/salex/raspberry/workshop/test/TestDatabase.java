package org.salex.raspberry.workshop.test;

public class TestDatabase {
//	Database database;
//
//	@Before
//	public void setUp() {
//		this.database = new Database("jdbc:derby://localhost:1527/climate");
//	}
//
//	@After
//	public void tearDown() {
//		this.database.stop();
//	}
//
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
