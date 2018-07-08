
package org.notgroupb.dataConnector;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

public class PegelOnlineSourceConnectorConfig extends AbstractConfig {

	private static final String SOURCE_POLL_INTERVAL_CONFIG = "rest.source.poll.interval.ms";
	private static final String SOURCE_POLL_INTERVAL_DOC = "How often to poll the source URL.";
	private static final String SOURCE_POLL_INTERVAL_DISPLAY = "Polling interval";
	private static final Long SOURCE_POLL_INTERVAL_DEFAULT = 60000L;

	private PegelOnlineSourceConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
		super(config, parsedConfig);
	}

	public PegelOnlineSourceConnectorConfig(Map<String, String> parsedConfig) {
		this(conf(), parsedConfig);
	}

	static ConfigDef conf() {	
		String group = "REST";
		int orderInGroup = 0;
		return new ConfigDef()
				.define(SOURCE_POLL_INTERVAL_CONFIG,
						Type.LONG,
						SOURCE_POLL_INTERVAL_DEFAULT,
						Importance.LOW,
						SOURCE_POLL_INTERVAL_DOC,
						group,
						++orderInGroup,
						ConfigDef.Width.SHORT,
						SOURCE_POLL_INTERVAL_DISPLAY);
	}

	Long getPollInterval() {
		return this.getLong(SOURCE_POLL_INTERVAL_CONFIG);
	}

	private static ConfigDef getConfig() {
		Map<String, ConfigDef.ConfigKey> everything = new HashMap<>(conf().configKeys());
		ConfigDef visible = new ConfigDef();
		for (ConfigDef.ConfigKey key : everything.values()) {
			visible.define(key);
		}
		return visible;
	}

	public static void main(String[] args) {
		System.out.println(getConfig().toEnrichedRst());
	}
}
