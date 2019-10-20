package seko.kafka.connect.transformer.groovy;

import org.apache.kafka.connect.connector.ConnectRecord;
import seko.kafka.connect.transformer.script.AbstractScriptTransformer;

public class GroovySeTransformer<R extends ConnectRecord<R>> extends AbstractScriptTransformer<R> {
    @Override
    public String getScripEngineName() {
        return "groovy";
    }
}
