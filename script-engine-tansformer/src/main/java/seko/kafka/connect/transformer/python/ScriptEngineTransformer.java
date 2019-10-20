package seko.kafka.connect.transformer.python;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.kafka.common.config.ConfigDef.Importance.MEDIUM;
import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;
import static seko.kafka.connect.transformer.script.configs.Configuration.*;

public class ScriptEngineTransformer<R extends ConnectRecord<R>> implements Transformation<R> {
    private static final Logger log = LoggerFactory.getLogger(ScriptEngineTransformer.class);
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(KEY_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for key transformation")
            .define(VALUE_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for value transformation")
            .define(SCRIP_ENGINE_NAME, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for key transformation");

    private static final String PURPOSE = "field extraction";

    private String valueScript;
    private String keyScript;
    private Invocable inv;
    private String scriptEngineName;

    @Override
    public R apply(R record) {
        Map<String, Object> key = Requirements.requireMapOrNull(record.key(), PURPOSE);

        if (keyScript != null && key != null) {
            key = transform(key, keyScript);
        }

        Map<String, Object> value = Requirements.requireMapOrNull(record.value(), PURPOSE);
        if (valueScript != null && value != null) {
            value = transform(value, valueScript);
        }

        return newRecord(record, key, value);
    }

    public Map<String, Object> transform(Map<String, Object> source, String script) {
        if (this.keyScript != null) {
            return tryTransform(source, "keyTransform");
        }
        if (this.valueScript != null) {
            return tryTransform(source, "valueTransform");
        }
        return source;
    }

    public String getScripEngineName() {
        return scriptEngineName;
    }

    private Map<String, Object> tryTransform(Map<String, Object> source, String methodName) {
        try {
            return (Map<String, Object>) inv.invokeFunction(methodName, source);
        } catch (Exception e) {
            List<String> tags = Optional.ofNullable(source.get("tags"))
                    .map(it -> (List<String>) it)
                    .orElse(new ArrayList<>());
            tags.add(getScripEngineName() + "_transformer: " + e.getMessage());
            source.put("tags", tags);
            log.warn("Fallout {} script evaluation: ", getScripEngineName(), e);
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
        this.scriptEngineName = config.getString(SCRIP_ENGINE_NAME);
        String keyScript = config.getString(KEY_SCRIPT_CONFIG);
        if (keyScript != null && !keyScript.trim().isEmpty()) {
            this.keyScript = keyScript;
        }
        String valueScript = config.getString(VALUE_SCRIPT_CONFIG);
        if (valueScript != null && !valueScript.trim().isEmpty()) {
            this.valueScript = valueScript;
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (this.keyScript != null) {
            stringBuilder.append(keyScript);
        }
        stringBuilder.append("\n");

        if (this.valueScript != null) {
            stringBuilder.append(valueScript);
        }
        this.inv = (Invocable) getScript(stringBuilder.toString());
    }

    private ScriptEngine getScript(String script) {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(getScripEngineName());
        try {
            scriptEngine.eval(script);
            return scriptEngine;
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }
}
