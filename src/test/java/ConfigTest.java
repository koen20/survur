import com.koenhabets.survur.server.ConfigHandler;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigTest {
    @Before
    public void prepare() {

    }

    @Test
    public void testConcatenate() {
        boolean old = ConfigHandler.alarmEnabled;
        ConfigHandler.changeConfig("alarm", "false");
        assertEquals(false, ConfigHandler.alarmEnabled);
        ConfigHandler.changeConfig("alarm", "true");
        assertEquals(true, ConfigHandler.alarmEnabled);
        ConfigHandler.changeConfig("alarm", old + "");
    }
}
