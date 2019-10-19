#Kafka script transformers

supported languages:
* Groovy
* JavaScript
* Python

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
  442.675 ±(99.9%) 1.631 ns/op [Average]
  (min, avg, max) = (440.508, 442.675, 444.009), stdev = 1.079
  CI (99.9%): [441.044, 444.306] (assumes normal distribution)

Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.jsTransformer":
  1577617.798 ±(99.9%) 78341.136 ns/op [Average]
  (min, avg, max) = (1465677.763, 1577617.798, 1652716.223), stdev = 51817.811
  CI (99.9%): [1499276.661, 1655958.934] (assumes normal distribution)

Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.pythonTransformer":
  1321.100 ±(99.9%) 13.768 ns/op [Average]
  (min, avg, max) = (1309.414, 1321.100, 1332.275), stdev = 9.106
  CI (99.9%): [1307.332, 1334.867] (assumes normal distribution)


##### Run complete. Total time: 00:10:06

### REMEMBER: 
The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.



|Benchmark                         |      (N)  | Mode  | Cnt |       Score |       Error | Units |
| -------------------------------- | --------- | ----- | --- | ----------- | ----------- | ----- |
|TransformersTest.groovyTransformer|  10000000 | avgt  | 10  |     442.675 |±     1.631  | ns/op |
|TransformersTest.jsTransformer    |  10000000 | avgt  | 10  | 1691179.191 |± 78341.136  | ns/op |
|TransformersTest.pythonTransformer|  10000000 | avgt  | 10  |    1321.100 |±    13.768  | ns/op |


