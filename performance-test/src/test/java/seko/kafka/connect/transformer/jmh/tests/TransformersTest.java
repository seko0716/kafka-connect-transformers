package seko.kafka.connect.transformer.jmh.tests;


import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.junit.Assert;
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
    private Map<String, Object> groovyConfig;
    private Map<String, Object> jsConfig;
    private Map<String, Object> pythonConfig;
    @Param({"10000000"})
    private int N;
    private Map<String, Object> event;
    private SourceRecord topic;

    @Setup
    public void setup() {

        groovyConfig = new HashMap<>();
        groovyConfig.put(Configuration.KEY_SCRIPT_CONFIG, "source['qweqweq'] = 12312312; source");
        groovyConfig.put(Configuration.VALUE_SCRIPT_CONFIG, "source['qweqweq'] = 12312312; source");
        groovyTransformer.configure(groovyConfig);

        jsConfig = new HashMap<>();
        jsConfig.put(Configuration.KEY_SCRIPT_CONFIG, "function keyTransform(source){ source.qweqweq = 12312312; return source;}");
        jsConfig.put(Configuration.VALUE_SCRIPT_CONFIG, "function valueTransform(source){ source.qweqweq = 12312312; return source;}");
        jsTransformer.configure(jsConfig);

        pythonConfig = new HashMap<>();
        pythonConfig.put(Configuration.KEY_SCRIPT_CONFIG, "def keyTransform(source): source['qweqweq'] = 12312312; return source");
        pythonConfig.put(Configuration.VALUE_SCRIPT_CONFIG, "def valueTransform(source): source['qweqweq'] = 12312312; return source");
        pythonTransformer.configure(pythonConfig);

        event = new HashMap<>();
        event.put("created_when", "2019-05-31T00:17:00.188Z");

        topic = new SourceRecord(null, null, "topic", 0, null, event);
    }

    @Benchmark
    public void jsTransformer() {
        SourceRecord transformed = jsTransformer.apply(topic);
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        validate(stringObjectMap);
    }

    @Benchmark
    public void groovyTransformer() {
        SourceRecord transformed = groovyTransformer.apply(topic);
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        validate(stringObjectMap);
    }

    @Benchmark
    public void pythonTransformer() {
        SourceRecord transformed = pythonTransformer.apply(topic);
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        validate(stringObjectMap);
    }

    private void validate(Map<String, Object> stringObjectMap) {
        Assert.assertEquals(12312312, stringObjectMap.get("qweqweq"));
        Assert.assertEquals(2, stringObjectMap.size());
    }
}
