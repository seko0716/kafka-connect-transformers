# Kafka script transformers

supported languages:
* Groovy
* JavaScript
* Python

# Configuration

## Examples:

### groovy
```json
{
  "transforms": "groovyTransform",
  "transforms.groovyTransform.type": "seko.kafka.connect.transformer.groovy.GroovyTransformer",
  "transforms.groovyTransform.key.script": "source.put('qweqweq', 12312312); return source;",
  "transforms.groovyTransform.value.script": "source.put('qweqweq', 12312312); return source;"
}
```

```json
{
  "transforms": "groovyTransform",
  "transforms.groovyTransform.type": "seko.kafka.connect.transformer.groovy.GroovyTransformer",
  "transforms.groovyTransform.key.script": "source.put('qweqweq', 12312312); return source;"
}
```

```json
{
  "transforms": "groovyTransform",
  "transforms.groovyTransform.type": "seko.kafka.connect.transformer.groovy.GroovyTransformer",
  "transforms.groovyTransform.value.script": "source.put('qweqweq', 12312312); return source;"
}
```

### groovy script engine
```json
{
  "transforms": "groovyEsTransform",
  "transforms.groovyTransform.type": "seko.kafka.connect.transformer.groovy.GroovySeTransformer",
  "transforms.groovyTransform.key.script": "def keyTransform(def source) {source.put('qweqweq', 12312312); return source; }",
  "transforms.groovyTransform.value.script": "def valueTransform(def source) {source.put('qweqweq', 12312312); return source; }"
}
```

### python
```json
{
  "transforms": "pythonTransform",
  "transforms.groovyTransform.type": "seko.kafka.connect.transformer.python.PythonTransformer",
  "transforms.groovyTransform.key.script": "def keyTransform(source): source['qweqweq'] = 12312312; return source",
  "transforms.groovyTransform.value.script": "def valueTransform(source): source['qweqweq'] = 12312312; return source"
}
```

### java script
```json
{
  "transforms": "jsTransform",
  "transforms.groovyTransform.type": "seko.kafka.connect.transformer.js.JavaScriptTransformer",
  "transforms.groovyTransform.key.script": "function keyTransform(source){ source.qweqweq = 12312312; return source;}",
  "transforms.groovyTransform.value.script": "function valueTransform(source){ source.qweqweq = 12312312; return source;}"
}
```

# JMH results

###### JMH version: 1.21
###### VM version: JDK 1.8.0_222, OpenJDK 64-Bit Server VM, 25.222-b05
###### VM invoker: /usr/lib/jvm/java-8-openjdk/jre/bin/java
###### VM options: -Xms2G -Xmx2G
###### Warmup: 5 iterations, 10 s each
###### Measurement: 5 iterations, 10 s each
###### Timeout: 10 min per iteration
###### Threads: 1 thread, will synchronize iterations
###### Benchmark mode: Average time, time/op
###### Benchmark: seko.kafka.connect.transformer.jmh.tests.TransformersTest.groovyTransformer
###### Parameters: (N = 10000000)

Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.groovyTransformer":
  - 442.675 ±(99.9%) 1.631 ns/op [Average]
  - (min, avg, max) = (440.508, 442.675, 444.009), stdev = 1.079s
  - CI (99.9%): [441.044, 444.306] (assumes normal distribution)

Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.jsTransformer":
  - 150.457 ±(99.9%) 11.876 ns/op [Average]
  - (min, avg, max) = (140.812, 150.457, 166.440), stdev = 7.856
  - CI (99.9%): [138.580, 162.333] (assumes normal distribution)

Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.pythonTransformer":
  - 792.442 ±(99.9%) 189.128 ns/op [Average]
  - (min, avg, max) = (662.980, 792.442, 985.057), stdev = 125.097
  - CI (99.9%): [603.314, 981.571] (assumes normal distribution)

Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.groovySeTransformer":
  - 124.776 ±(99.9%) 15.387 ns/op [Average]
  - (min, avg, max) = (114.271, 124.776, 150.406), stdev = 10.178
  - CI (99.9%): [109.389, 140.163] (assumes normal distribution)

##### Run complete. Total time: 00:10:06

### REMEMBER: 
The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.



|Benchmark                           |      (N)  | Mode  | Cnt |       Score |       Error | Units |
| ---------------------------------- | --------- | ----- | --- | ----------- | ----------- | ----- |
|TransformersTest.groovyTransformer  |  10000000 | avgt  | 10  |     442.675 |±     1.631  | ns/op |
|TransformersTest.groovySeTransformer|  10000000 | avgt  | 10  |     124.776 |±    15.387  | ns/op |
|TransformersTest.jsTransformer      |  10000000 | avgt  | 10  |     150.457 |±    11.876  | ns/op |
|TransformersTest.pythonTransformer  |  10000000 | avgt  | 10  |     792.442 |±   189.128  | ns/op |


