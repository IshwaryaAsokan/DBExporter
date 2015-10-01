package driver;

import io.CouchWriter;
import io.ExcelOutputData;
import io.OutputWriter;
import java.util.List;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import data.JSONObjectBuilder;
import data.converters.json.JSONConverter;
import data.converters.json.ConverterService;
import definitions.enums.Business;
import definitions.enums.OutputFormat;

public class Driver {
	public static void main(String args[]){
		runBuilder(Business.STRL, OutputFormat.XML);
	}
	
	private static void runBuilder(Business business, OutputFormat format){
		RunData runData = new RunData(business, format);
		System.out.println("Connection made");
		runData.populateJson();
		System.out.println("JSON populated");

		if(format == OutputFormat.JSON || format == OutputFormat.XML || format == OutputFormat.COUCHDB){
			runData.populateMappings();
			List<Triple<String,JSONObject,Boolean>> children = runData.getJsonOutputFormat();			

			if(format == OutputFormat.JSON){
				runData.applyDataTransformations();
				JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(runData.getProductsJson(), runData.getMappings(), children);
				OutputWriter.writeResult(populatedProductsJson, business, format);
			}
			else if(format == OutputFormat.COUCHDB){
				JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(runData.getProductsJson(), runData.getMappings(), children);
				CouchWriter writer = new CouchWriter();
				writer.writeToCouch(populatedProductsJson);
			}
			else { //format == OutputFormat.XML
				JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(runData.getProductsJson(), runData.getMappings(), children);
				JSONConverter transformer = ConverterService.getService(business);
				JSONArray skus = transformer.convert(populatedProductsJson);
				OutputWriter.writeResult(skus, business, format);
			}
			
		}
		else{ //xls
			runData.populateAttributeTypesLists();
			System.out.println("Attribute types defined");
			runData.populateHeaders();
			System.out.println("Headers populated");
			List<ExcelOutputData> excelOutput = runData.getExcelOutputFormat();			
			System.out.println("Excel output defined");

			OutputWriter.writeResult(excelOutput, business, format);
			System.out.println("Result written");
		}

		//close connection
		runData.closeConnection();
		System.out.println("Done!");		
	}
}
