package seko.kafka.connect.transformer.python;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seko.kafka.connect.transformer.script.configs.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ScriptEngineTransformerTest {
    private ScriptEngineTransformer<SourceRecord> dateRouter = new ScriptEngineTransformer<>();
    private Map<String, Object> config;

    @Before
    public void setUp() {
        config = new HashMap<>();
        config.put(Configuration.KEY_SCRIPT_CONFIG, "def keyTransform(source): source['qweqweq'] = 12312312; return source");
        config.put(Configuration.VALUE_SCRIPT_CONFIG, "def valueTransform(source): source['qweqweq'] = 12312312; return source");
    }

    @Test
    public void applyWithoutSchemaStringGroovy() {
        config.put(Configuration.SCRIP_ENGINE_NAME, "groovy");
        config.put(Configuration.KEY_SCRIPT_CONFIG, "def keyTransform(def source) {source.put('qweqweq', 12312312); return source; }");
        config.put(Configuration.VALUE_SCRIPT_CONFIG, "def valueTransform(def source) {source.put('qweqweq', 12312312); return source; }");
        dateRouter.configure(config);
        Map<String, Object> event = new HashMap<>();
        event.put("created_when", "2019-05-31T00:17:00.188Z");

        SourceRecord transformed = dateRouter.apply(new SourceRecord(null, null, "topic", 0, null, event));
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        Assert.assertEquals(12312312, stringObjectMap.get("qweqweq"));
        Assert.assertEquals(2, stringObjectMap.size());
    }


    @Test
    public void applyWithoutSchemaPython() {
        config.put(Configuration.SCRIP_ENGINE_NAME, "python");
        config.put(Configuration.KEY_SCRIPT_CONFIG, "def keyTransform(source): source['qweqweq'] = 12312312; return source");
        config.put(Configuration.VALUE_SCRIPT_CONFIG, "def valueTransform(source): source['qweqweq'] = 12312312; return source");
        dateRouter.configure(config);
        Map<String, Object> event = new HashMap<>();
        event.put("created_when", "2019-05-31T00:17:00.188Z");

        SourceRecord transformed = dateRouter.apply(new SourceRecord(null, null, "topic", 0, null, event));
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        Assert.assertEquals(12312312, stringObjectMap.get("qweqweq"));
        Assert.assertEquals(2, stringObjectMap.size());
    }

    @Test
    public void applyWithoutSchemaJs() {
        config.put(Configuration.SCRIP_ENGINE_NAME, "JavaScript");
        config.put(Configuration.KEY_SCRIPT_CONFIG, "function keyTransform(source){ source.qweqweq = 12312312; return source;}");
        config.put(Configuration.VALUE_SCRIPT_CONFIG, "function valueTransform(source){ source.qweqweq = 12312312; return source;}");
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
