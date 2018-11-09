# beaker

[![Gitter](https://badges.gitter.im/tkowalcz-beaker/Lobby.svg)](https://gitter.im/tkowalcz-beaker/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# NUMA
Verified thread assignment using `htop`. Verified memory assignment using `numastat <PID>`.

## Output from x1.32xlarge instance (128 CPUs, 2TB RAM) running on `openjdk version "11.0.1" 2018-10-16`.

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

## Output from m5a.24xlarge instance (96 CPUs, 360GB RAM) running on `openjdk version "12-testing" 2019-03-19` from https://builds.shipilev.net/openjdk-panama/.

```
NUMA topology
nodes: 6
node 0 size = 63291mb
{0, 1, 2, 3, 4, 5, 6, 7, 48, 49, 50, 51, 52, 53, 54, 55}
node 1 size = 63375mb
{8, 9, 10, 11, 12, 13, 14, 15, 56, 57, 58, 59, 60, 61, 62, 63}
node 2 size = 63375mb
{16, 17, 18, 19, 20, 21, 22, 23, 64, 65, 66, 67, 68, 69, 70, 71}
node 3 size = 63375mb
{24, 25, 26, 27, 28, 29, 30, 31, 72, 73, 74, 75, 76, 77, 78, 79}
node 4 size = 63375mb
{32, 33, 34, 35, 36, 37, 38, 39, 80, 81, 82, 83, 84, 85, 86, 87}
node 5 size = 63374mb
{40, 41, 42, 43, 44, 45, 46, 47, 88, 89, 90, 91, 92, 93, 94, 95}
Distance (0,0): 10
Distance (0,1): 16
Distance (0,2): 16
Distance (0,3): 32
Distance (0,4): 32
Distance (0,5): 32
Distance (1,0): 16
Distance (1,1): 10
Distance (1,2): 16
Distance (1,3): 32
Distance (1,4): 32
Distance (1,5): 32
Distance (2,0): 16
Distance (2,1): 16
Distance (2,2): 10
Distance (2,3): 32
Distance (2,4): 32
Distance (2,5): 32
Distance (3,0): 32
Distance (3,1): 32
Distance (3,2): 32
Distance (3,3): 10
Distance (3,4): 16
Distance (3,5): 16
Distance (4,0): 32
Distance (4,1): 32
Distance (4,2): 32
Distance (4,3): 16
Distance (4,4): 10
Distance (4,5): 16
Distance (5,0): 32
Distance (5,1): 32
Distance (5,2): 32
Distance (5,3): 16
Distance (5,4): 16
Distance (5,5): 10

Benchmark                                                      (dataNumaNode)  (dataSizeMegabytes)  (threadNumaNode)   Mode  Cnt      Score   Error  Units
NUMAMicrobenchmark.readByteBufferInOrder                                    0                  100                 0  thrpt         290.813          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                0                  100                 0  thrpt       28271.586          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    0                  100                 1  thrpt         175.695          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                0                  100                 1  thrpt       17619.475          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    0                  100                 2  thrpt         181.045          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                0                  100                 2  thrpt       18094.502          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    0                  100                 3  thrpt         106.932          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                0                  100                 3  thrpt       10673.227          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    0                  100                 4  thrpt         107.359          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                0                  100                 4  thrpt       10710.892          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    0                  100                 5  thrpt         122.163          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                0                  100                 5  thrpt       12201.284          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    1                  100                 0  thrpt         179.368          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                1                  100                 0  thrpt       17921.834          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    1                  100                 1  thrpt         262.818          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                1                  100                 1  thrpt       27036.601          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    1                  100                 2  thrpt         182.513          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                1                  100                 2  thrpt       18226.272          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    1                  100                 3  thrpt         108.393          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                1                  100                 3  thrpt       10774.324          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    1                  100                 4  thrpt         107.643          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                1                  100                 4  thrpt       10754.325          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    1                  100                 5  thrpt         106.941          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                1                  100                 5  thrpt       10679.115          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    2                  100                 0  thrpt         171.026          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                2                  100                 0  thrpt       17557.412          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    2                  100                 1  thrpt         179.285          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                2                  100                 1  thrpt       17928.462          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    2                  100                 2  thrpt         290.217          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                2                  100                 2  thrpt       28211.950          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    2                  100                 3  thrpt         122.105          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                2                  100                 3  thrpt       12190.551          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    2                  100                 4  thrpt         109.979          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                2                  100                 4  thrpt       10957.890          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    2                  100                 5  thrpt         110.198          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                2                  100                 5  thrpt       10999.808          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    3                  100                 0  thrpt         107.695          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                3                  100                 0  thrpt       10779.521          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    3                  100                 1  thrpt         107.908          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                3                  100                 1  thrpt       10780.851          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    3                  100                 2  thrpt         123.347          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                3                  100                 2  thrpt       12359.667          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    3                  100                 3  thrpt         272.520          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                3                  100                 3  thrpt       27277.016          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    3                  100                 4  thrpt         175.118          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                3                  100                 4  thrpt       17481.787          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    3                  100                 5  thrpt         179.884          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                3                  100                 5  thrpt       17983.366          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    4                  100                 0  thrpt         108.360          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                4                  100                 0  thrpt       10830.954          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    4                  100                 1  thrpt         108.116          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                4                  100                 1  thrpt       10801.567          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    4                  100                 2  thrpt         107.446          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                4                  100                 2  thrpt       10744.622          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    4                  100                 3  thrpt         180.791          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                4                  100                 3  thrpt       17799.154          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    4                  100                 4  thrpt         273.749          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                4                  100                 4  thrpt       27374.882          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    4                  100                 5  thrpt         181.180          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                4                  100                 5  thrpt       18102.998          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    5                  100                 0  thrpt         122.028          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                5                  100                 0  thrpt       12212.834          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    5                  100                 1  thrpt         109.850          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                5                  100                 1  thrpt       10990.024          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    5                  100                 2  thrpt         111.814          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                5                  100                 2  thrpt       11176.366          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    5                  100                 3  thrpt         179.289          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                5                  100                 3  thrpt       17903.898          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    5                  100                 4  thrpt         185.862          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                5                  100                 4  thrpt       18246.304          ops/s
NUMAMicrobenchmark.readByteBufferInOrder                                    5                  100                 5  thrpt         273.430          ops/s
NUMAMicrobenchmark.readByteBufferInOrder:throughputMegabytes                5                  100                 5  thrpt       27332.954          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   0                  100                 0  thrpt           4.023          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               0                  100                 0  thrpt         421.874          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   0                  100                 1  thrpt           2.662          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               0                  100                 1  thrpt         266.180          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   0                  100                 2  thrpt           2.720          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               0                  100                 2  thrpt         271.972          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   0                  100                 3  thrpt           1.642          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               0                  100                 3  thrpt         164.204          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   0                  100                 4  thrpt           1.637          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               0                  100                 4  thrpt         163.719          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   0                  100                 5  thrpt           1.957          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               0                  100                 5  thrpt         195.744          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   1                  100                 0  thrpt           2.683          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               1                  100                 0  thrpt         268.323          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   1                  100                 1  thrpt           4.035          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               1                  100                 1  thrpt         398.582          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   1                  100                 2  thrpt           2.794          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               1                  100                 2  thrpt         279.403          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   1                  100                 3  thrpt           1.635          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               1                  100                 3  thrpt         163.487          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   1                  100                 4  thrpt           1.642          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               1                  100                 4  thrpt         164.180          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   1                  100                 5  thrpt           1.624          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               1                  100                 5  thrpt         162.351          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   2                  100                 0  thrpt           2.908          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               2                  100                 0  thrpt         281.154          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   2                  100                 1  thrpt           2.716          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               2                  100                 1  thrpt         271.605          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   2                  100                 2  thrpt           4.438          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               2                  100                 2  thrpt         419.156          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   2                  100                 3  thrpt           1.967          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               2                  100                 3  thrpt         196.735          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   2                  100                 4  thrpt           1.666          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               2                  100                 4  thrpt         166.596          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   2                  100                 5  thrpt           1.750          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               2                  100                 5  thrpt         170.138          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   3                  100                 0  thrpt           1.625          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               3                  100                 0  thrpt         162.531          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   3                  100                 1  thrpt           1.642          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               3                  100                 1  thrpt         164.202          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   3                  100                 2  thrpt           1.960          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               3                  100                 2  thrpt         195.973          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   3                  100                 3  thrpt           3.991          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               3                  100                 3  thrpt         399.071          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   3                  100                 4  thrpt           2.647          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               3                  100                 4  thrpt         264.713          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   3                  100                 5  thrpt           2.710          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               3                  100                 5  thrpt         271.022          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   4                  100                 0  thrpt           1.644          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               4                  100                 0  thrpt         164.404          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   4                  100                 1  thrpt           1.641          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               4                  100                 1  thrpt         164.131          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   4                  100                 2  thrpt           1.617          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               4                  100                 2  thrpt         161.745          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   4                  100                 3  thrpt           2.661          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               4                  100                 3  thrpt         266.145          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   4                  100                 4  thrpt           4.022          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               4                  100                 4  thrpt         402.237          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   4                  100                 5  thrpt           2.952          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               4                  100                 5  thrpt         285.326          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   5                  100                 0  thrpt           1.955          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               5                  100                 0  thrpt         195.504          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   5                  100                 1  thrpt           1.661          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               5                  100                 1  thrpt         166.108          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   5                  100                 2  thrpt           1.692          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               5                  100                 2  thrpt         169.179          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   5                  100                 3  thrpt           2.714          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               5                  100                 3  thrpt         271.375          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   5                  100                 4  thrpt           2.727          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               5                  100                 4  thrpt         272.698          ops/s
NUMAMicrobenchmark.readByteBufferRandomly                                   5                  100                 5  thrpt           3.995          ops/s
NUMAMicrobenchmark.readByteBufferRandomly:throughputMegabytes               5                  100                 5  thrpt         399.532          ops/s
```
![Beaker](https://vignette.wikia.nocookie.net/muppet/images/0/05/Beaker.jpg/revision/latest?cb=20101015151246)
