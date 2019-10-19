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
        config.put(Configuration.KEY_SCRIPT_CONFIG, "source.put('qweqweq', 12312312); return source;");
        config.put(Configuration.VALUE_SCRIPT_CONFIG, "source.put('qweqweq', 12312312); return source;");
    }

    @Test
    public void applyWithoutSchemaString() {
        dateRouter.configure(config);
        Map<String, Object> event = new HashMap<>();
        event.put("created_when", "2019-05-31T00:17:00.188Z");

        SourceRecord transformed = dateRouter.apply(new SourceRecord(null, null, "topic", 0, null, event));
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        Assert.assertEquals(12312312, stringObjectMap.get("qweqweq"));
        Assert.assertEquals(2, stringObjectMap.size());
    }

    /*@Test(expected = ConfigException.class)
    public void testSFConfigEmpty() {
        config.put(Configuration.KEY_SCRIPT_CONFIG, "");
        dateRouter.configure(config);
    }*/
}
