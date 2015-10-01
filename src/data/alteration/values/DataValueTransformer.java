package data.alteration.values;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import definitions.enums.JsonTransformationType;

public class DataValueTransformer {
	private static JSONObject transform(JSONObject obj, Transformation transformation){
		if(obj.has(transformation.getKey())){
			try {
				if(transformation.getTransformation() == JsonTransformationType.NEW_KEY){					
					Object attrValue = obj.get(transformation.getKey());
					obj = obj.put(transformation.getValue(), attrValue);
					obj.remove(transformation.getKey());
				}
				else if(transformation.getTransformation() == JsonTransformationType.APPEND){
					String currentVal = obj.getString(transformation.getKey());
					obj = obj.put(transformation.getKey(), currentVal + transformation.getValue());
				}
				else { //transformation == DataValueTransformationType.PREPEND
					String currentVal = obj.getString(transformation.getKey());
					obj = obj.put(transformation.getKey(), transformation.getValue() + currentVal);
				}		
			} catch (JSONException e) {
				System.out.println("Error attempting to transform JSON values.");
				e.printStackTrace();
			}
		}
		
		return obj;
	}
	
	public static JSONObject transform(JSONObject rootObj, List<Transformation> transformations){
		try {		
			Iterator<String> iter = rootObj.keys();
			
			while(iter.hasNext()){
				String key = iter.next();
				JSONObject obj = rootObj.getJSONObject(key);;
				
				for(Transformation transformation : transformations){
					obj = transform(obj, transformation);
				}
				
				rootObj.put(key, obj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rootObj;
	}
}
