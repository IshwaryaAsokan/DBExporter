package driver;

import io.CouchWriter;
import io.ExcelOutputData;
import io.OutputWriter;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;

import data.converters.xml.ConverterService;
import data.converters.xml.JSONConverter;
import data.services.ExcelDataService;
import data.services.UnalteredJSONService;
import definitions.enums.Business;
import definitions.enums.OutputFormat;

public class Driver {
	public static void main(String args[]){
		runBuilder(Business.ANNS, OutputFormat.XLSX);
	}
	
	private static void runBuilder(Business business, OutputFormat format){
		UnalteredJSONService getDataService = new UnalteredJSONService(business);
		JSONObject populatedProductsJson = getDataService.getPopulatedJSON();
						
		if(format == OutputFormat.JSON || format == OutputFormat.XML || format == OutputFormat.COUCHDB){
			if(format == OutputFormat.JSON){
				//changeDataService.applyDataTransformations();
				OutputWriter.writeResult(populatedProductsJson, business, format);
			}
			else if(format == OutputFormat.COUCHDB){
				CouchWriter writer = new CouchWriter();
				writer.writeToCouch(populatedProductsJson);
			}
			else { //format == OutputFormat.XML
				JSONConverter transformer = ConverterService.getService(business);
				JSONArray skus = transformer.convert(populatedProductsJson);
				OutputWriter.writeResult(skus, business, format);
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
