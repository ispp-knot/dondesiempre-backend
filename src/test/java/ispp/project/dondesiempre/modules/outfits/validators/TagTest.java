package ispp.project.dondesiempre.modules.outfits.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class TagTest {
  class TestDTO {

    @Tag String tag;

    TestDTO(String tag) {
      this.tag = tag;
    }
  }

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shoudReturnEmpty_whenInvalidTag() {
    TestDTO tag = new TestDTO("Summer");
    Set<ConstraintViolation<TestDTO>> violations = validator.validateProperty(tag, "tag");
    assertTrue(violations.isEmpty());
  }

  @Test
  void shoudReturnBlankViolation_whenBlankTag() {
    TestDTO tag = new TestDTO("    ");
    Set<ConstraintViolation<TestDTO>> violations = validator.validateProperty(tag, "tag");
    assertEquals(1, violations.size());
    Annotation annotation = violations.iterator().next().getConstraintDescriptor().getAnnotation();
    assertTrue(annotation instanceof NotBlank);
  }

  @Test
  void shoudReturnSizeViolation_whenSizeUpper255Tag() {
    TestDTO tag = new TestDTO("a".repeat(300));
    Set<ConstraintViolation<TestDTO>> violations = validator.validateProperty(tag, "tag");
    assertEquals(1, violations.size());
    Annotation annotation = violations.iterator().next().getConstraintDescriptor().getAnnotation();
    assertTrue(annotation instanceof Size);
  }

  @Test
  void shoudReturnBothViolation_whenViolateAllTagConstraints() {
    TestDTO tag = new TestDTO(" ".repeat(300));
    Set<ConstraintViolation<TestDTO>> violations = validator.validateProperty(tag, "tag");
    assertEquals(2, violations.size());
    Function<ConstraintViolation<TestDTO>, Annotation> func =
        constr -> constr.getConstraintDescriptor().getAnnotation();
    List<Annotation> annotations = violations.stream().map(func).toList();

    assertTrue(annotations.stream().anyMatch(annot -> annot instanceof NotBlank));
    assertTrue(annotations.stream().anyMatch(annot -> annot instanceof Size));
  }

  @Test
  void shouldThrowConstraintViolationException_whenUsingInvalidTagAsParam() {
    String tag = null;
    ExecutableValidator execValid = validator.forExecutables();
    try {
      Set<ConstraintViolation<TestClass>> violations =
          execValid.validateParameters(
              new TestClass(),
              TestClass.class.getDeclaredMethod("testMethod", String.class),
              new Object[] {tag});
      assertFalse(violations.isEmpty());
    } catch (Exception e) {
      fail("Error inintencionado");
    }
  }

  public class TestClass {

    public void testMethod(@Tag String tag) {
      System.err.println("a");
    }
  }
}
