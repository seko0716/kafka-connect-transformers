package seko.kafka.connect.transformer.python;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

import static org.apache.kafka.common.config.ConfigDef.Importance.MEDIUM;
import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;
import static seko.kafka.connect.transformer.script.configs.Configuration.*;

public class ScriptEngineTransformer<R extends ConnectRecord<R>> implements Transformation<R> {
    private static final Logger log = LoggerFactory.getLogger(ScriptEngineTransformer.class);
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(KEY_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for key transformation")
            .define(VALUE_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for value transformation")
            .define(SCRIP_ENGINE_NAME, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for key transformation")
            .define(FIELD_FOR_EXCEPTION, STRING, FIELD_FOR_EXCEPTION_DEFAULT, MEDIUM, "Field for exception");

    private String fieldForException;
    private String valueScript;
    private String keyScript;
    private Invocable inv;
    private String scriptEngineName;

    @Override
    public R apply(R record) {
        Object key = record.key();
        if (keyScript != null && key != null) {
            key = tryTransform(key, "keyTransform");
        }

        Object value = record.value();
        if (valueScript != null && value != null) {
            value = tryTransform(value, "valueTransform");
        }

        return newRecord(record, key, value);
    }

    public String getScripEngineName() {
        return scriptEngineName;
    }

    private Object tryTransform(Object source, String methodName) {
        try {
            return inv.invokeFunction(methodName, source);
        } catch (Exception e) {
            if (fieldForException != null && !fieldForException.isEmpty() && source instanceof Map) {
                Map sourceMap = (Map) source;
                List<String> tags = Optional.ofNullable(sourceMap.get(fieldForException))
                        .map(it -> (List<String>) it)
                        .orElse(new ArrayList<>());
                tags.add(getScripEngineName() + "_transformer: " + e.getMessage());
                sourceMap.put("tags", tags);
            }
            log.warn("Fallout {} script evaluation: ", getScripEngineName(), e);
            return source;
        }
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    private R newRecord(R record, Object newKey, Object newValue) {
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
        this.fieldForException = config.getString(FIELD_FOR_EXCEPTION);
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
        ScriptEngine scriptEngine;
        if (Arrays.asList("nashorn", "javascript").contains(getScripEngineName().toLowerCase())) {
            scriptEngine = new NashornScriptEngineFactory().getScriptEngine("-strict", "--no-java", "--no-syntax-extensions");
        } else {
            scriptEngine = new ScriptEngineManager().getEngineByName(getScripEngineName());
        }

        try {
            scriptEngine.eval(script);
            return scriptEngine;
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }
}
