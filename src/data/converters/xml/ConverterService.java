package data.converters.xml;

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
}
