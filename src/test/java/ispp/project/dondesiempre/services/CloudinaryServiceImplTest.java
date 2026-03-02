package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import ispp.project.dondesiempre.config.CloudinaryProperties;
import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class CloudinaryServiceImplTest {

  @Mock private Cloudinary cloudinary;
  @Mock private Uploader uploader;
  @Mock private CloudinaryProperties properties;
  @Mock private MultipartFile mockFile;

  @InjectMocks private CloudinaryServiceImpl cloudinaryService;

  @Test
  void upload_returnsSecureUrl_whenUploadSucceeds() throws Exception {
    when(cloudinary.uploader()).thenReturn(uploader);
    when(mockFile.getBytes()).thenReturn(new byte[] {1, 2, 3});
    when(properties.getFolderPrefix()).thenReturn("dev");
    when(uploader.upload(any(byte[].class), any(Map.class)))
        .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/test/img.jpg"));

    String result = cloudinaryService.upload(mockFile);

    assertEquals("https://res.cloudinary.com/test/img.jpg", result);
  }

  @Test
  void upload_throwsInvalidRequestException_whenIOExceptionOccurs() throws Exception {
    when(cloudinary.uploader()).thenReturn(uploader);
    when(mockFile.getBytes()).thenThrow(new IOException("disk error"));

    assertThrows(InvalidRequestException.class, () -> cloudinaryService.upload(mockFile));
  }
}
