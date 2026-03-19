package ispp.project.dondesiempre.utils.cloudinary;

import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

  /**
   * Uploads a file to Cloudinary under the configured folder prefix and returns the secure URL of
   * the uploaded image.
   */
  String upload(MultipartFile file) throws InvalidRequestException;

  /**
   * Uploads a classpath resource to Cloudinary using a fixed publicId, returning the secure URL. If
   * the asset already exists under that publicId, returns the existing URL without re-uploading.
   */
  String uploadSeedResource(String classpathPath, String publicId);
}
