package seko.kafka.connect.transformer.script;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.util.Map;

import static org.apache.kafka.common.config.ConfigDef.Importance.MEDIUM;
import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;
import static seko.kafka.connect.transformer.script.configs.Configuration.KEY_SCRIPT_CONFIG;
import static seko.kafka.connect.transformer.script.configs.Configuration.VALUE_SCRIPT_CONFIG;

public abstract class AbstractScriptTransformer<R extends ConnectRecord<R>> implements Transformation<R>, Transform {
    private static final String PURPOSE = "field extraction";
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(KEY_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for key transformation")
            .define(VALUE_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for value transformation");

    private String valueScript;
    private String keyScript;

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
