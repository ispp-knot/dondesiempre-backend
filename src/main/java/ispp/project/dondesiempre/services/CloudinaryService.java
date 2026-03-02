package ispp.project.dondesiempre.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

  /**
   * Uploads a file to Cloudinary under the configured folder prefix and returns the secure URL of
   * the uploaded image.
   */
  String upload(MultipartFile file);
}
