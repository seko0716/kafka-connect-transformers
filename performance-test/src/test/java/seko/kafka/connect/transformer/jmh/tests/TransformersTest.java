package seko.kafka.connect.transformer.jmh.tests;


import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.junit.Assert;
import org.openjdk.jmh.annotations.*;
import seko.kafka.connect.transformer.groovy.GroovyTransformer;
import seko.kafka.connect.transformer.python.ScriptEngineTransformer;
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
    private final ScriptEngineTransformer<SourceRecord> scriptEngineTransformerJs = new ScriptEngineTransformer<>();
    private final ScriptEngineTransformer<SourceRecord> scriptEngineTransformerGroovy = new ScriptEngineTransformer<>();
    private final ScriptEngineTransformer<SourceRecord> scriptEngineTransformerPython = new ScriptEngineTransformer<>();
    private final ScriptEngineTransformer<SourceRecord> scriptEngineTransformerRuby = new ScriptEngineTransformer<>();
    private final GroovyTransformer<SourceRecord> groovyTransformer = new GroovyTransformer<>();
    private Map<String, Object> groovyConfig;
    private Map<String, Object> groovySeConfig;
    private Map<String, Object> jsConfig;
    private Map<String, Object> pythonConfig;
    private Map<String, Object> rubyConfig;
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

        groovySeConfig = new HashMap<>();
        groovySeConfig.put(Configuration.KEY_SCRIPT_CONFIG, "def keyTransform(def source) {return source + '123' }; return keyTransform(source)");
        groovySeConfig.put(Configuration.VALUE_SCRIPT_CONFIG, "def valueTransform(def source) {source.put('qweqweq', 12312312); return source; }; return valueTransform(source)");
        groovySeConfig.put(Configuration.SCRIP_ENGINE_NAME, "groovy");
        scriptEngineTransformerGroovy.configure(groovySeConfig);

        jsConfig = new HashMap<>();
        jsConfig.put(Configuration.KEY_SCRIPT_CONFIG, "function keyTransform(source){ source.qweqweq = 12312312; return source;}");
        jsConfig.put(Configuration.VALUE_SCRIPT_CONFIG, "function valueTransform(source){ source.qweqweq = 12312312; return source;}");
        jsConfig.put(Configuration.SCRIP_ENGINE_NAME, "JavaScript");
        scriptEngineTransformerJs.configure(jsConfig);

        pythonConfig = new HashMap<>();
        pythonConfig.put(Configuration.KEY_SCRIPT_CONFIG, "def keyTransform(source): source['qweqweq'] = 12312312; return source");
        pythonConfig.put(Configuration.VALUE_SCRIPT_CONFIG, "def valueTransform(source): source['qweqweq'] = 12312312; return source");
        pythonConfig.put(Configuration.SCRIP_ENGINE_NAME, "python");
        scriptEngineTransformerPython.configure(pythonConfig);

        rubyConfig = new HashMap<>();
        rubyConfig.put(Configuration.SCRIP_ENGINE_NAME, "jruby");
        rubyConfig.put(Configuration.KEY_SCRIPT_CONFIG, "def keyTransform(source) return source + '123' end");
        rubyConfig.put(Configuration.VALUE_SCRIPT_CONFIG, "def valueTransform(source) source['qweqweq'] = 12312312; return source; end");
        scriptEngineTransformerRuby.configure(rubyConfig);

        event = new HashMap<>();
        event.put("created_when", "2019-05-31T00:17:00.188Z");

        topic = new SourceRecord(null, null, "topic", 0, null, event);
    }


    @Benchmark
    public void groovyTransformer() {
        SourceRecord transformed = groovyTransformer.apply(topic);
        validate(transformed);
    }

    @Benchmark
    public void jsTransformer() {
        SourceRecord transformed = scriptEngineTransformerJs.apply(topic);
        validate(transformed);
    }

    @Benchmark
    public void groovySeTransformer() {
        SourceRecord transformed = scriptEngineTransformerGroovy.apply(topic);
        validate(transformed);
    }

    @Benchmark
    public void pythonTransformer() {
        SourceRecord transformed = scriptEngineTransformerPython.apply(topic);
        validate(transformed);
    }

    @Benchmark
    public void rubyTransformer() {
        SourceRecord transformed = scriptEngineTransformerRuby.apply(topic);
        validate(transformed);
    }

    private void validate(SourceRecord transformed) {
        Map<String, Object> stringObjectMap = Requirements.requireMapOrNull(transformed.value(), "");
        Assert.assertNotNull(stringObjectMap.get("qweqweq"));
        Assert.assertEquals(2, stringObjectMap.size());
    }
}
