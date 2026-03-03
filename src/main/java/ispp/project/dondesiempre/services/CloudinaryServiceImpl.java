package ispp.project.dondesiempre.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import ispp.project.dondesiempre.config.CloudinaryProperties;
import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import java.io.IOException;
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
  public String upload(MultipartFile file) {
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
}
