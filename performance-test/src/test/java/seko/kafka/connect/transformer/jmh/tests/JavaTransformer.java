package seko.kafka.connect.transformer.jmh.tests;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seko.kafka.connect.transformer.script.ScriptEngineTransformer;

import java.util.Map;

import static org.apache.kafka.common.config.ConfigDef.Importance.MEDIUM;
import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;
import static seko.kafka.connect.transformer.script.configs.Configuration.*;

public class JavaTransformer<R extends ConnectRecord<R>> implements Transformation<R> {
    private static final Logger log = LoggerFactory.getLogger(ScriptEngineTransformer.class);
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(KEY_SCRIPT_CONFIG, STRING, null, MEDIUM, "Script for key transformation")
            .define(VALUE_SCRIPT_CONFIG, STRING, null, MEDIUM, "Script for value transformation")
            .define(SCRIP_ENGINE_NAME, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for key transformation")
            .define(FIELD_FOR_EXCEPTION, STRING, FIELD_FOR_EXCEPTION_DEFAULT, MEDIUM, "Field for exception");

    @Override
    public R apply(R record) {
        Object key = record.key();
        key = key + "123";

        Object value = record.value();
        ((Map<String, Object>) value).put("qweqweq", 12312312);

        return newRecord(record, key, value);
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

    }
}
