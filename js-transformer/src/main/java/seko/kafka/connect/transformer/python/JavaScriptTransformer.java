package seko.kafka.connect.transformer.python;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.kafka.common.config.ConfigDef.Importance.MEDIUM;
import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;
import static seko.kafka.connect.transformer.script.configs.Configuration.KEY_SCRIPT_CONFIG;
import static seko.kafka.connect.transformer.script.configs.Configuration.VALUE_SCRIPT_CONFIG;

public class JavaScriptTransformer<R extends ConnectRecord<R>> implements Transformation<R> {
    private static final String PURPOSE = "field extraction";
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(KEY_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Field name to extract.")
            .define(VALUE_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Format extracted field.");


    private static final Logger log = LoggerFactory.getLogger(JavaScriptTransformer.class);

    private String valueScript;
    private String keyScript;

    @Override
    public R apply(R record) {
        Map<String, Object> key = Requirements.requireMapOrNull(record.key(), PURPOSE);

        if (keyScript != null && key != null) {
            key = groovyTransform(key, keyScript);
        }

        Map<String, Object> value = Requirements.requireMapOrNull(record.value(), PURPOSE);
        if (valueScript != null && value != null) {
            value = groovyTransform(value, valueScript);
        }

        return newRecord(record, key, value);
    }

    private Map<String, Object> groovyTransform(Map<String, Object> source, String script) {
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        engine.put("source", source);

        try {
            return (Map<String, Object>) engine.eval(script);
        } catch (Exception e) {
            List<String> tags = Optional.ofNullable(source.get("tags"))
                    .map(it -> (List<String>) it)
                    .orElse(new ArrayList<>());
            tags.add("groovy_transformer: " + e.getMessage());
            source.put("tags", tags);
            log.warn("Fallout groovy script evaluation: ", e);
            return source;
        }
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }


    private R newRecord(R record, Map<String, Object> newKey, Map<String, Object> newValue) {
        Object key = newKey == null ? record.key() : newKey;
        Object value = newValue == null ? record.value() : newValue;

        return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(),
                key, record.valueSchema(), value, record.timestamp());
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {
        SimpleConfig config = new SimpleConfig(CONFIG_DEF, configs);
        String keyScript = config.getString(KEY_SCRIPT_CONFIG);
        if (keyScript != null && !keyScript.trim().isEmpty()) {
            this.keyScript = keyScript;
        }
        String valueScript = config.getString(VALUE_SCRIPT_CONFIG);
        if (valueScript != null && !valueScript.trim().isEmpty()) {
            this.valueScript = valueScript;
        }
    }
}
