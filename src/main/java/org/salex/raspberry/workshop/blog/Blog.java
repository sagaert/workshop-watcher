package org.salex.raspberry.workshop.blog;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

// TODO: Umscheiben auf Spring !!!

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Blog {
	public final static String OVERVIEW_ID = "146";
	public final static String OVERVIEW_TYPE = "content_block";
	public final static String DETAILS_ID = "148";
	public final static String DETAILS_TYPE = "pages";
	public final static String HISTORY_ID = "60309";
	public final static String HISTORY_TYPE = "pages";
	public final static String REFERENCED_IMAGES_META_FIELD = "referenced_images";
	public final static String REFERENCED_IMAGES_SEPARATOR = ";";

	private final String auth;
	private final WebTarget target;

	public Blog(final String url, final String username, final String password) {
		this.auth = "Basic "
				+ Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
		this.target = ClientBuilder.newClient().target(url);
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
		this.target.path(type).path(id).request(MediaType.APPLICATION_JSON).header("authorization", this.auth).post(Entity.json(post));

		// Delete old references images
		if(oldReferencedImages != null) {
			for (String imageId : oldReferencedImages.split(REFERENCED_IMAGES_SEPARATOR)) {
				deleteImage(imageId);
			}
		}
	}

	public void deleteImage(String id) {
		this.target.path("media").path(id).queryParam("force", true).request().header("authorization", this.auth)
				.delete();
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
		return target.path(type).path(id).request(MediaType.APPLICATION_JSON).header("authorization", this.auth).get(Post.class);
	}
	
	public Image addPNGImage(String prefix, byte[] data) {
		final String filename = prefix + UUID.randomUUID() + ".png";
		final Response uploadResult = this.target.path("media").request(MediaType.APPLICATION_JSON)
				.header("authorization", this.auth).header("content-type", "image/png")
				.header("content-disposition", "attachement; filename=" + filename)
				.post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));
		if (uploadResult.getStatus() == 201) {
			final String location = uploadResult.getHeaderString("Location");
			if (location != null) {
				final String[] elements = location.split("/");
				final Image image = new Image(elements[elements.length - 1]);
				final Media media = this.target.path("media").path(image.getId()).request(MediaType.APPLICATION_JSON).get(Media.class);
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
			throw new RuntimeException("Fehler beim hochladen eines Bildes: " + uploadResult.getStatusInfo());
		}
	}
}
