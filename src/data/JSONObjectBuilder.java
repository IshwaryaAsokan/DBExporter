package data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectBuilder {
	//takes in ResultSets and builds the objects associated with those results
	public static JSONObject buildItemInfo(ResultSet rs){		
		JSONObject allData = new JSONObject();
		
		try {
			while(rs != null && rs.next()){
				String itemNo = rs.getString("ITEM_NO");
				String attrType = rs.getString("TYPE");
				String value = rs.getString("VALUE");
										
				if(allData.has(itemNo)){
					JSONObject entry = allData.getJSONObject(itemNo);
					if(entry.has(attrType) && entry.get(attrType) != null){
						String newVal = entry.get(attrType).toString() + "|" + value;
						entry.put(attrType, newVal);
						allData.put(itemNo, entry);
					}
					else {
						entry.put(attrType, value);
						//checkForUnicodeBlockCharacters(itemNo, attrType, value);
						allData.put(itemNo, entry);
					}
				}
				else {
					JSONObject newData = new JSONObject();
					newData.put(attrType, value);
					newData.put("Item_No", itemNo);
					//checkForUnicodeBlockCharacters(itemNo, attrType, value);
					allData.put(itemNo, newData);
				}
			}			
		} catch (SQLException e) {
			System.out.println("Error building SQL into data objects.");
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Issue with JSON.");
			e.printStackTrace();
		}
		
		return allData;
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
	
	public static JSONObject buildProducts(JSONObject products, Map<String,String> mappings, List<Triple<String,JSONObject,Boolean>> children){
		try{
			for(Triple<String,JSONObject,Boolean> child : children){
				String childLabel = child.getLeft();
				JSONObject childInfo = child.getMiddle();
				Boolean mapToParent = child.getRight();
				Iterator<String> keys = childInfo.keys();
				
				while(keys.hasNext()){
					String childItemNo = keys.next();
					String attachInfoTo = mapToParent ? mappings.get(childItemNo) : childItemNo;
					
					if(products.has(attachInfoTo) && childInfo.has(childItemNo)){
						if(!mapToParent){
							JSONObject parentJson = products.getJSONObject(attachInfoTo);
							parentJson.put(childLabel, childInfo.get(childItemNo));
						}
						else { //skus
							JSONObject parentJson = products.getJSONObject(attachInfoTo);
							JSONArray childArr = parentJson.has(childLabel) ? parentJson.getJSONArray(childLabel) : new JSONArray();							
							childArr.put(childInfo.get(childItemNo));
							parentJson.put(childLabel, childArr);
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
	
	public static JSONObject subsumeAttributes(JSONObject obj, String attribute){
		try {
			Iterator<String> keys = obj.keys();
			while(keys.hasNext()){
				String key = keys.next();
				if(obj.has(key)){
					JSONObject childObj = obj.getJSONObject(key);
					
					if(childObj.has(attribute)){
						JSONObject attributeObj = childObj.getJSONObject(attribute);
						Iterator<String> attributeKeys = attributeObj.keys();
						while(attributeKeys.hasNext()){
							String attributeKey = attributeKeys.next();
							if(attributeObj.has(attributeKey)){
								String attributeValue = attributeObj.getString(attributeKey);
								if(!childObj.has(attributeKey)){
									childObj.put(attributeKey, attributeValue);
								}
								else{
									System.out.println("Conflicting attribute name [subsumeAttributes] " + attributeKey + " - obj: " + childObj);
								}
							}
						}
					} //end: childObj.has(attribute)
					obj.put(key, childObj);
				}				
			}
		} catch (JSONException e) {
			System.out.println("Error attempting to subsume attribute [subsumeAttributes]");
			e.printStackTrace();
		}		
		
		return obj;
	}
	
	//a debugging aid
	private static void checkForUnicodeBlockCharacters(String itemNo, String type, String value){
		for(int index = 0; index < value.length(); index++){
			if(Character.UnicodeBlock.of(value.charAt(index))!=Character.UnicodeBlock.BASIC_LATIN){
				System.out.println("Illegal character in " + value + " - item: " + itemNo + ", attribute: " + type);
			}
		}
	}
}
