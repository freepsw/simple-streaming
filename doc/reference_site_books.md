# [도서] NO1. 실시간 분석의 모든 것

## 1. 스트리밍 데이터 소개
-  데이터 유형 : 모니터링, 웹로그분석, 온라인 광고, SNS, IOT
-  스트리밍 데이터 특징 : seamless, loosely coupled, 높은 cardinality(집합의 크기)

## 2. 스트리밍 분석 아키텍처
1. 실시간 스트리밍 아키텍처 설계
 - 아키텍처 구성요소
 - 수집 ~ 처리/분석 ~ 저장 ~ 시각화 (Web Socket, Rendering)
2. 실시간 아키텍처의 특징
 - 고가용성 : 분산 + 복제
 - 낮은 응답시간 : 수집의 분산 + 데이터 흐름
 - 수평적 확장 : zookeeper 활용
3. 실시간 프로그래밍을 위한 언어
 - java, (scala or closure), js, Go(수집서버용 활용높음, fluentd)


## 3. Layer별 기술요소 검토
1.  수집 : 기존 체계 유지(대부분), Go(적은 메모리공간, 동시접속 처리 효과 탁월)
2.  데이터 흐름 : Flume(GC 부하 증가) or Kafka (Connect활용, 일부 streaming 처리 가능)
3.  처리 : spark, storm, kafka streaming, flink, samza
4.  저장 :

-  Storage : HDFS

- Memory Storage : Alluxio https://www.youtube.com/watch?v=QVtxDpA-jis

-  DBMS : NoSQL(Hbase, Elasticsearch, Casandra),

- NewSQL(MemSQL, voltDB, Snappy Data, Ignite, Geode)
    - Apache Geode : Pivotal의 Gemfire를 OSS로 공개,

- Memory Cache (key - value)
  * redis : 가장 단순한 형태의 memory Cache, 3.0부터 클러스터를 지원
  * aerospike : redis 대비 대규모 환경에서 cluster 관리 및 확장성이 좋음.
  * Ignite : 다양한 기능(cep, spark rdd, computing api 등)을 제공. GridGain 상용제품에 활용, 좀 더 구체적으로 확인할 필요 있음.
 -


https://news.ycombinator.com/item?id=10596859 참고
1. Geode vs redis
2. Geode vs Ignite

=> 결국 비슷하다..(내용 확인이 필요함.)

 Two Videos:
1. "Open Sourced GemFire In-Memory Distributed Database and Apache Contributors" (https://www.youtube.com/playlist?list=PL62pIycqXx-TTMXsq09BE...)
2. "Creating a Highly Scalable Stock Prediction System with R, Geode & Spring XD" (https://www.youtube.com/playlist?list=PL62pIycqXx-Rzd_HcjU7Y...)

Ignite vs Pivotal Gemfilre (Apache Geode) https://ignite.apache.org/use-cases/compare/gemfire.html
This is cool: "Ignite allows for most of the data structures from java.util.concurrent framework to be used in a distributed fashion." https://ignite.apache.org/features/datastructures.html

# [도서] No2. Mastering Apache Spark
https://jaceklaskowski.gitbooks.io/mastering-apache-spark/content/

## 1. Table of Contents
- 이 기술들이 실제 어떻게 사용되나?
- spark를 활용하여 어떻게 확장되나? (H2O, 타이탄 + GraphX, Stream-Flume+Kafka)
- 1장. 전체 개요, 모듈기능
- 2장. MLlib 모듈, Spark 설치 및 구축, K-means/Nive Base알고리즘 활용
- 3장. Checkpoint, Streaming 기술
- 4장. Spark SQL, Dataframe활용, Hive와 비교, Hive + Spark 연동
- 5장. GraphX, Neo4J연동(Mazerunner), Docker
- 6장. Graph기반 스토리지
 - 오픈소스기반 스토리지 소개, DataStax의 Titan),
 - Graph 프로세싱 TinkerPop (gremlin, A Graph Traversal Language)
- 7장. H2O를 활용한 spark 확장 (설치, Flow인터페이스 설명, Sparkling water 구조, 신경망 분류)
- 8장. Spark Databricks AWS
- 9장. Databricks 시각화 (원격 클러스터 관리, )

# No2. Spakr 관련 주요 topic
## 1. Spark 아키텍처 수립
 - https://0x0fff.com/spark-architecture/ (2015/12/17)
 - 이 토픽에서 작성된 메모리 모델은 1.6에서 새롭게 구성됨. 아래 토픽 참고

## 2. Spark Memory Model
 - https://0x0fff.com/spark-memory-management/

## 3. Understanding Spark Caching
 - http://sujee.net/2015/01/22/understanding-spark-caching/#.V6HFjvmLSUn
 - spark에서 2가지 cache option이 존재한다. (Raw storage , Serialized)
 - 결론적으로
  * raw caching은 memory에 적재될때 RDD 대비 2~4배로 증가하여 메모리가 할당되고,
  * Serialized caching은 RDD와 동일한 사이즈로 할당
  * 하지만 processing 성능 측면에서는 raw caching이 훨씬 더 빠른 성능(특히 iteration 시)
  * 그래서 작은 데이터는 raw caching을 사용(memory가 부족하면 GC 부하 증가)

## 4. Accelerate Spark Jobs from Hours to Seconds
 - https://dzone.com/articles/Accelerate-In-Memory-Processing-with-Spark-from-Hours-to-Seconds-With-Tachyon
 - Tachyon (memory storage, 현재는 Alluxio)를 활용하여 1시간 소요되는 data pipeline을 초 단위로 성능향상.
 - Alluxio 데모 영상이 있으니.. 나중에 강의에 포함할지 검토 필요..

## 5. Can Spark Streaming survive Chaos Monkey? (2015/3/11)
 - http://techblog.netflix.com/2015/03/can-spark-streaming-survive-chaos-monkey.html?m=1
 - Netflix의 chaos monkey 테스트 결과를 정리함.
 - Driver : client mode는 회복불가
 - Master : Single Master는 회복불가
 - Worker procss : 복구
 - Executor : worker 복구 후 executor 복구됨. worker에 의해 자동으로 relaunch됨.
 - Receiver : Executor와 동일 (Executor 내부 Thread로 동작함)
 - Worker Node :

## 6. Distributed, Real-time Joins and Aggregations on User Activity Events using Kafka Streams
http://www.confluent.io/blog/distributed-real-time-joins-and-aggregations-on-user-activity-events-using-kafka-streams
 - Kafka Stream을 활용하여 user의 click 횟수 및 최종 접속한 지역의 정보를 표시한다.
 - 지역별로 total user click 수를 집계하는 continuous query를 구현한다.
 - 만약 kafka stream과 같은 sw를 사용하지 않을 경우, 어떤 어려움이 있는지 설명한다.
 - Kafka Stream이란?
 http://www.confluent.io/blog/introducing-kafka-streams-stream-processing-made-simple
 - Kafka Stream 설명 : 22page When to use Kafka streams?
 http://embedslide.net/slide-introducing-kafka-streams-the-new-stream-processing-library-of-apache-kafka-berlin-buzzwords-2016-s575ee72858e69350208fc407.html

## 7. Comparison of Apache Stream Processing Frameworks
http://www.cakesolutions.net/teamblogs/comparison-of-apache-stream-processing-frameworks-part-1
- 실시간 분산 streaming 프레임워크를 선택할 때 중요하게 고려해야할 항목을 중심으로 OSS 비교
 - Runtime and Programming model.
 - State Management
 - Message Delivery Guarantees
 - Failures
 - Latency, Throughput and Scalability
 - Maturity and Adoption Level
 - Ease of Development and Ease of Operability
- 위 항목을 중심으로 현재 Project 및 Biz 요건에 맞는 OSS 선택
- PART 2에서는 각 항목별로 좀 더 구체적으로 비교
- 각 OSS가 어떤 use case에 좀 더 최적인지 가이드
http://www.cakesolutions.net/teamblogs/comparison-of-apache-stream-processing-frameworks-part-2


## 8. Four Things to Know About Reliable Spark Streaming with Typesafe and Databricks
http://www.slideshare.net/Typesafe_Inc/four-things-to-know-about-reliable-spark-streaming-with-typesafe-and-databricks#
- Spark streaming의 상세 아키텍처와 장애시 data loss를 제거하기 위한 전략
- 모든 단계(driver - receiver - processing - sink)에서 exactly once를 지원하기 위한 방법
- 특히 receiver는 kafka direct를 활용하여 at least once -> exactly once로 변경

## 1. Teach yourself Apache Spark – Guide for nerds!
  - https://www.linkedin.com/pulse/teach-yourself-apache-spark-guide-nerds-shrinath-parikh



# [교육] 관련 Learning Course 정보
## 1. EDX : Data Science and Engineering with Apache Spark XSeries

- 1.1  Introduction to Apache Spark
 * Basic Spark architecture
 * Common operations
 * How to avoid coding mistakes
 * How to debug your Spark program

- 1.2 Big Data Analysis with Apache Spark
 * How to use Apache Spark to perform data analysis
 * How to use parallel programming to explore data sets
 * Apply log mining, textual entity recognition and collaborative filtering techniques to real-world data questions

- 1.3 Distributed Machine Learning with Apache Spark
 * The underlying statistical and algorithmic principles required to develop scalable real-world machine learning pipelines
 * Exploratory data analysis, feature extraction, supervised learning, and model evaluation
 * Application of these principles using Spark
 * How to implement distributed algorithms for fundamental statistical models

 - 1.4 Data Science and Engineering with Apache Spark
 * How to use Spark and its libraries to solve big data problems
 * How to approach large scale data science and engineering problems
 * Spark's APIs, architecture and many internal details
 * The trade-offs between communication and computation in a distributed environment
 * Use cases for Spark

##2. Developing Apache Spark Applications (유료, Sept 19-21 Melbourne, AUS	$2805)
https://www.mapr.com/services/mapr-academy/developing-apache-spark-applications-training-course-instructor-led

- 1 Spark 소개
 * 장점, component 소개
- 2. Load and inspect data (RDD를 이용하여 data 전처리 및 저장)
- 3. simple spark Applications
- 4. Pair RDD를 이용한 실습 (why, transformation/action 함수)
- 5. Spark Dataframe 활용하기 (user defined function)
- 6. Monitoring Spark Applications (Debug and tune spark Applications)
- 7. Spark Data Pipelines
- 8. Create apache spark streaming application (spark SQL, define windos operation, fault tolerance)
- 9. Graphx to analyze Flight Data
- 10. Flight Delay를 예측하기 위한 MLlib 활용

##3. Berkley AmpCamp (http://ampcamp.berkeley.edu/big-data-mini-course/)
- 1.1. Introductory Exercises
 - Scala - a quick crashcourse on the Scala language and command line interface.
 - Spark (project homepage) - a fast cluster compute engine.
 - Shark (project homepage) - a SQL layer on top of Spark.
1.2. Advanced Exercises
These can be done in any order according to your interests.

Spark Streaming (project homepage) - A stream processing layer on top of Spark.
Machine Learning with MLLib (project homepage) - Build a movie recommender with Spark.
Graph Analytics with GraphX (project homepage) - Explore graph-structured data (e.g., Web-Graph) and graph algorithms (e.g., PageRank) with GraphX.
Tachyon (project homepage) - Deploy a reliable in-memory filesystem across the cluster.
BlinkDB (project homepage) - Use SQL with statistical sampling to decrease latency.
