package org.salex.raspberry.workshop.test;

public class TestBlog {
//	private Blog blog;
//	private Database database;
//	private ChartGenerator chartGenerator;
//	private BlogGenerator blogGenerator;
//
//	@Before
//	public void setUp() {
//		this.blog = new Blog("https://holzwerken.salex.org/wp-json/wp/v2", System.getProperty("blog.user","Sascha"), System.getProperty("blog.password"));
//		this.database = new Database("jdbc:derby://localhost:1527/climate");
//		this.chartGenerator = new ChartGenerator(this.database.getSensors());
//		this.blogGenerator = new BlogGenerator(this.database.getSensors());
//	}
//
//	@Test
//	public void testReadOverview() throws Exception {
//		final Post overview = blog.getPost(Blog.OVERVIEW_ID, Blog.OVERVIEW_TYPE);
//		Assert.assertNotNull(overview);
//		Assert.assertNotNull(overview);
//		Assert.assertNotNull(overview.getContent());
//		Assert.assertNotNull(overview.getContent());
//	}
//
//	@Test
//	public void testAddNewImage() throws Exception {
//		byte[] data = createImage("Test-Bild");
//		final Image image = blog.addPNGImage("verlauf-", data);
//		Assert.assertNotNull(image);
//		Assert.assertNotNull(image.getId());
//		Assert.assertNotNull(image.getFull());
//		Assert.assertNotNull(image.getThumbnail());
//		blog.deleteImage(image.getId());
//	}
//
//	@Test
//	public void testUpdatePost() throws Exception {
//		final String content = "<p>" + UUID.randomUUID() + "</p>";
//		byte[] data = createImage("Test-Bild");
//		final Image image = blog.addPNGImage("verlauf-", data);
//		final List<Image> images = new ArrayList<Image>();
//		images.add(image);
//		blog.updatePost("61290", "pages", content, images);
//		final Post result = blog.getPost("61290", "pages");
//		Assert.assertEquals(content, result.getContent().trim());
//		Assert.assertEquals(image.getId(), result.getMeta().getReferencedImages());
//		blog.deleteImage(image.getId());
//	}
//
//
//	@Test
//	@Ignore /* Dieser Test dient dem manuellen Updaten der History und sollte nur explizit ausgeführt werden ! */
//	public void testUpdateHistory() throws Exception {
//		final Map<Sensor, List<BoundaryReading>> data = this.database.getBoundaryReading(365);
//		final Map<Sensor, Map<String, Image>> diagrams = new HashMap<Sensor, Map<String, Image>>();
//		final List<Image> images = new ArrayList<Image>();
//		for(Sensor sensor : this.database.getSensors()) {
//			if(!diagrams.containsKey(sensor)) {
//				diagrams.put(sensor, new HashMap<String, Image>());
//			}
//			final Map<String, Image> diagramsForSensor = diagrams.get(sensor);
//			Image image = this.blog.addPNGImage("temperature-", this.chartGenerator.create365DayTemperatureChart(data.get(sensor), sensor));
//			diagramsForSensor.put("temperature", image);
//			images.add(image);
//			if(Sensor.Type.DHT22.equals(sensor.getType())) {
//				image = this.blog.addPNGImage("humidity-", this.chartGenerator.create365DayHumidityChart(data.get(sensor), sensor));
//				diagramsForSensor.put("humidity", image);
//				images.add(image);
//			}
//		}
//		this.blog.updateHistory(this.blogGenerator.generateHistory(data, diagrams), images);
//	}
//
//
//	@Test
//	@Ignore /* Dieser Test dient dem manuellen Updaten der Details und sollte nur explizit ausgeführt werden ! */
//	public void testUpdateDetails() throws Exception {
//		final List<Measurement> data = this.database.getMeasurements(24);
//		final Image diagram = this.blog.addPNGImage("verlauf-", this.chartGenerator.create24HourChart(data));
//		final List<Image> images = new ArrayList<Image>();
//		images.add(diagram);
//		this.blog.updateDetails(this.blogGenerator.generateDetails(data, diagram), images);
//
//	}
//
//	private byte[] createImage(String text) throws IOException {
//        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = img.createGraphics();
//        Font font = new Font("Arial", Font.PLAIN, 48);
//        g2d.setFont(font);
//        FontMetrics fm = g2d.getFontMetrics();
//        int width = fm.stringWidth(text);
//        int height = fm.getHeight();
//        g2d.dispose();
//        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        g2d = img.createGraphics();
//        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//        g2d.setFont(font);
//        fm = g2d.getFontMetrics();
//        g2d.setColor(Color.BLACK);
//        g2d.drawString(text, 0, fm.getAscent());
//        g2d.dispose();
//       	final ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(img, "png", baos);
//        return baos.toByteArray();
//	}
}