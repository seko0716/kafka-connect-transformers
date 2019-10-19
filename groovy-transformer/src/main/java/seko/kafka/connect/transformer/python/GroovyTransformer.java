package seko.kafka.connect.transformer.python;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.transform.CompileStatic;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apache.kafka.common.config.ConfigDef.Importance.MEDIUM;
import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;
import static seko.kafka.connect.transformer.python.configs.Configuration.KEY_SCRIPT_CONFIG;
import static seko.kafka.connect.transformer.python.configs.Configuration.VALUE_SCRIPT_CONFIG;

public class GroovyTransformer<R extends ConnectRecord<R>> implements Transformation<R> {
    private static final String PURPOSE = "field extraction";
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(KEY_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Field name to extract.")
            .define(VALUE_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Format extracted field.");


    private static final Logger log = LoggerFactory.getLogger(GroovyTransformer.class);

    private Script valueScript;
    private Script keyScript;

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

    private Map<String, Object> groovyTransform(Map<String, Object> source, Script script) {
        script.setProperty("source", source);
        try {
            return (Map<String, Object>) script.run();
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
            this.keyScript = getScript(keyScript);
        }
        String valueScript = config.getString(VALUE_SCRIPT_CONFIG);
        if (valueScript != null && !valueScript.trim().isEmpty()) {
            this.valueScript = getScript(valueScript);
        }
    }

    private Script getScript(String script) {
        CompilerConfiguration conf = new CompilerConfiguration();
        SecureASTCustomizer customizer = new SecureASTCustomizer();
        List<String> receiversBlackList = Arrays.asList(
                System.class.getName()

        );
        customizer.setReceiversBlackList(receiversBlackList);
        conf.addCompilationCustomizers(customizer);
        ASTTransformationCustomizer astcz = new ASTTransformationCustomizer(CompileStatic.class);
        conf.addCompilationCustomizers(astcz);
        conf.addCompilationCustomizers(new SandboxTransformer());

        GroovyShell shell = new GroovyShell(conf);

        return shell.parse(script);
    }

}
