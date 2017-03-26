import com.koenhabets.survur.server.TemperatureHandler;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemperatureTest {

    @Before
    public void prepare() {

    }

    @Test
    public void testConcatenate() {
        double result = TemperatureHandler.round(5.568, 2);
        assertEquals(5.57, result, 0.01);
    }
}
