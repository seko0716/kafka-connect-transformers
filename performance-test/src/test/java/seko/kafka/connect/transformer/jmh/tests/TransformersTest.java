package seko.kafka.connect.transformer.jmh.tests;


import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.openjdk.jmh.annotations.*;
import seko.kafka.connect.transformer.groovy.GroovyTransformer;
import seko.kafka.connect.transformer.js.JavaScriptTransformer;
import seko.kafka.connect.transformer.python.PythonTransformer;
import seko.kafka.connect.transformer.script.configs.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
//@Warmup(iterations = 3)
//@Measurement(iterations = 8)
public class TransformersTest {
    private final PythonTransformer<SourceRecord> pythonTransformer = new PythonTransformer<>();
    private final GroovyTransformer<SourceRecord> groovyTransformer = new GroovyTransformer<>();
    private final JavaScriptTransformer<SourceRecord> jsTransformer = new JavaScriptTransformer<>();
    private Map<String, Object> config;
    private Map<String, Object> jsConfig;
    @Param({"10000000"})
    private int N;
    private Map<String, Object> event;

    @Setup
    public void setup() {
        config = new HashMap<>();
        config.put(Configuration.KEY_SCRIPT_CONFIG, "source['qweqweq'] = 12312312; source");
        config.put(Configuration.VALUE_SCRIPT_CONFIG, "source['qweqweq'] = 12312312; source");
        jsConfig = new HashMap<>();
        jsConfig.put(Configuration.KEY_SCRIPT_CONFIG, "function keyTransform(source){ source.qweqweq = 12312312; return source;}");
        jsConfig.put(Configuration.VALUE_SCRIPT_CONFIG, "function valueTransform(source){ source.qweqweq = 12312312; return source;}");
        pythonTransformer.configure(config);
        groovyTransformer.configure(config);
        jsTransformer.configure(jsConfig);
        event = new HashMap<>();
        event.put("created_when", "2019-05-31T00:17:00.188Z");
    }

    @Benchmark
    public void jsTransformer() {
        SourceRecord transformed = jsTransformer.apply(new SourceRecord(null, null, "topic", 0, null, event));
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
    }

/*    @Benchmark
    public void groovyTransformer() {
        SourceRecord transformed = groovyTransformer.apply(new SourceRecord(null, null, "topic", 0, null, event));
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
    }

    @Benchmark
    public void pythonTransformer() {
        SourceRecord transformed = pythonTransformer.apply(new SourceRecord(null, null, "topic", 0, null, event));
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
    }*/
}
