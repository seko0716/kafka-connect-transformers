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

##### Run progress: 0.00% complete, ETA 00:10:00
###### Fork: 1 of 2
###### Warmup Iteration   1: 468.897 ns/op
###### Warmup Iteration   2: 443.473 ns/op
###### Warmup Iteration   3: 442.065 ns/op
###### Warmup Iteration   4: 442.258 ns/op
###### Warmup Iteration   5: 442.716 ns/op
Iteration   1: 442.323 ns/op
Iteration   2: 442.190 ns/op
Iteration   3: 443.771 ns/op
Iteration   4: 444.009 ns/op
Iteration   5: 443.093 ns/op

##### Run progress: 16.67% complete, ETA 00:08:28
###### Fork: 2 of 2
###### Warmup Iteration   1: 458.327 ns/op
###### Warmup Iteration   2: 442.437 ns/op
###### Warmup Iteration   3: 442.079 ns/op
###### Warmup Iteration   4: 441.136 ns/op
###### Warmup Iteration   5: 442.744 ns/op
Iteration   1: 443.737 ns/op
Iteration   2: 442.999 ns/op
Iteration   3: 442.433 ns/op
Iteration   4: 441.687 ns/op
Iteration   5: 440.508 ns/op


Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.groovyTransformer":
  442.675 ±(99.9%) 1.631 ns/op [Average]
  (min, avg, max) = (440.508, 442.675, 444.009), stdev = 1.079
  CI (99.9%): [441.044, 444.306] (assumes normal distribution)


###### JMH version: 1.21
###### VM version: JDK 1.8.0_222, OpenJDK 64-Bit Server VM, 25.222-b05
###### VM invoker: /usr/lib/jvm/java-8-openjdk/jre/bin/java
###### VM options: -Xms2G -Xmx2G
###### Warmup: 5 iterations, 10 s each
###### Measurement: 5 iterations, 10 s each
###### Timeout: 10 min per iteration
###### Threads: 1 thread, will synchronize iterations
###### Benchmark mode: Average time, time/op
###### Benchmark: seko.kafka.connect.transformer.jmh.tests.TransformersTest.jsTransformer
###### Parameters: (N = 10000000)

##### Run progress: 33.33% complete, ETA 00:06:45
###### Fork: 1 of 2
###### Warmup Iteration   1: 2291639.254 ns/op
###### Warmup Iteration   2: 1594184.292 ns/op
###### Warmup Iteration   3: 1492610.319 ns/op
###### Warmup Iteration   4: 1418905.840 ns/op
###### Warmup Iteration   5: 1550988.214 ns/op
Iteration   1: 1592593.231 ns/op
Iteration   2: 1587437.666 ns/op
Iteration   3: 1599927.316 ns/op
Iteration   4: 1613364.911 ns/op
Iteration   5: 1590770.850 ns/op

##### Run progress: 50.00% complete, ETA 00:05:03
###### Fork: 2 of 2
###### Warmup Iteration   1: 2220655.984 ns/op
###### Warmup Iteration   2: 1584667.295 ns/op
###### Warmup Iteration   3: 1446964.746 ns/op
###### Warmup Iteration   4: 1456940.618 ns/op
###### Warmup Iteration   5: 1511296.238 ns/op
Iteration   1: 1465677.763 ns/op
Iteration   2: 1598188.439 ns/op
Iteration   3: 1545632.732 ns/op
Iteration   4: 1529868.844 ns/op
Iteration   5: 1652716.223 ns/op


Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.jsTransformer":
  1577617.798 ±(99.9%) 78341.136 ns/op [Average]
  (min, avg, max) = (1465677.763, 1577617.798, 1652716.223), stdev = 51817.811
  CI (99.9%): [1499276.661, 1655958.934] (assumes normal distribution)


###### JMH version: 1.21
###### VM version: JDK 1.8.0_222, OpenJDK 64-Bit Server VM, 25.222-b05
###### VM invoker: /usr/lib/jvm/java-8-openjdk/jre/bin/java
###### VM options: -Xms2G -Xmx2G
###### Warmup: 5 iterations, 10 s each
###### Measurement: 5 iterations, 10 s each
###### Timeout: 10 min per iteration
###### Threads: 1 thread, will synchronize iterations
###### Benchmark mode: Average time, time/op
###### Benchmark: seko.kafka.connect.transformer.jmh.tests.TransformersTest.pythonTransformer
###### Parameters: (N = 10000000)

##### Run progress: 66.67% complete, ETA 00:03:22
###### Fork: 1 of 2
###### Warmup Iteration   1: 1312583.697 ns/op
###### Warmup Iteration   2: 831458.403 ns/op
###### Warmup Iteration   3: 782676.953 ns/op
###### Warmup Iteration   4: 727904.559 ns/op
###### Warmup Iteration   5: 755433.337 ns/op
Iteration   1: 770601.967 ns/op
Iteration   2: 773515.414 ns/op
Iteration   3: 789581.552 ns/op
Iteration   4: 785774.658 ns/op
Iteration   5: 805311.028 ns/op

##### Run progress: 83.33% complete, ETA 00:01:41
###### Fork: 2 of 2
###### Warmup Iteration   1: 1288633.465 ns/op
###### Warmup Iteration   2: 954229.133 ns/op
###### Warmup Iteration   3: 813393.705 ns/op
###### Warmup Iteration   4: 792680.325 ns/op
###### Warmup Iteration   5: 791766.367 ns/op
Iteration   1: 773771.427 ns/op
Iteration   2: 759612.313 ns/op
Iteration   3: 759159.350 ns/op
Iteration   4: 763007.510 ns/op
Iteration   5: 768447.262 ns/op


Result "seko.kafka.connect.transformer.jmh.tests.TransformersTest.pythonTransformer":
  774878.248 ±(99.9%) 22224.511 ns/op [Average]
  (min, avg, max) = (759159.350, 774878.248, 805311.028), stdev = 14700.138
  CI (99.9%): [752653.737, 797102.759] (assumes normal distribution)


##### Run complete. Total time: 00:10:06

###REMEMBER: 
The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.



|Benchmark                         |      (N)  | Mode  | Cnt |       Score |       Error | Units |
| -------------------------------- | --------- | ----- | --- | ----------- | ----------- | ----- |
|TransformersTest.groovyTransformer|  10000000 | avgt  | 10  |     442.675 |±     1.631  | ns/op |
|TransformersTest.jsTransformer    |  10000000 | avgt  | 10  | 1577617.798 |± 78341.136  | ns/op |
|TransformersTest.pythonTransformer|  10000000 | avgt  | 10  |  774878.248 |± 22224.511  | ns/op |


