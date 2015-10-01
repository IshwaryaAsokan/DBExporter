package data.converters.json;

import definitions.enums.Business;

public class ConverterService {
	public static JSONConverter getService(Business business){
		if(business == Business.STRL){
			return new STRLJSONConverter();
		}
		else if(business == Business.KPNA){
			return new KPNAJSONConverter();
		}
		
		return null;
	}
	
	public static String getRootValue(Business business){
		if(business == Business.STRL){
			return "Product";
		}
		else if(business == Business.KPNA){
			return "item";
		}
		
		return null;
	}
	
	public static String getStartXmlWrapper(Business business){
		if(business == Business.STRL){
			return "<?xml version=\"1.0\" encoding=\"utf-8\" ?><SterlingProducts><CountryCode=\"US\">";
		}
		else if(business == Business.KPNA){
			return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\" xmlns:g=\"http://base.google.com/ns/1.0\"><channel>";
		}
		
		return "";
	}
	
	public static String getEndXmlWrapper(Business business){
		if(business == Business.STRL){
			return "<SterlingProducts>";
		}
		else if(business == Business.KPNA){
			return "</channel></rss>";
		}
		
		return "";
	}
}
