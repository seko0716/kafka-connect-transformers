package seko.kafka.connect.transformer.js;

import org.apache.kafka.connect.connector.ConnectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seko.kafka.connect.transformer.script.AbstractScriptTransformer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JavaScriptTransformer<R extends ConnectRecord<R>> extends AbstractScriptTransformer<R> {
    private static final Logger log = LoggerFactory.getLogger(JavaScriptTransformer.class);

    @Override
    public Map<String, Object> transform(Map<String, Object> source, String script) {
        ScriptEngineManager factory = new ScriptEngineManager();
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
}
