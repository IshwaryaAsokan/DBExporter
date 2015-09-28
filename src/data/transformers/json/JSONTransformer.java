package data.transformers.json;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public abstract class JSONTransformer {
	//this method must be implemented on a per LOB basis
	public abstract JSONArray transform(JSONObject originalJson);
	
	static String getValue(JSONObject json, String path){
		try {
			String retVal = JsonPath.read(json.toString(), path);
			return retVal;
		}
		catch(PathNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}
	
	static List<JSONObject> getArrayValue(JSONObject json, String path){
		try {
			List<JSONObject> retVal = new ArrayList<JSONObject>();
			ArrayList<Object> valArr = JsonPath.read(json.toString(), path);
			
			if(valArr != null){
				for(Object val : valArr){
					JSONObject valObj = new JSONObject(val.toString());
					retVal.add(valObj);
				}				
			}

			return retVal;
		}
		catch(PathNotFoundException | JSONException e){
			e.printStackTrace();
		}
		return null;
	}
	
	static JSONObject putIfNotNull(JSONObject obj, String key, String value){
		if(value != null){
			try {
				obj.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}
}
