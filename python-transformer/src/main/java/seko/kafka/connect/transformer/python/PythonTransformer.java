package seko.kafka.connect.transformer.python;

import org.apache.kafka.connect.connector.ConnectRecord;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seko.kafka.connect.transformer.script.AbstractScriptTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PythonTransformer<R extends ConnectRecord<R>> extends AbstractScriptTransformer {
    private static final Logger log = LoggerFactory.getLogger(PythonTransformer.class);


    public Map<String, Object> transform(Map<String, Object> source, String script) {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.set("source", source);

        try {
            PyCode compile = interpreter.compile(script);
            PyObject pyObject = interpreter.eval(compile);
            return (Map<String, Object>) pyObject.__tojava__(Map.class);
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
