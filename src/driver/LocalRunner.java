package driver;

import io.CouchWriter;
import io.ExcelOutputData;
import io.OutputWriter;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import data.converters.xml.ConverterService;
import data.converters.xml.JSONConverter;
import data.services.ExcelDataService;
import data.services.UnalteredJSONService;
import definitions.enums.Business;
import definitions.enums.BusinessPurpose;
import definitions.enums.OutputFormat;

public class LocalRunner {
	public static void main(String args[]){
		//You can update the business used here to run the program locally.
		runBuilder(Business.KPNA, OutputFormat.COUCHDB, "KPNA_full");
	}
	
	private static void runBuilder(Business business, OutputFormat format){
		runBuilder(business, format, null, StringUtils.EMPTY);
	}
	
	private static void runBuilder(Business business, OutputFormat format, String sqlRoot){
		runBuilder(business, format, null, sqlRoot);
	}
	
	private static void runBuilder(Business business, OutputFormat format, BusinessPurpose purpose, String sqlRoot){
		Date now = new Date();
		System.out.println("Start: " + now.toString());
		
		UnalteredJSONService getDataService = new UnalteredJSONService(business, sqlRoot);
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
			ExcelDataService excelDataService = new ExcelDataService(business, format, sqlRoot);
			
			excelDataService.populateAttributeTypesLists();			
			excelDataService.populateHeaders();
			List<ExcelOutputData> excelOutput = excelDataService.getExcelOutputFormat();						

			OutputWriter.writeResult(populatedProductsJson, excelOutput, business, format);
			excelDataService.closeConnection();
		}

		now = new Date();
		System.out.println("End: " + now.toString());
		System.out.println("Done!");
	}
}
