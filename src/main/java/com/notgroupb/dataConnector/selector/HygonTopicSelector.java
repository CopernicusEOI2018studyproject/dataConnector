package com.notgroupb.dataConnector.selector;

import com.tm.kafka.connect.rest.RestSourceConnectorConfig;
import com.tm.kafka.connect.rest.selector.TopicSelector;

public class HygonTopicSelector implements TopicSelector {

	@Override
	public String getTopic(Object data) {
		// Cast Object to String
		String filename;
		try {
			filename = (String)data;
		} catch (Exception e) {
			return null;
		}
		
		// Switch Topic 
    	switch(filename) {
    		case "pegel_stationen.txt": {
    			return "HygonStationsWL";
    		}
    		case "messwerte.txt": {
    			return "HygonWLRaw";
    		}
    	};
		return null;
	}

	@Override
	public void start(RestSourceConnectorConfig config) {
		// Do nothing 

	}

}
