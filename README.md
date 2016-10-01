# simple-streaming
spark-streaming, elasticsearch, hbase, redis

d

# Kibana
## kibana  "No results found" 문제 해결
- elasticsearch 데이터를 조회(sense활용) 해보면 데이터는 존재함. (GET realtime/_search?q=*)
- 그런데 kibana 메인화면에서 indics를 추가하고, settings>advanced>metaFields에 "_timestamp"를 추가해도 
- "No results found"만 표시되고 데이터는 보이지 않음.
- [해결]
  * timestamp field를 추가해야 함... -_-;
```json
PUT realtime/_mapping/blacklist
{
      "_timestamp": { 
        "enabled": true
      }
} 
```
