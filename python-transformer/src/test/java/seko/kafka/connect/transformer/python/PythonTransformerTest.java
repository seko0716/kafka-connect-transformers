package seko.kafka.connect.transformer.python;

import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seko.kafka.connect.transformer.script.configs.Configuration;

import java.util.HashMap;
import java.util.Map;

public class PythonTransformerTest {
    private PythonTransformer<SourceRecord> dateRouter = new PythonTransformer<>();
    private Map<String, Object> config;

    @Before
    public void setUp() {
        config = new HashMap<>();
        config.put(Configuration.KEY_SCRIPT_CONFIG, "source['qweqweq'] = 12312312; source");
        config.put(Configuration.VALUE_SCRIPT_CONFIG, "source['qweqweq'] = 12312312; source");
    }

    @Test
    public void applyWithoutSchemaString() {
        dateRouter.configure(config);
        Map<String, Object> event = new HashMap<>();
        event.put("created_when", "2019-05-31T00:17:00.188Z");

        ConnectRecord transformed = dateRouter.apply(new SourceRecord(null, null, "topic", 0, null, event));
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        Assert.assertEquals(12312312, stringObjectMap.get("qweqweq"));
    }

    /*@Test(expected = ConfigException.class)
    public void testSFConfigEmpty() {
        config.put(Configuration.KEY_SCRIPT_CONFIG, "");
        dateRouter.configure(config);
    }*/
}
