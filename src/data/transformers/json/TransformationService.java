package data.transformers.json;

import definitions.Business;

public class TransformationService {
	public static JSONTransformer getService(Business business){
		if(business == Business.STRL){
			return new StrlJSONTransformer();
		}
		
		return null;
	}
	
	public static String getRootValue(Business business){
		if(business == Business.STRL){
			return "Product";
		}
		
		return null;
	}
	
	public static String getStartXmlWrapper(Business business){
		if(business == Business.STRL){
			return "<?xml version=\"1.0\" encoding=\"utf-8\" ?><SterlingProducts><CountryCode=\"US\">";
		}
		
		return "";
	}
	
	public static String getEndXmlWrapper(Business business){
		if(business == Business.STRL){
			return "<SterlingProducts>";
		}
		
		return "";
	}
}
