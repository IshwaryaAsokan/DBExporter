package data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectBuilder {
	//takes in ResultSets and builds the objects associated with those results
	public static JSONObject buildItemInfo(ResultSet rs){
		JSONObject allItems = new JSONObject();
		
		try {
			String currentItem = "";
			JSONObject currentItemObj = null;
			
			while(rs != null && rs.next()){
				String itemNo = rs.getString("ITEM_NO");
				String attrType = rs.getString("TYPE");
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
	
	public static JSONObject buildProducts(JSONObject products, Map<String,String> mappings, List<Pair<String,JSONObject>> children){
		try{
			for(Pair<String,JSONObject> child : children){
				String childLabel = child.getLeft();
				JSONObject childInfo = child.getRight();
				Iterator<String> keys = childInfo.keys();
				
				while(keys.hasNext()){
					String childItemNo = keys.next();
					String parentItemNo = mappings.get(childItemNo);
					
					if(products.has(parentItemNo)){
						JSONObject parentJson = products.getJSONObject(parentItemNo);
						
						if(parentJson.has(childLabel)){
							JSONArray siblings = parentJson.getJSONArray(childLabel);
							siblings.put(childInfo.get(childItemNo));
							parentJson.put(childLabel, siblings);
						}
						else {
							JSONArray childItems = new JSONArray();
							childItems.put(childInfo.get(childItemNo));
							parentJson.put(childLabel, childItems);
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return products;		
	}
}
