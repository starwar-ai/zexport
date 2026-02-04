# JVM调优

<cite>
**本文档引用文件**   
- [README.md](file://README.md)
- [OPTIMIZATION_PLAN.md](file://eplus-framework/OPTIMIZATION_PLAN.md)
- [OPTIMIZATION_PLAN.md](file://eplus-module-infra/OPTIMIZATION_PLAN.md)
- [application-dev.yaml](file://yudao-server/src/main/resources/application-dev.yaml)
- [application-prod.yaml](file://yudao-server/src/main/resources/application-prod.yaml)
- [Dockerfile](file://yudao-server/Dockerfile)
- [BizThreadPoolConfig.java](file://yudao-framework/eplus-spring-boot-starter-thread-pool/src/main/java/com/syj/eplus/framework/thread/pool/BizThreadPoolConfig.java)
</cite>

## 目录
1. [引言](#引言)
2. [JVM内存结构与配置](#jvm内存结构与配置)
3. [垃圾回收器选择与配置](#垃圾回收器选择与配置)
4. [GC日志分析与常见问题诊断](#gc日志分析与常见问题诊断)
5. [生产环境JVM参数推荐](#生产环境jvm参数推荐)
6. [性能监控与指标设置](#性能监控与指标设置)
7. [内存泄漏与线程死锁排查](#内存泄漏与线程死锁排查)
8. [总结](#总结)

## 引言

本JVM调优文档旨在为Eplus Admin Server企业级ERP管理系统提供全面的Java虚拟机性能调优指导。该系统基于Spring Boot 2.7.18构建，采用Java 17运行环境，涵盖供应链、销售、仓储、财务、CRM等多个业务领域，是一个复杂的企业级应用。

通过对项目架构和配置文件的分析，我们发现系统已经集成了多种性能优化机制，包括Druid数据库连接池、Redis缓存、异步任务处理等。然而，为了确保系统在高并发、大数据量场景下的稳定性和高性能，必须对JVM进行精细化调优。

本文档将系统介绍堆内存、栈内存和元空间的配置参数，提供GC日志分析工具使用指南和常见GC问题诊断方法，介绍不同垃圾回收器的适用场景和配置参数，包含内存泄漏检测和线程死锁排查技巧，并提供生产环境JVM参数推荐配置和性能监控指标设置。

**Section sources**
- [README.md](file://README.md#L1-L747)

## JVM内存结构与配置

Java虚拟机（JVM）内存主要分为堆内存（Heap）、栈内存（Stack）和元空间（Metaspace）三大部分。合理的内存配置是保证应用性能和稳定性的基础。

### 堆内存配置

堆内存是JVM中最大的一块内存区域，用于存储对象实例和数组。堆内存的配置主要通过以下参数控制：

- **-Xms**: 初始堆大小
- **-Xmx**: 最大堆大小
- **-XX:NewRatio**: 老年代与新生代的比例
- **-XX:SurvivorRatio**: Eden区与Survivor区的比例

根据项目中的Dockerfile配置，当前生产环境的JVM堆内存设置为512MB：
```
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"
```

对于企业级ERP系统，建议根据实际物理内存和应用负载进行调整。一般建议：
- 初始堆大小和最大堆大小设置为相同值，避免运行时动态调整带来的性能波动
- 对于8GB内存的服务器，可设置为-Xms4g -Xmx4g
- 新生代大小应足够大，以容纳大多数短生命周期对象

### 栈内存配置

栈内存用于存储线程的局部变量、方法调用和返回值。每个线程都有独立的栈空间。

- **-Xss**: 设置线程栈大小

默认情况下，Java 17的线程栈大小为1MB。对于线程数量较多的应用，可以适当减小栈大小以节省内存，但需确保不会导致StackOverflowError。

### 元空间配置

元空间用于存储类的元数据信息，取代了JDK 8之前的永久代（PermGen）。

- **-XX:MetaspaceSize**: 初始元空间大小
- **-XX:MaxMetaspaceSize**: 最大元空间大小

由于本系统包含大量业务模块和依赖，类的数量较多，建议设置合理的元空间大小：
```
-XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m
```

**Section sources**
- [Dockerfile](file://yudao-server/Dockerfile#L1-L23)
- [application-dev.yaml](file://yudao-server/src/main/resources/application-dev.yaml#L1-L225)

## 垃圾回收器选择与配置

垃圾回收器的选择对应用性能有重大影响。不同的垃圾回收器适用于不同的应用场景。

### Serial收集器

单线程收集器，适用于单核CPU或小型应用。使用`-XX:+UseSerialGC`启用。

### Parallel收集器

多线程收集器，关注吞吐量。使用`-XX:+UseParallelGC`启用。适合后台计算密集型应用。

### CMS收集器

以最短停顿时间为目标的收集器。使用`-XX:+UseConcMarkSweepGC`启用。适合对响应时间敏感的应用。

### G1收集器

兼顾吞吐量和停顿时间的收集器。使用`-XX:+UseG1GC`启用。推荐用于大内存应用。

根据本系统的特性（企业级ERP，多模块，高并发），推荐使用G1垃圾回收器。配置示例如下：
```
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
-XX:G1ReservePercent=15
-XX:InitiatingHeapOccupancyPercent=45
```

G1收集器能够将堆内存划分为多个区域，优先回收垃圾最多的区域，有效控制GC停顿时间。

**Section sources**
- [application-dev.yaml](file://yudao-server/src/main/resources/application-dev.yaml#L1-L225)
- [application-prod.yaml](file://yudao-server/src/main/resources/application-prod.yaml#L1-L245)

## GC日志分析与常见问题诊断

GC日志是诊断JVM性能问题的重要工具。通过分析GC日志，可以了解内存分配、垃圾回收频率和停顿时间等关键信息。

### GC日志开启

要开启GC日志，需要添加以下JVM参数：
```
-XX:+PrintGC
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+PrintGCTimeStamps
-Xloggc:/path/to/gc.log
```

### GC日志分析工具

常用的GC日志分析工具包括：
- **GCViewer**: 开源的GC日志可视化工具
- **GCEasy**: 在线GC日志分析服务
- **JDK自带工具**: jstat, jvisualvm

### 常见GC问题诊断

#### 内存泄漏

内存泄漏表现为老年代内存持续增长，最终导致Full GC频繁发生。诊断方法：
- 使用jmap生成堆转储文件
- 使用jhat或Eclipse MAT分析对象引用关系
- 查找不应该存在的对象引用链

#### 频繁GC

频繁GC可能是由于堆内存过小或对象创建速度过快。解决方案：
- 增加堆内存大小
- 优化代码，减少临时对象创建
- 调整新生代大小

#### 长时间停顿

长时间停顿通常由Full GC引起。解决方案：
- 使用G1或ZGC等低延迟收集器
- 减少大对象的创建
- 避免在GC期间执行耗时操作

**Section sources**
- [application-dev.yaml](file://yudao-server/src/main/resources/application-dev.yaml#L1-L225)
- [application-prod.yaml](file://yudao-server/src/main/resources/application-prod.yaml#L1-L245)

## 生产环境JVM参数推荐

基于对本系统的分析，推荐以下生产环境JVM参数配置：

```bash
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
-XX:G1ReservePercent=15
-XX:InitiatingHeapOccupancyPercent=45
-Xms4g
-Xmx4g
-XX:MetaspaceSize=256m
-XX:MaxMetaspaceSize=512m
-XX:+PrintGC
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+PrintGCTimeStamps
-Xloggc:/opt/logs/gc.log
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=5
-XX:GCLogFileSize=100M
-Djava.security.egd=file:/dev/./urandom
```

这些参数的配置考虑了以下因素：
- 系统为8核16GB内存的服务器，分配4GB堆内存
- 使用G1收集器以平衡吞吐量和延迟
- 合理设置元空间大小，避免动态扩展开销
- 开启详细的GC日志，便于问题诊断

**Section sources**
- [Dockerfile](file://yudao-server/Dockerfile#L1-L23)
- [application-prod.yaml](file://yudao-server/src/main/resources/application-prod.yaml#L1-L245)

## 性能监控与指标设置

有效的性能监控是确保系统稳定运行的关键。本系统已经集成了多种监控组件。

### 内置监控

系统内置了以下监控组件：
- **Druid监控**: 数据库连接池监控，SQL执行监控
- **Spring Boot Actuator**: 应用健康检查、指标监控
- **定时任务监控**: 任务执行日志、失败告警

### 推荐集成

建议集成以下监控工具：
- **Prometheus + Grafana**: 指标采集和可视化
- **SkyWalking**: APM性能监控、链路追踪
- **ELK Stack**: 日志收集和分析

### 关键监控指标

应重点关注以下JVM相关指标：
- **堆内存使用率**: 应保持在70%以下
- **GC频率**: Minor GC应小于1次/分钟，Full GC应极少发生
- **GC停顿时间**: 平均停顿时间应小于200ms
- **线程数**: 活跃线程数应稳定
- **类加载数**: 应稳定，避免持续增长

通过设置告警规则，当指标超出阈值时及时通知运维人员。

**Section sources**
- [application-dev.yaml](file://yudao-server/src/main/resources/application-dev.yaml#L1-L225)
- [application-prod.yaml](file://yudao-server/src/main/resources/application-prod.yaml#L1-L245)

## 内存泄漏与线程死锁排查

内存泄漏和线程死锁是Java应用中常见的性能问题，需要系统的方法进行排查。

### 内存泄漏排查

内存泄漏是指不再使用的对象无法被垃圾回收器回收，导致内存占用持续增长。

#### 排查步骤

1. **监控内存使用情况**: 使用jstat观察老年代内存增长趋势
2. **生成堆转储文件**: 使用jmap -dump:format=b,file=heap.hprof <pid>
3. **分析堆转储文件**: 使用Eclipse MAT或jhat分析
4. **查找泄漏点**: 查找占用内存最多的对象及其引用链

#### 常见泄漏场景

- 静态集合类持有对象引用
- 缓存未设置过期策略
- 监听器未正确注销
- 线程局部变量未清理

### 线程死锁排查

线程死锁是指多个线程相互等待对方释放锁，导致所有线程都无法继续执行。

#### 排查步骤

1. **生成线程转储**: 使用jstack <pid> > thread_dump.txt
2. **分析线程状态**: 查找处于BLOCKED状态的线程
3. **识别死锁**: jstack会自动检测并报告死锁

#### 预防措施

- 按固定顺序获取锁
- 使用tryLock设置超时
- 避免在持有锁时调用外部方法
- 使用工具类如ReentrantLock

**Section sources**
- [OPTIMIZATION_PLAN.md](file://eplus-framework/OPTIMIZATION_PLAN.md#L1-L572)
- [OPTIMIZATION_PLAN.md](file://eplus-module-infra/OPTIMIZATION_PLAN.md#L1-L800)

## 总结

JVM调优是一个系统工程，需要综合考虑应用特性、硬件环境和业务需求。对于Eplus Admin Server这样的企业级ERP系统，建议采取以下调优策略：

1. **合理配置内存**: 根据服务器配置设置合适的堆内存和元空间大小
2. **选择合适的垃圾回收器**: 推荐使用G1收集器以平衡吞吐量和延迟
3. **开启详细GC日志**: 便于问题诊断和性能分析
4. **建立完善的监控体系**: 实时监控关键性能指标
5. **定期进行性能测试**: 验证调优效果

通过持续的监控和优化，可以确保系统在高负载下依然保持良好的性能和稳定性。同时，建议建立性能基线，当性能指标偏离基线时及时进行分析和调优。

JVM调优不是一蹴而就的过程，需要根据实际运行情况进行持续的观察、分析和调整。建议建立性能问题响应机制，当出现性能问题时能够快速定位和解决。