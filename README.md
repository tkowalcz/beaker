# beaker

[![Gitter](https://badges.gitter.im/tkowalcz-beaker/Lobby.svg)](https://gitter.im/tkowalcz-beaker/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# NUMA
Verified thread assignment using `htop`. Verified memory assignment using `numastat <PID>`.

Output from x1.32xlarge instance (128 CPUs, 2TB RAM) running on `openjdk version "11.0.1" 2018-10-16`.

```
NUMA topology
nodes: 4
node 0 size = 491822mb
{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79}
node 1 size = 491899mb
{16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95}
node 2 size = 491899mb
{32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111}
node 3 size = 491898mb
{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127}
Distance (0,0): 10
Distance (0,1): 20
Distance (0,2): 20
Distance (0,3): 20
Distance (1,0): 20
Distance (1,1): 10
Distance (1,2): 20
Distance (1,3): 20
Distance (2,0): 20
Distance (2,1): 20
Distance (2,2): 10
Distance (2,3): 20
Distance (3,0): 20
Distance (3,1): 20
Distance (3,2): 20
Distance (3,3): 10
```

```
Benchmark                                              (dataNumaNode)  (dataSizeMegabytes)  (threadNumaNode)   Mode  Cnt         Score   Error   Units
NUMAMicrobenchmark.traverseByteBuffer                               0                  100                 0  thrpt              0.129          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               0                  100                 0  thrpt       13294664.253          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               0                  100                 1  thrpt              0.075          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               0                  100                 1  thrpt        7892870.810          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               0                  100                 2  thrpt              0.076          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               0                  100                 2  thrpt        7965735.518          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               0                  100                 3  thrpt              0.074          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               0                  100                 3  thrpt        7580413.572          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               1                  100                 0  thrpt              0.075          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               1                  100                 0  thrpt        7895685.593          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               1                  100                 1  thrpt              0.127          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               1                  100                 1  thrpt       13353148.173          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               1                  100                 2  thrpt              0.077          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               1                  100                 2  thrpt        8051246.387          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               1                  100                 3  thrpt              0.070          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               1                  100                 3  thrpt        7451326.321          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               2                  100                 0  thrpt              0.075          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               2                  100                 0  thrpt        7836982.561          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               2                  100                 1  thrpt              0.076          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               2                  100                 1  thrpt        8019348.980          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               2                  100                 2  thrpt              0.127          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               2                  100                 2  thrpt       13397067.832          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               2                  100                 3  thrpt              0.074          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               2                  100                 3  thrpt        7805468.604          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               3                  100                 0  thrpt              0.073          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               3                  100                 0  thrpt        7686027.807          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               3                  100                 1  thrpt              0.075          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               3                  100                 1  thrpt        7867699.669          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               3                  100                 2  thrpt              0.076          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               3                  100                 2  thrpt        8030222.041          ops/ms
NUMAMicrobenchmark.traverseByteBuffer                               3                  100                 3  thrpt              0.128          ops/ms
NUMAMicrobenchmark.traverseByteBuffer:throughputBytes               3                  100                 3  thrpt       13335603.824          ops/ms
```

![Beaker](https://vignette.wikia.nocookie.net/muppet/images/0/05/Beaker.jpg/revision/latest?cb=20101015151246)
