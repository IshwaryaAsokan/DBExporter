package data.transformers.json;

import definitions.Business;

public class TransformationService {
	public static JSONTransformer getService(Business business){
		if(business == Business.STRL){
			return new StrlJSONTransformer();
		}
		
		return null;
	}
}
