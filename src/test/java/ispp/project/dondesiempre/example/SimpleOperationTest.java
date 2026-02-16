package ispp.project.dondesiempre.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import example.SimpleOperation;

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