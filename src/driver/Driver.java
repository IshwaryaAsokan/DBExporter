package driver;

import io.CouchWriter;
import io.ExcelOutputData;
import io.OutputWriter;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;

import data.JSONObjectBuilder;
import data.transformers.json.JSONTransformer;
import data.transformers.json.TransformationService;
import definitions.Business;
import definitions.OutputFormat;

public class Driver {
	public static void main(String args[]){
		runBuilder(Business.PORT, OutputFormat.XLSX);
	}
	
	private static void runBuilder(Business business, OutputFormat format){
		RunData runData = new RunData(business, format);
		System.out.println("Connection made");
		runData.populateJson();
		System.out.println("JSON populated");

		if(format == OutputFormat.JSON || format == OutputFormat.XML || format == OutputFormat.COUCHDB){
			runData.populateMappings();
			List<Triple<String,JSONObject,Boolean>> children = runData.getJsonOutputFormat();
			JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(runData.getProductsJson(), runData.getMappings(), children);

			if(format == OutputFormat.JSON){
				OutputWriter.writeResult(populatedProductsJson, business, format);
			}
			else if(format == OutputFormat.COUCHDB){
				CouchWriter writer = new CouchWriter();
				writer.writeToCouch(populatedProductsJson);
			}
			else { //format == OutputFormat.XML
				JSONTransformer transformer = TransformationService.getService(business);
				JSONArray skus = transformer.transform(populatedProductsJson);
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
