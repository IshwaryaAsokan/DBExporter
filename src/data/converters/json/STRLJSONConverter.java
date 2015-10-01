package data.converters.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class STRLJSONConverter extends JSONConverter{
	public JSONArray convert(JSONObject originalJson){
		JSONArray transformedJson = new JSONArray();
		try {
			Iterator<String> iter = originalJson.keys();
			while(iter.hasNext()){
				String key = iter.next();
				JSONObject product = originalJson.getJSONObject(key);
				List<JSONObject> productSkus = readObjectSTRL(product);

				for(JSONObject sku : productSkus){
					transformedJson.put(sku);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return transformedJson;
	}
	
	private static List<JSONObject> readObjectSTRL(JSONObject json){
		List<JSONObject> products = new ArrayList<JSONObject>();
		
		try {
			String defaultCategory = getValue(json, "$.ATG_Default_Category");
			String shortDescription = getValue(json, "$.Description_Thumbnail");
			String model = getValue(json, "$.Item_No");
								
			List<JSONObject> skus = getArrayValue(json, "$.[skus]");
			for(JSONObject sku : skus){
				JSONObject product = new JSONObject();
				product.put("country", "US");
				product.put("manufacturerName", "Sterling");
				product = putNullStringIfNull(product, "model", model);
				product = putNullStringIfNull(product, "shortDescription", shortDescription);
				product = putNullStringIfNull(product, "category", defaultCategory);
				
				String skuStr = getValue(sku, "$.Item_No");
				String color = getValue(sku, "$.Color_Finish_Name");
				String upcCode = getValue(sku, "$.UPC_Code");

				String jpg = getValue(sku, "$.JPG_Item_Image");
				if(jpg != null){
					String jpgRoot = jpg.replace(".jpg", "");
					product = putIfNotNull(product, "imageURL", wrapInCData(wrapInUrl(jpgRoot)));
				}
				else {
					product = putNullStringIfNull(product, "imageURL", null);
				}
				
				product = putNullStringIfNull(product, "sku", skuStr);
				product = putNullStringIfNull(product, "upc", upcCode);					
				product = putNullStringIfNull(product, "color", color);
				product.put("productGroup", "US");
				product.put("action", "Add");
				
				products.add(product);	
			}
		} catch (JSONException e) {
			System.out.println("Error creating product.");
			e.printStackTrace();
		}		
		return products;
	}
	
	private static String wrapInCData(String val){
		return "<![CDATA[" + val + "]]>";
	}
	
	private static String wrapInUrl(String val){
		return "http://s7d4.scene7.com/is/image/Kohler/" + val + "?wid=2000";
	}

	@Override
	public String getStartXmlWrapper() {
		return "<?xml version=\"1.0\" encoding=\"utf-8\" ?><SterlingProducts><CountryCode=\"US\">";
	}

	@Override
	public String getEndXmlWrapper() {
		return "<SterlingProducts>";
	}

	@Override
	public List<Pair<String, String>> getReplacements() {
		List<Pair<String, String>> replacements = new ArrayList<Pair<String, String>>();
		replacements.add(Pair.of("<array>", "<Product>"));
		replacements.add(Pair.of("</array>", "</Product>"));
		replacements.add(Pair.of("&lt;", "<"));
		replacements.add(Pair.of("&gt;", ">"));
		return replacements;
	}
}
