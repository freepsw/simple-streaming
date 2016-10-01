# 실시간 아키텍처 커리큘럼 설계

## 1. 실시간 분산처리에 필요한 Open Source 유형 및 특징

## 2. 유형별 실시간 분산처리 아키텍처 소개

## 3. 실시간 아키텍처 설계(조별 과제) ? 또는 개인별 과제 (설계는 같이 하고, 구현은 개인별로 하는것도 검토)
 - 개인별 역량에 따라서 1인 또는 조별로 진행도 가능한데..
 - 어느정도 수준을 예상하고 진행할지 결정 필요

3.1 아키텍처 리뷰 (조별 발표 및 보완사항 검토)

## 4. 프로젝트 실습 환경 구축

 4.1  실습 환경 소개
 - 실시간 처리를 위한 유스케이스 설명 (NW Access Log, srcIP, desIP, cnt)
  - 수집 : Input -> collect(filtering) -> msgQueue(Streaming)
  - 처리 : processing(filter-broadxast, reducety-accumulator, dataframe-sparkSQL, savetoES, savetoRedis)
  - 저장 & 시각화 : Elasticsearch-Kibana + Nodejs-D3js(redis and spark SQL)
  '#' 고려사항
   * graphX or sparkML을 포함할 수 있는 유스케이스를 포함하는 것이 좋을지..
 - 실습 아키텍처 구성도 (HW, SW, I/F, Open Source)
 4.2 실습환경 구축
  - 3개 VM (Mem 8G, CPU core ?, Disk ?)
  - 1 Node (DataGenerator, flume, kafka, redis)
  - 2 Node (spark)
  - 3 Node (Elasticsearch, kibana, nodejs)

*  모든 오픈소스를 설치하고 환경을 구성해야 하는지?
* 기본 역량이 없는 사람은 여기서 많은 시간이 소요되어 진행이 어려움.

## 5. 프로젝트 구축 및 실행
5.1 Collect : Flume 수집 환경 설정 및 Interceptor 구현

5.2 Queue : Kafka Topic 생성 및 Kafka Streaming 구현 && Kafka Connect 설정? 고민중..

5.3 Processing : Spark Project 구성 (Intellij + scala plugin)
 -
