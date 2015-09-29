package driver;

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
		runBuilder(Business.STRL, OutputFormat.XML);
	}
	
	private static void runBuilder(Business business, OutputFormat format){
		RunData runData = new RunData(business, format);
		runData.populateJson();

		if(format == OutputFormat.JSON || format == OutputFormat.XML){
			runData.populateMappings();
			List<Triple<String,JSONObject,Boolean>> children = runData.getJsonOutputFormat();
			JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(runData.getProductsJson(), runData.getMappings(), children);

			if(format == OutputFormat.JSON){
				OutputWriter.writeResult(populatedProductsJson, business, format);
			}
			else { //format == OutputFormat.XML
				JSONTransformer transformer = TransformationService.getService(business);
				JSONArray skus = transformer.transform(populatedProductsJson);
				OutputWriter.writeResult(skus, business, format);
			}
			
		}
		else{ //xls
			runData.populateAttributeTypesLists();
			runData.populateHeaders();
			List<ExcelOutputData> excelOutput = runData.getExcelOutputFormat();			

			OutputWriter.writeResult(excelOutput, business, format);
		}

		//close connection
		runData.closeConnection();
		System.out.println("Done!");		
	}
}
