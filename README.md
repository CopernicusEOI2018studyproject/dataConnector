# notgroupb dataConnector

Connector to ingest Data from different Data Portals into Kafka Ecosystem
Currently implemented Data Portals:
 - HYGON Data Portal: http://luadb.lds.nrw.de/LUA/hygon/pegel.php?rohdaten=ja
 - PegelOnline: https://www.pegelonline.wsv.de/gast/start

Data is parsed to custom Data Formats for each Portal. See [dataPreformatter](https://github.com/CopernicusEOI2018studyproject/dataPreformatter) for details.

## Getting Started

The Software is an Extension to the [kafka-connect-rest](https://github.com/llofberg/kafka-connect-rest) Library.

### Prerequisites
 - [Apache Maven](https://maven.apache.org/) for packaging.
 - [confluent](https://www.confluent.io/) or similar Service for running 
### Installing

```
mvn package
```

Then copy the Files to the specified plugin directory (Specified by `plugin.path` in `kafka-connect-distributed.properties` or `kafka-connect-standalone.properties`) or include them in the path directly. 

If you have installed confluent via `apt-get` on Debian the default plugin directory is `/usr/share/java`.

Whether the Connectors are sucessfully registered can be checked by Querying the kafka-rest API  at `http://{{Hostname}}:8083/connector-plugins`  and checking whether `org.notgroupb.dataConnector.PegelOnlineSourceConnector` is present. 

## Deployment

The connectors need to be started/registered individually via the kafka-rest Interface.

Hygon Connector (For Data Retrieval):
```bash
curl -X POST \
  http://localhost:8083/connectors \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -d '{
  "name": "HygonDataConnector",
  "config": {
    "connector.class": "com.tm.kafka.connect.rest.RestSourceConnector",
    "tasks.max": "1",
    "rest.source.poll.interval.ms": "500000",
    "rest.source.method": "GET",
    "rest.source.url": "http://luadb.it.nrw.de/LUA/hygon/messwerte/messwerte.tar.gz",
    "rest.source.payload.converter.class": "org.notgroupb.dataConnector.converter.HYGONPayloadConverter",
    "rest.source.topic.selector": "org.notgroupb.dataConnector.selector.HygonTopicSelector",
    "log4j.logger": "INFO"
  }
}'
```
Hygon Connector (For Stations Retrieval):
```bash
curl -X POST \
  http://localhost:8083/connectors \
  -H 'accept: application/json' \
  -H 'content-type: application/json' \
  -d '{
  "name": "HygonStationsConnector",
  "config": {
    "connector.class": "com.tm.kafka.connect.rest.RestSourceConnector",
    "tasks.max": "1",
    "rest.source.poll.interval.ms": "500000",
    "rest.source.method": "GET",
    "rest.source.url": "http://luadb.it.nrw.de/LUA/hygon/messwerte/pegeldaten.tar.gz",
    "rest.source.payload.converter.class": "org.notgroupb.dataConnector.converter.HYGONPayloadConverter",
    "rest.source.topic.selector": "org.notgroupb.dataConnector.selector.HygonTopicSelector",
    "log4j.logger": "INFO"
  }
}'
```
PegelOnline Connector:
```bash
curl -X POST \
  http://localhost:8083/connectors \
  -H 'content-type: application/json' \
  -d '{
  "name": "PegelOnlineConnector",
  "config": {
    "connector.class": "org.notgroupb.dataConnector.PegelOnlineSourceConnector",
    "tasks.max": "1",
    "rest.source.poll.interval.ms": "500000",
    "log4j.logger": "INFO"
  }
}'
```

The Connectors output to hardcoded Topics:
 - HygonDataConnector --> "HygonWLRaw"
 - HygonStationsConnector --> "HygonStationsWL"
 - PegelOnlineConnector --> "PegelOnlineStations" + "PegelOnlineDataSource"

For Data from Hygon Portal both Connectors need to be present.

## Versioning

We use [Semamtic Versioning](http://semver.org/) for versioning.

## Authors

* **Jan Speckamp** - *Initial work* - [speckij](https://github.com/speckij)

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for details
