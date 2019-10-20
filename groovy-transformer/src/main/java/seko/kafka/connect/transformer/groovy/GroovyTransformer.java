package seko.kafka.connect.transformer.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.transform.CompileStatic;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seko.kafka.connect.transformer.script.AbstractScriptTransformer;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static seko.kafka.connect.transformer.script.configs.Configuration.KEY_SCRIPT_CONFIG;
import static seko.kafka.connect.transformer.script.configs.Configuration.VALUE_SCRIPT_CONFIG;

public class GroovyTransformer<R extends ConnectRecord<R>> extends AbstractScriptTransformer<R> {
    private static final Logger log = LoggerFactory.getLogger(GroovyTransformer.class);

    private Script groovyValueScript;
    private Script groovyKeyScript;

    @Override
    public Map<String, Object> transform(Map<String, Object> source, String script) {
        if ("keyScript".equals(script)) {
            return groovyTransform(source, groovyKeyScript);
        }
        if ("valueScript".equals(script)) {
            return groovyTransform(source, groovyValueScript);
        }
        return source;
    }

    private Map<String, Object> groovyTransform(Map<String, Object> source, Script script) {
        script.setProperty("source", source);
        try {
            return (Map<String, Object>) script.run();
        } catch (Exception e) {
            List<String> tags = Optional.ofNullable(source.get("tags"))
                    .map(it -> (List<String>) it)
                    .orElse(new ArrayList<>());
            tags.add(getScripEngineName() + "_transformer: " + e.getMessage());
            source.put("tags", tags);
            log.warn("Fallout groovy script evaluation: ", e);
            return source;
        }
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
    public String getScripEngineName() {
        return "groovy-sandbox";
    }
}
