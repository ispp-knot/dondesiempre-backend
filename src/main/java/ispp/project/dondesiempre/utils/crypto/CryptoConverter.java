package ispp.project.dondesiempre.utils.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

  private static String AES_SECRET_KEY;

  @Value("${AES_SECRET_KEY:}")
  public void setAesSecretKey(String aesSecretKey) {
    if (aesSecretKey == null || aesSecretKey.isBlank()) {
      throw new IllegalStateException(
          "AES_SECRET_KEY no está configurada. Define AES_SECRET_KEY en application.yml o variables de entorno.");
    }
    if (!(aesSecretKey.length() == 16
        || aesSecretKey.length() == 24
        || aesSecretKey.length() == 32)) {
      throw new IllegalStateException(
          "AES_SECRET_KEY debe tener 16, 24 o 32 caracteres. Actualmente: "
              + aesSecretKey.length());
    }
    CryptoConverter.AES_SECRET_KEY = aesSecretKey;
  }

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null) return null;
    if (AES_SECRET_KEY == null) {
      throw new IllegalStateException("AES_SECRET_KEY no está inicializada.");
    }
    try {
      SecretKeySpec secretKey = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
    } catch (Exception e) {
      throw new RuntimeException("Error al cifrar para la BBDD", e);
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null) return null;
    if (AES_SECRET_KEY == null) {
      throw new IllegalStateException("AES_SECRET_KEY no está inicializada.");
    }
    try {
      SecretKeySpec secretKey = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
    } catch (Exception e) {
      return dbData;
    }
  }
}
