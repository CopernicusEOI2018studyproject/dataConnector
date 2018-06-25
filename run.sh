curl -X POST \
  http://localhost:8083/connectors \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 2d2e4ee9-18fe-4bcf-c99d-6cb9559d6bf5' \
  -d '{
  "name": "PegelOnline Stations Connector",
  "config": {
    "connector.class": "com.tm.kafka.connect.rest.RestSourceConnector",
    "tasks.max": "1",
    "rest.source.poll.interval.ms": "60000",
    "rest.source.method": "GET",
    "rest.source.url": "https://www.pegelonline.wsv.de/webservices/rest-api/v2/stations.json?includeTimeseries=true&includeCurrentMeasurement=true",
    "rest.source.payload.converter.class": "org.notgroupb.dataConnector.converter.HYGONPayloadConverter",
    "rest.source.properties": "Content-Type:application/json,Accept::application/json",
    "rest.source.topic.selector": "org.notgroupb.dataConnector.selector.HygonTopicSelector",
    "rest.source.destination.topics": "PegelOnlineRAW",
    "log4j.logger": "TRACE, kafkaAppender"
  }
}'

curl -X POST \
  http://localhost:8083/connectors \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 743cf29a-afd7-f2cd-f4d7-125fa6d9e1ce' \
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
  -H 'postman-token: b57861b2-b3f1-4071-e684-5007033a020c' \
  -d '{
  "name": "Hygon Stations Connector",
  "config": {
    "connector.class": "com.tm.kafka.connect.rest.RestSourceConnector",
    "tasks.max": "1",
    "rest.source.poll.interval.ms": "60000",
    "rest.source.method": "GET",
    "rest.source.url": "http://luadb.it.nrw.de/LUA/hygon/messwerte/pegeldaten.tar.gz",
    "rest.source.payload.converter.class": "org.tm.kafka.connect.rest.converter.HYGONPayloadConverter",
    "rest.source.properties": "",
    "rest.source.topic.selector": "org.tm.kafka.connect.rest.selector.HygonTopicSelector",
    "rest.source.destination.topics": "HygonStationsWL",
    "log4j.logger": "INFO, kafkaAppender"
  }
}'
