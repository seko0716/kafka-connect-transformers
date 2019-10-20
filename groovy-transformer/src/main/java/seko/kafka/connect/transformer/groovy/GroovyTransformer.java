package seko.kafka.connect.transformer.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.transform.CompileStatic;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static org.apache.kafka.common.config.ConfigDef.Importance.MEDIUM;
import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;
import static seko.kafka.connect.transformer.script.configs.Configuration.*;

public class GroovyTransformer<R extends ConnectRecord<R>> implements Transformation<R> {
    private static final Logger log = LoggerFactory.getLogger(GroovyTransformer.class);

    protected static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(KEY_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for key transformation")
            .define(VALUE_SCRIPT_CONFIG, STRING, NO_DEFAULT_VALUE, MEDIUM, "Script for value transformation")
            .define(FIELD_FOR_EXCEPTION, STRING, FIELD_FOR_EXCEPTION_DEFAULT, MEDIUM, "Field for exception");

    protected String valueScript;
    protected String keyScript;
    private String fieldForException;

    private Script groovyValueScript;
    private Script groovyKeyScript;

    @Override
    public R apply(R record) {
        Object key = record.key();
        if (keyScript != null && key != null) {
            key = transform(key, keyScript);
        }

        Object value = record.value();
        if (valueScript != null && value != null) {
            value = transform(value, valueScript);
        }

        return newRecord(record, key, value);
    }


    private R newRecord(R record, Object newKey, Object newValue) {
        Object key = newKey == null ? record.key() : newKey;
        Object value = newValue == null ? record.value() : newValue;

        return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(),
                key, record.valueSchema(), value, record.timestamp());
    }

    public Object transform(Object source, String script) {
        if ("keyScript".equals(script)) {
            return groovyTransform(source, groovyKeyScript);
        }
        if ("valueScript".equals(script)) {
            return groovyTransform(source, groovyValueScript);
        }
        return source;
    }

    private Object groovyTransform(Object source, Script script) {
        script.setProperty("source", source);
        try {
            return script.run();
        } catch (Exception e) {
            if (fieldForException != null && !fieldForException.isEmpty() && source instanceof Map) {
                Map sourceMap = (Map) source;
                List<String> tags = Optional.ofNullable(sourceMap.get(fieldForException))
                        .map(it -> (List<String>) it)
                        .orElse(new ArrayList<>());
                tags.add("groovy_sandbox_transformer: " + e.getMessage());
                sourceMap.put("tags", tags);
            }
            log.warn("Fallout script evaluation: groovy sandbox", e);
            return source;
        }
    }


    @Override
    public void configure(Map<String, ?> configs) {
        SimpleConfig config = new SimpleConfig(CONFIG_DEF, configs);
        String keyScript = config.getString(KEY_SCRIPT_CONFIG);
        this.fieldForException = config.getString(FIELD_FOR_EXCEPTION);
        if (keyScript != null && !keyScript.trim().isEmpty()) {
            this.keyScript = keyScript;
        }
        String valueScript = config.getString(VALUE_SCRIPT_CONFIG);
        if (valueScript != null && !valueScript.trim().isEmpty()) {
            this.valueScript = valueScript;
        }

        if (this.keyScript != null) {
            this.groovyKeyScript = getScript(keyScript);
            this.keyScript = "keyScript";
        }

        if (this.valueScript != null) {
            this.groovyValueScript = getScript(valueScript);
            this.valueScript = "valueScript";
        }
    }

    private Script getScript(String script) {
        CompilerConfiguration conf = new CompilerConfiguration();
        SecureASTCustomizer customizer = new SecureASTCustomizer();
        List<String> receiversBlackList = Arrays.asList(
                System.class.getName(),
                File.class.getName(),
                Path.class.getName(),
                InputStream.class.getName()
        );
        customizer.setReceiversBlackList(receiversBlackList);
        conf.addCompilationCustomizers(customizer);
        ASTTransformationCustomizer astcz = new ASTTransformationCustomizer(CompileStatic.class);
        conf.addCompilationCustomizers(astcz);
        conf.addCompilationCustomizers(new SandboxTransformer());

        GroovyShell shell = new GroovyShell(conf);

        return shell.parse(script);
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() {

    }
}
