package org.notgroupb.dataConnector;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PegelOnlineSourceConnector extends SourceConnector {
	private static Logger log = LoggerFactory.getLogger(PegelOnlineSourceConnector.class);
	private PegelOnlineSourceConnectorConfig config;

	@Override
	public String version() {
		try {
			return this.getClass().getPackage().getImplementationVersion();
		} catch (Exception ex) {
			return "0.0.0.0";
		}
	}

	@Override
	public void start(Map<String, String> map) {
		config = new PegelOnlineSourceConnectorConfig(map);
	}

	@Override
	public Class<? extends Task> taskClass() {
		return PegelOnlineSourceTask.class;
	}

	@Override
	public List<Map<String, String>> taskConfigs(int maxTasks) {
		Map<String, String> taskProps = new HashMap<>(config.originalsStrings());
		List<Map<String, String>> taskConfigs = new ArrayList<>(maxTasks);
		for (int i = 0; i < maxTasks; ++i) {
			taskConfigs.add(taskProps);
		}
		return taskConfigs;
	}

	@Override
	public void stop() {
	}

	@Override
	public ConfigDef config() {
		return PegelOnlineSourceConnectorConfig.conf();
	}
}
