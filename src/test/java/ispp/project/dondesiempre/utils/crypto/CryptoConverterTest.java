package ispp.project.dondesiempre.utils.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CryptoConverterTest {

  @InjectMocks private CryptoConverter cryptoConverter;

  @BeforeEach
  void setUp() {
    String randomTestKey = UUID.randomUUID().toString().replace("-", "");
    cryptoConverter.setAesSecretKey(randomTestKey);
  }

  @Test
  void shouldEncryptStringSuccessfully() {
    String originalCode = "ABCD-1234-EFGH";
    String encryptedCode = cryptoConverter.convertToDatabaseColumn(originalCode);

    assertNotNull(encryptedCode);
    assertNotEquals(originalCode, encryptedCode);
    assertTrue(encryptedCode.length() > originalCode.length());
  }

  @Test
  void shouldReturnNullWhenEncryptingNull() {
    String encryptedCode = cryptoConverter.convertToDatabaseColumn(null);
    assertNull(encryptedCode);
  }

  @Test
  void shouldThrowExceptionWhenEncryptingWithInvalidKey() {
    cryptoConverter.setAesSecretKey("clave-rota");

    String originalCode = "ABCD-1234-EFGH";

    assertThrows(
        RuntimeException.class,
        () -> {
          cryptoConverter.convertToDatabaseColumn(originalCode);
        });
  }

  @Test
  void shouldDecryptStringSuccessfully() {
    String originalCode = "ABCD-1234-EFGH";
    String encryptedCode = cryptoConverter.convertToDatabaseColumn(originalCode);
    String decryptedCode = cryptoConverter.convertToEntityAttribute(encryptedCode);

    assertNotNull(decryptedCode);
    assertEquals(originalCode, decryptedCode);
  }

  @Test
  void shouldReturnNullWhenDecryptingNull() {
    String decryptedCode = cryptoConverter.convertToEntityAttribute(null);
    assertNull(decryptedCode);
  }

  @Test
  void shouldReturnOriginalStringWhenDecryptingFails() {
    String notEncryptedData = "plain-text-code";
    String decryptedCode = cryptoConverter.convertToEntityAttribute(notEncryptedData);
    assertEquals(notEncryptedData, decryptedCode);
  }
}
