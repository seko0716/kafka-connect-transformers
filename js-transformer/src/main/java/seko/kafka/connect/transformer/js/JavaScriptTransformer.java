package seko.kafka.connect.transformer.js;

import org.apache.kafka.connect.connector.ConnectRecord;
import seko.kafka.connect.transformer.script.AbstractScriptTransformer;

public class JavaScriptTransformer<R extends ConnectRecord<R>> extends AbstractScriptTransformer<R> {
    @Override
    public String getScripEngineName() {
        return "JavaScript";
    }
}
