curl -X POST \
  http://localhost:8083/connectors \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: e1dec892-3140-4187-1d7e-c2657499e3ab' \
  -d '{
  "name": "Hygon Stations Connector",
  "config": {
    "connector.class": "com.tm.kafka.connect.rest.RestSourceConnector",
    "tasks.max": "1",
    "rest.source.poll.interval.ms": "60000",
    "rest.source.method": "GET",
    "rest.source.url": "http://luadb.it.nrw.de/LUA/hygon/messwerte/pegeldaten.tar.gz",
    "rest.source.payload.converter.class": "com.tm.kafka.connect.rest.converter.StringPayloadConverter",
    "rest.source.properties": "",
    "rest.source.topic.selector": "com.tm.kafka.connect.rest.selector.SimpleTopicSelector",
    "rest.source.destination.topics": "HygonStationsWL",
    "log4j.logger": "INFO, kafkaAppender"
  }
}'

curl -X POST \
  http://localhost:8083/connectors \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: fe89491f-a96c-5366-8e71-4b05eab70a34' \
  -d '{
  "name": "Hygon Data Connector",
  "config": {
    "connector.class": "com.tm.kafka.connect.rest.RestSourceConnector",
    "tasks.max": "1",
    "rest.source.poll.interval.ms": "60000",
    "rest.source.method": "GET",
    "rest.source.url": "http://luadb.it.nrw.de/LUA/hygon/messwerte/messwerte.tar.gz",
    "rest.source.payload.converter.class": "org.notgroupb.dataConnector.converter.HYGONPayloadConverter",
    "rest.source.properties": "",
    "rest.source.topic.selector": "org.notgroupb.dataConnector.selector.HygonTopicSelector",
    "rest.source.destination.topics": "HygonRAW",
    "log4j.logger": "INFO, kafkaAppender"
  }
}'

curl -X POST \
  http://localhost:8083/connectors \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 75c74dd5-4ac0-58f0-2d24-1aa3fa0b35c4' \
  -d '{
  "name": "Hygon Stations Connector",
  "config": {
    "connector.class": "com.tm.kafka.connect.rest.RestSourceConnector",
    "tasks.max": "1",
    "rest.source.poll.interval.ms": "60000",
    "rest.source.method": "GET",
    "rest.source.url": "http://luadb.it.nrw.de/LUA/hygon/messwerte/pegeldaten.tar.gz",
    "rest.source.payload.converter.class": "com.tm.kafka.connect.rest.converter.StringPayloadConverter",
    "rest.source.properties": "",
    "rest.source.topic.selector": "com.tm.kafka.connect.rest.selector.SimpleTopicSelector",
    "rest.source.destination.topics": "HygonStationsWL",
    "log4j.logger": "INFO, kafkaAppender"
  }
}'
