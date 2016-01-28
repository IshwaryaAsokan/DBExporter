package driver;

import io.CouchWriter;
import io.ExcelOutputData;
import io.OutputWriter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import data.converters.xml.ConverterService;
import data.converters.xml.JSONConverter;
import data.services.ExcelDataService;
import data.services.UnalteredJSONService;
import definitions.enums.Business;
import definitions.enums.BusinessPurpose;
import definitions.enums.OutputFormat;

public class Driver {
	public static void main(String args[]){
		runBuilder(Business.KPNA, OutputFormat.JSON);
//		CouchWriter writer = new CouchWriter(Business.KPNA);
//		writer.writeToCouch();
	}
	
	private static void runBuilder(Business business, OutputFormat format){
		runBuilder(business, format, null);
	}
	
	private static void runBuilder(Business business, OutputFormat format, BusinessPurpose purpose){
		UnalteredJSONService getDataService = new UnalteredJSONService(business);
		JSONObject populatedProductsJson = getDataService.getPopulatedJSON();
						
		if(format == OutputFormat.JSON || format == OutputFormat.XML || format == OutputFormat.COUCHDB){
			if(format == OutputFormat.JSON){
				//changeDataService.applyDataTransformations();
				OutputWriter.writeResult(populatedProductsJson, business, format);
			}
			else if(format == OutputFormat.COUCHDB){
				CouchWriter writer = new CouchWriter(business);
				writer.writeToCouch(populatedProductsJson);
			}
			else { //format == OutputFormat.XML
				JSONConverter transformer = ConverterService.getService(business, purpose);
				JSONArray skus = transformer.convert(populatedProductsJson);
				OutputWriter.writeResult(skus, business, format, purpose);
			}
			
		}
		else{ //xls
			ExcelDataService excelDataService = new ExcelDataService(business, format);
			
			excelDataService.populateAttributeTypesLists();			
			excelDataService.populateHeaders();
			List<ExcelOutputData> excelOutput = excelDataService.getExcelOutputFormat();						

			OutputWriter.writeResult(populatedProductsJson, excelOutput, business, format);
			excelDataService.closeConnection();
		}

		System.out.println("Done!");
	}
}
