package ispp.project.dondesiempre.modules.common.exceptions;

public class QRGenerationException extends RuntimeException {
  public QRGenerationException(String message) {
    super(message);
  }

  public QRGenerationException() {
    super("Hubo un error durante la generación del QR, inténtelo de nuevo.");
  }
}
