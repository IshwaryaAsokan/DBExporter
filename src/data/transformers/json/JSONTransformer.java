package data.transformers.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JSONTransformer {
	public static JSONArray transformSTRL(JSONObject originalJson){
		JSONArray transformedJson = new JSONArray();
		try {
			Iterator<String> iter = originalJson.keys();
			while(iter.hasNext()){
				String key = iter.next();
				JSONObject product = originalJson.getJSONObject(key);
				List<JSONObject> productSkus = readObject(product);

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
	
	private static List<JSONObject> readObject(JSONObject json){
		List<JSONObject> products = new ArrayList<JSONObject>();
		
		try {
			String defaultCategory = getValue(json, "$.ATG_Default_Category");
			String shortDescription = getValue(json, "$.Description_Thumbnail");
			String brand = getValue(json, "$.keywords[0].Brand_Name");
			String model = getValue(json, "$.Item_No");
			
			JSONObject product = new JSONObject();
			product.put("country", "USA");
			product.put("manufacturerName", "Sterling");
			product.put("model", model);
			product.put("productName", brand);
			product.put("shortDescription", shortDescription);
			product.put("category", defaultCategory);
			products.add(product);
					
//			List<String> skus = getArrayValue(json, "$.[skus]");
//			for(String skuJson : skus){
//				JSONObject sku = new JSONObject(skuJson);
//				String skuStr = getValue(sku, "$.Item_No");
//				String color = getValue(sku, "$.Color_Finish_Name");
//				String upcCode = getValue(sku, "$.UPC_Code");
//				String jpg = getValue(sku, "$.JPG_Item_Image");
//				String jpgImgLocation = "http://s7d4.scene7.com/is/image/Kohler/" + jpg + "?$SterlingMain$";				
//	
//					product.put("sku", skuStr);
//					product.put("upc", upcCode);
//					product.put("imageURL", jpgImgLocation);
//					product.put("productGroup", "US");
//					product.put("action", "Add");
//					product.put("color", color);
//					products.add(product.toString());
//	
//			}
		} catch (JSONException e) {
			System.out.println("Error creating product.");
			e.printStackTrace();
		}		
		return products;
	}
	
	private static String getValue(JSONObject json, String path){
		try {
			String retVal = JsonPath.read(json.toString(), path);
			return retVal;
		}
		catch(PathNotFoundException e){
			//for now, do nothing
		}
		return null;
	}
	
	private static List<String> getArrayValue(JSONObject json, String path){
		try {
			List<String> retVal = JsonPath.read(json.toString(), path);
			return retVal;
		}
		catch(PathNotFoundException e){
			//for now, do nothing
		}
		return null;
	}
}
