package com.notgroupb.dataConnector.converter;

import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.rauschig.jarchivelib.ArchiveEntry;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiveStream;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tm.kafka.connect.rest.RestSinkConnectorConfig;
import com.tm.kafka.connect.rest.RestSourceConnectorConfig;
import com.tm.kafka.connect.rest.converter.StringPayloadConverter;
import com.tm.kafka.connect.rest.selector.TopicSelector;

public class HYGONPayloadConverter extends StringPayloadConverter {
  
  private String url;
  private TopicSelector topicSelector;

  public List<SourceRecord> convert(byte[] bytes) {
	
    ArrayList<SourceRecord> records = new ArrayList<>();
    try {
    	File inputArchive = File.createTempFile("HYGON", "tmp");
		FileUtils.writeByteArrayToFile(inputArchive, bytes);
		
	    Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
	    ArchiveStream stream = archiver.stream(inputArchive);
	    ArchiveEntry entry;

	    while((entry = stream.getNextEntry()) != null) {
	    	File textfile = entry.extract(new File("HYGON_unpacked" + entry.getName()));
    		String topic = topicSelector.getTopic(entry.getName());
    		
	    	// Stop processing if there is no fitting topic
	    	if (topic == null) {
	    		break;
	    	}
	    	
	        List<String> rawdata = Files.readAllLines(textfile.toPath(), Charset.forName("ISO-8859-1"));
	    	
	    	for (String item : rawdata) {
    			SourceRecord sourceRecord = new SourceRecord(
					Collections.singletonMap("URL", url),
        			Collections.singletonMap("timestamp", currentTimeMillis()),
        			topic,
        			Schema.STRING_SCHEMA,
        			item
				);
	        	records.add(sourceRecord);
	    	}
	    	textfile.delete();
	    }
	    stream.close();
	    inputArchive.delete();
	} catch (IOException e) {
		e.printStackTrace();
		return null;
	}
    return records;
  }

  @Override
  public void start(RestSourceConnectorConfig config) {
    url = config.getUrl();
    topicSelector = config.getTopicSelector();
    topicSelector.start(config);
  }

  @Override
  public void start(RestSinkConnectorConfig config) {
    url = config.getUrl();
  }
}
