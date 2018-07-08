package org.notgroupb.dataConnector.formatting;

import org.json.JSONArray;
import org.json.JSONObject;
import org.notgroupb.formats.PegelOnlineDataPoint;

public class PegelOnlineFormatter {

	public PegelOnlineDataPoint format(JSONObject element) {
		PegelOnlineDataPoint dp = new PegelOnlineDataPoint();
		String key = element.getString("longname");
		
		// Get Water Level Timeseries
		JSONObject waterleveltimeseries = null;
		JSONArray timeseries = element.getJSONArray("timeseries");
		for (int j = 0; j < timeseries.length(); j++) {
			String wlt = (String) timeseries.getJSONObject(j).get("shortname");
			if (wlt.equals("W")) {
				waterleveltimeseries = timeseries.getJSONObject(j);
				break;
			}
		}
		// Discard Datapoint if no water level is measured
		if (waterleveltimeseries == null) {
			return null;
		}
		dp.setName(key);

		// Get Characteristic Values
		JSONArray characteristicValues = waterleveltimeseries.getJSONArray("characteristicValues");
		for (int j = 0; j < characteristicValues.length(); j++) {
			JSONObject obj = characteristicValues.getJSONObject(j);
			switch((String)obj.get("shortname")) {
				case "HHW": {
					dp.setHhw((double) obj.get("value"));
					break;
				}
				case "MHW": {
					dp.setMhw((double) obj.get("value"));
					break;
				}
				case "MW": {
					dp.setMw((double) obj.get("value"));
					break;
				}
				case "MNW": {
					dp.setMnw((double) obj.get("value"));
					break;
				}
			}
		}
			
		// Get coordinates
		dp.setLon(element.getLong("longitude"));
		dp.setLat(element.getLong("latitude"));
		return dp;
	}
}
