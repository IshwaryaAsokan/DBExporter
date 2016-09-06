package data.converters.xml;

import definitions.enums.Business;
import definitions.enums.BusinessPurpose;

public class ConverterService {
	public static JSONConverter getService(Business business, BusinessPurpose purpose){
		if(business == Business.STRL && purpose == BusinessPurpose.PRICE_SPIDER){
			return new STRLJSONConverter();
		}
		else if(business == Business.KPNA && purpose == BusinessPurpose.GOOGLE_XML_SHOPPING){
			return new KPNAJSONConverter();
		}
		else if(business == Business.KPNA && purpose == BusinessPurpose.OLAPIC_XML){
			return new OlapicConverter();
		}

		System.out.println("No converter found for business " + business.toString() + ".");
		
		return null;
	}
}
