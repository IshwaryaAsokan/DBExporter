package data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ObjectBuilder {
	//takes in ResultSets and builds the objects associated with those results
	public static JSONObject buildItemInfo(ResultSet rs){
		JSONObject allItems = new JSONObject();
		
		try {
			String currentItem = "";
			JSONObject currentItemObj = null;
			
			while(rs != null && rs.next()){
				String itemNo = rs.getString("ITEM_NO");
				String attrType = rs.getString("ATTRIBUTE_TYPE");
				String value = rs.getString("VALUE");
				
				if(!currentItem.equalsIgnoreCase(itemNo)){
					//add previous object to output
					if(currentItemObj != null){
						String key = (String) currentItemObj.get("Item_No");
						allItems.put(key, currentItemObj);
					}					
					
					//reset current to point to new item_no
					currentItem = itemNo;
					currentItemObj = new JSONObject();
					currentItemObj.put("Item_No", itemNo);
				}
				
				currentItemObj.put(attrType, value);
			}			
		} catch (SQLException e) {
			System.out.println("Error building SQL into data objects.");
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Issue with JSON.");
			e.printStackTrace();
		}
		
		return allItems;
	}
	
	//takes in parent/child mappings and outputs a map where key=item, value=product
	public static Map<String,String> mapItemsToProducts(ResultSet rs){
		Map<String,String> mappings = new HashMap<String,String>();
		
		try {
			while(rs != null && rs.next()){
				String product = rs.getString("PARENT");
				String item = rs.getString("CHILD");
				mappings.put(item, product);
			}
		} catch (SQLException e) {
			System.out.println("Error building item -> product mapping.");
			e.printStackTrace();
		}
		
		return mappings;
	}
	
	//to do: generalize so that children can be added easily
	public static JSONObject buildProducts(JSONObject products, Map<String,String> mappings, JSONObject items){
		Iterator<String> itemKeys = items.keys();
		Map<String,JSONArray> productItems = new HashMap<String,JSONArray>(); //to hold a list of all items for this product
		
		try {
			//iterate through all items, adding them to the array of items that belong to their product
			while(itemKeys.hasNext()){
				String itemKey = itemKeys.next(); //an item_no
				String productKey = mappings.get(itemKey); //a product item_no
				
				JSONArray currentProductItems = productItems.get(productKey);
				if(currentProductItems == null){
					currentProductItems = new JSONArray();
				}
				
				currentProductItems.put(items.get(itemKey));
				productItems.put(productKey, currentProductItems);
			}
			
			//iterate through all lists of items, associating them with their products
			Set<String> productItemNumbers = productItems.keySet();
			for(String productItemNo : productItemNumbers){
				JSONObject productInfo = (JSONObject) products.get(productItemNo);
				productInfo.put("skus", productItems.get(productItemNo));
				products.put(productItemNo, productInfo);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return products;
	}
}
