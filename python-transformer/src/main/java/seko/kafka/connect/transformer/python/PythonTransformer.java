package seko.kafka.connect.transformer.python;

import org.apache.kafka.connect.connector.ConnectRecord;
import seko.kafka.connect.transformer.script.AbstractScriptTransformer;

public class PythonTransformer<R extends ConnectRecord<R>> extends AbstractScriptTransformer<R> {
    @Override
    public String getScripEngineName() {
        return "python";
    }
}
