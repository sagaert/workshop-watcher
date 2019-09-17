package org.salex.raspberry.workshop.blog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class Blog {
	private static final Logger LOG = LoggerFactory.getLogger(Blog.class);

	public final static String OVERVIEW_ID = "146";
	public final static String OVERVIEW_TYPE = "content_block";
	public final static String DETAILS_ID = "148";
	public final static String DETAILS_TYPE = "pages";
	public final static String HISTORY_ID = "60309";
	public final static String HISTORY_TYPE = "pages";
	public final static String REFERENCED_IMAGES_META_FIELD = "referenced_images";
	public final static String REFERENCED_IMAGES_SEPARATOR = ";";

	private final RestTemplate template;

	public Blog(
			@Value("${org.salex.blog.url}") String url,
			@Value("${org.salex.blog.username}") String username,
			@Value("${org.salex.blog.password}") String password,
			RestTemplateBuilder builder) {
		this.template = builder.rootUri(url).basicAuthentication(username, password).build();
	}

	public void updateOverview(String content) {
		updatePost(OVERVIEW_ID, OVERVIEW_TYPE, content, new ArrayList<Image>());
	}

	public void updateDetails(String content, List<Image> images) {
		updatePost(DETAILS_ID, DETAILS_TYPE, content, images);
	}

	public void updateHistory(String content, List<Image> images) {
		updatePost(HISTORY_ID, HISTORY_TYPE, content, images);
	}

	public void updatePost(String id, String type, String content, List<Image> images) {
		// Get old post and references images
		final Post post = getPost(id, type);
		String oldReferencedImages = null;
		if(post.getMeta() != null) {
			oldReferencedImages = post.getMeta().getReferencedImages();
		}

		// Update post
		post.setContent(content);
		if(!images.isEmpty()) {
			post.getMeta().setReferencedImages(getRefrencedImages(images));
		}
//		this.target.path(type).path(id).request(MediaType.APPLICATION_JSON).header("authorization", this.auth).post(Entity.json(post));
		this.template.postForLocation("/" + type + "/" + id, post);

		// Delete old references images
		if(oldReferencedImages != null) {
			for (String imageId : oldReferencedImages.split(REFERENCED_IMAGES_SEPARATOR)) {
				deleteImage(imageId);
			}
		}
	}

	public void deleteImage(String id) {
//		this.target.path("media").path(id).queryParam("force", true).request().header("authorization", this.auth)
//				.delete();
		Map<String, String> params = new HashMap<>();
		params.put("forcw", "true");
		this.template.delete("/media/" + id, params);
	}

	private String getRefrencedImages(List<Image> images) {
		final StringBuffer answer = new StringBuffer();
		boolean isFirst = true;
		for (Image each : images) {
			if (isFirst) {
				isFirst = false;
			} else {
				answer.append(REFERENCED_IMAGES_SEPARATOR);
			}
			answer.append(each.getId());
		}
		return answer.toString();
	}

	public Post getPost(String id, String type) {
//		return target.path(type).path(id).request(MediaType.APPLICATION_JSON).header("authorization", this.auth).get(Post.class);
		return this.template.getForEntity("/" + type + "/" + id, Post.class).getBody();
	}
	
	public Image addPNGImage(String prefix, byte[] data) {
		final String filename = prefix + UUID.randomUUID() + ".png";
//		final Response uploadResult = this.target.path("media").request(MediaType.APPLICATION_JSON)
//				.header("authorization", this.auth).header("content-type", "image/png")
//				.header("content-disposition", "attachement; filename=" + filename)
//				.post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));
		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", "image/png");
		headers.set("content-disposition", "attachement; filename=" + filename);
		HttpEntity<byte[]> entity = new HttpEntity(data, headers);
		ResponseEntity<String> uploadResult = this.template.postForEntity("/media", entity, String.class);
		if (uploadResult.getStatusCodeValue() == 201) {
			final String location = uploadResult.getHeaders().get("Location").get(0);
			if (location != null) {
				final String[] elements = location.split("/");
				final Image image = new Image(elements[elements.length - 1]);
//				final Media media = this.target.path("media").path(image.getId()).request(MediaType.APPLICATION_JSON).get(Media.class);
				final Media media = this.template.getForEntity("/media/" + image.getId(), Media.class).getBody();
				if (media.getDetails().getSizes().containsKey("full")) {
					image.setFull(media.getDetails().getSizes().get("full").getUrl());
				}
				if (media.getDetails().getSizes().containsKey("thumbnail")) {
					image.setThumbnail(media.getDetails().getSizes().get("thumbnail").getUrl());
					image.setThumbnailWidth(media.getDetails().getSizes().get("thumbnail").getWidth());
					image.setThumbnailHeight(media.getDetails().getSizes().get("thumbnail").getHeight());
				}
				return image;
			}
			return null;
		} else {
			throw new RuntimeException("Fehler beim hochladen eines Bildes: " + uploadResult.getStatusCode().getReasonPhrase());
		}
	}
}
