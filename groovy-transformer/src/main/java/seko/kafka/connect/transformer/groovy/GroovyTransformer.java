package seko.kafka.connect.transformer.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.transform.CompileStatic;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seko.kafka.connect.transformer.script.AbstractScriptTransformer;

import java.util.*;

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
            tags.add("groovy_transformer: " + e.getMessage());
            source.put("tags", tags);
            log.warn("Fallout groovy script evaluation: ", e);
            return source;
        }
    }


    @Override
    public void configure(Map<String, ?> configs) {
        super.configure(configs);
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
