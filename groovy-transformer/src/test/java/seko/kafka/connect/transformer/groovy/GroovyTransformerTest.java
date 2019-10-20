package seko.kafka.connect.transformer.groovy;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seko.kafka.connect.transformer.script.configs.Configuration;

import java.util.HashMap;
import java.util.Map;

public class GroovyTransformerTest {
    private GroovyTransformer<SourceRecord> dateRouter = new GroovyTransformer<>();
    private Map<String, Object> config;

    @Before
    public void setUp() {
        config = new HashMap<>();
        config.put(Configuration.KEY_SCRIPT_CONFIG, "return source+123");
        config.put(Configuration.VALUE_SCRIPT_CONFIG, "source.put('qweqweq', 12312312); return source;");
    }

    @Test
    public void applyWithoutSchemaString() {
        dateRouter.configure(config);
        Map<String, Object> event = new HashMap<>();
        event.put("created_when", "2019-05-31T00:17:00.188Z");

        SourceRecord record = new SourceRecord(null, null, "topic", 0, null, "key___", null, event);
        SourceRecord transformed = dateRouter.apply(record);
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        Assert.assertEquals(12312312, stringObjectMap.get("qweqweq"));
        Assert.assertEquals(2, stringObjectMap.size());
        Assert.assertEquals("key___123", transformed.key());
    }

    /*@Test(expected = ConfigException.class)
    public void testSFConfigEmpty() {
        config.put(Configuration.KEY_SCRIPT_CONFIG, "");
        dateRouter.configure(config);
    }*/
}
