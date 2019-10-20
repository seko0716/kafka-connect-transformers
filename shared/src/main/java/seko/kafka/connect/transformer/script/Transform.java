package seko.kafka.connect.transformer.script;

import java.util.Map;

public interface Transform {
    default Map<String, Object> transform(Map<String, Object> source, String script) {
        return source;
    }

    String getScripEngineName();
}
