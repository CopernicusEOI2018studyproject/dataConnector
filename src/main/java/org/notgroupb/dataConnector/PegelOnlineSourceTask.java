package org.notgroupb.dataConnector;


import static java.lang.System.currentTimeMillis;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.notgroupb.dataConnector.formatting.PegelOnlineFormatter;
import org.notgroupb.formats.PegelOnlineDataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PegelOnlineSourceTask extends SourceTask {
  private static Logger log = LoggerFactory.getLogger(PegelOnlineSourceTask.class);

  private Long pollInterval;
  private String method;
  private String url;
  private String data;
  
  private String requestURL = "https://www.pegelonline.wsv.de/webservices/rest-api/v2/stations";
  private List<String> endpoints;
  private Producer<String, PegelOnlineDataPoint> producer;

  private Long lastPollTime = 0L;

  @Override
  public void start(Map<String, String> map) {
	PegelOnlineSourceConnectorConfig connectorConfig = new PegelOnlineSourceConnectorConfig(map);
    pollInterval = connectorConfig.getPollInterval();
    method = "GET";
    url = requestURL;
    
    // Get initial Stations List
    producer = new KafkaProducer<String, PegelOnlineDataPoint>(producerProperties());
    endpoints = new ArrayList<>();
    
    HttpURLConnection conn;
	try {
		conn = (HttpURLConnection) new URL(url + ".json?includeTimeseries=true&includeCharacteristicValues=true").openConnection();
	    conn.setRequestMethod(method);
	    if (data != null) {
	      conn.setDoOutput(true);
	      OutputStream os = conn.getOutputStream();
	      os.write(data.getBytes());
	      os.flush();
	    }
	    if (log.isTraceEnabled()) {
	      log.trace("Response code: {}, Request data: {}", conn.getResponseCode(), data);
	    }
		String response = new String(IOUtils.toByteArray(conn.getInputStream()));
		this.parseStations(response);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  private Properties producerProperties() {
	Properties props = new Properties();
	props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			"org.notgroupb.dataPreformatter.formats.DataPointSerializer");
	props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			"org.apache.kafka.common.serialization.StringSerializer");
	props.put(ProducerConfig.RETRIES_CONFIG, 0);
	return props;
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
	  long millis = pollInterval - (System.currentTimeMillis() - lastPollTime);
	  if (millis > 0) {
		  Thread.sleep(millis);
	  }
	  List<SourceRecord> records = new ArrayList<>();

	  for (String element : endpoints) {
		  try {
			  String url = requestURL + element + "/W/measurements.json?start=P1D";
			  HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			  conn.setRequestMethod(method);
			  if (data != null) {
				  conn.setDoOutput(true);
				  OutputStream os = conn.getOutputStream();
				  os.write(data.getBytes());
				  os.flush();
			  }
			  if (log.isTraceEnabled()) {
				  log.trace("Response code: {}, Request data: {}", conn.getResponseCode(), data);
			  }
			  Map<String, String> sourcePartition = Collections.singletonMap("URL", "PegelOnlineDataConnector");
			  Map<String, Long> sourceOffset = Collections.singletonMap("timestamp", currentTimeMillis());
			  SourceRecord sourceRecord = new SourceRecord(sourcePartition, sourceOffset, "PegelOnlineData",
					  Schema.STRING_SCHEMA, element, Schema.STRING_SCHEMA, IOUtils.toByteArray(conn.getInputStream()));

			  if (log.isTraceEnabled()) {
				  log.trace("SourceRecord: {}", sourceRecord);
			  }
			  records.add(sourceRecord);
		  } catch (Exception e) {
			  log.error("REST source connector poll() failed", e);
			  continue;
		  }
	  }
	  lastPollTime = System.currentTimeMillis();
	  return records;
  }

  @Override
  public void stop() {
    log.debug("Stopping source task");
  }

  @Override
  public String version() {
	  return this.getClass().getPackage().getImplementationVersion();
  }
  
  private void parseStations(String v) {
	  	String topicname = "PegelOnlineStations";
	  	JSONArray allStations;
		PegelOnlineFormatter formatter = new PegelOnlineFormatter();

		// Remove noise & Bit hacking cause it somehow gets broken in the process
		// v = StringUtilstrimAllWhitespace(v);
		// v = StringUtils.deleteAny(v, "\n").substring(7);

		// Convert to JSON Object and extract Data into custom Format
		try {
			if(v.charAt(0) != '[') {
				v = v.substring(1);
			}
			allStations = new JSONArray(v);
			for (int i = 0; i < allStations.length();i++) {
				PegelOnlineDataPoint dp = formatter.format(allStations.getJSONObject(i));
				if (dp == null) {
					continue;
				}
				String key = allStations.getJSONObject(i).getString("longname");
				
				ProducerRecord<String, PegelOnlineDataPoint> result = new ProducerRecord<>(topicname,key,dp);
				producer.send(result);
				
				// Add Station to be polled frequently
				endpoints.add(dp.getName());
			}
		} catch (Exception e) {
			log.error("Error parsing response to JSON. Error was: " + e + e.getMessage());
		}
  	}
}
