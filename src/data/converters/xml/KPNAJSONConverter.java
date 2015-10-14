package data.converters.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

public class KPNAJSONConverter extends JSONConverter {

	@Override
	public JSONArray convert(JSONObject originalJson) {
		// TODO Populate this the next time we get an updated request from the data team
		return null;
	}

	@Override
	public String getStartXmlWrapper() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\" xmlns:g=\"http://base.google.com/ns/1.0\"><channel>";
	}

	@Override
	public String getEndXmlWrapper() {
		return "</channel></rss>";
	}

	@Override
	public List<Pair<String, String>> getReplacements() {
		List<Pair<String, String>> replacements = new ArrayList<Pair<String, String>>();
		replacements.add(Pair.of("<array>", "<item>"));
		replacements.add(Pair.of("</array>", "</item>"));
		replacements.add(Pair.of("&lt;", "<"));
		replacements.add(Pair.of("&gt;", ">"));
		return replacements;
	}

}
