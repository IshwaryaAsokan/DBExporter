package data.converters.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
			String brand = getValue(json, "$.keywords[0].Brand_Name");
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
					String jpgImgLocation = "http://s7d4.scene7.com/is/image/Kohler/" + jpgRoot + "?$SterlingMain$";
					product = putIfNotNull(product, "imageURL", jpgImgLocation);
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
}
