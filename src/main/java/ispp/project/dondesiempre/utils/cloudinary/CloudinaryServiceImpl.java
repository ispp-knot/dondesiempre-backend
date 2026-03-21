package ispp.project.dondesiempre.utils.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import ispp.project.dondesiempre.config.CloudinaryProperties;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

  private final Cloudinary cloudinary;
  private final CloudinaryProperties properties;

  @Override
  public String upload(MultipartFile file) throws InvalidRequestException {
    try {
      Map<?, ?> result =
          cloudinary
              .uploader()
              .upload(file.getBytes(), ObjectUtils.asMap("folder", properties.getFolderPrefix()));
      return (String) result.get("secure_url");
    } catch (IOException e) {
      throw new InvalidRequestException("Failed to upload image: " + e.getMessage());
    }
  }

  @Override
  public String uploadSeedResource(String classpathPath, String publicId) {
    String fullPublicId = properties.getFolderPrefix() + "/seed/" + publicId;
    try {
      Map<?, ?> existing = cloudinary.api().resource(fullPublicId, ObjectUtils.emptyMap());
      return (String) existing.get("secure_url");
    } catch (Exception notFound) {
      try (InputStream is = getClass().getClassLoader().getResourceAsStream(classpathPath)) {
        if (is == null) throw new InvalidRequestException("Seed image not found: " + classpathPath);
        Map<?, ?> result =
            cloudinary
                .uploader()
                .upload(
                    is.readAllBytes(),
                    ObjectUtils.asMap("public_id", fullPublicId, "overwrite", false));
        return (String) result.get("secure_url");
      } catch (IOException e) {
        throw new InvalidRequestException("Failed to upload seed image: " + classpathPath);
      }
    }
  }
}
