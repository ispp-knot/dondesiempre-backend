import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

class SimpleOperationTest {
    
    @Test
    void testAddSuccess() {
        SimpleOperation operation = new SimpleOperation();
        int result = operation.add(2, 3);
        assertEquals(5, result);
    }

    @Test
    void testAddFailure() {
        SimpleOperation operation = new SimpleOperation();
        int result = operation.add(2, 3);
        assertNotEquals(4, result);
    }
}