package driver;

import io.ExcelOutputData;
import io.OutputWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;

import sql.SqlService;
import connection.ConnectionService;
import data.DataBuilder;
import data.JSONObjectBuilder;
import definitions.Business;
import definitions.ExcelOutputFormat;
import definitions.OutputFormat;

public class Driver {
	public static void main(String args[]){		
		runBuilder(Business.NBKR, OutputFormat.XLS);
	}	
	
	private static void runBuilder(Business business, OutputFormat format){
		Connection connection = ConnectionService.getConnection(business);

		//build objects
		JSONObject productsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/" + business.toString() + "/all-product-attributes.sql"));

		//build child items
		JSONObject itemsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/" + business.toString() + "/all-item-attributes.sql"));
		JSONObject adCopyJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/" + business.toString() + "/ad-copy.sql"));
		JSONObject crossSellingJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/" + business.toString() + "/cross-selling.sql"));
		JSONObject keywordsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/" + business.toString() + "/keywords.sql"));

		if(format == OutputFormat.JSON){
			//get parent/child mapping
			Map<String,String> mappings = JSONObjectBuilder.mapItemsToProducts(SqlService.getResults(connection, "src/sql/" + business.toString() + "/parent-child.sql"));
			
			//build json objects: Triple.of("attribute name", data, attachInfoToParent)
			List<Triple<String,JSONObject,Boolean>> children = new ArrayList<Triple<String,JSONObject,Boolean>>();
			children.add(Triple.of("skus", itemsJson, Boolean.TRUE));
			children.add(Triple.of("adCopy", adCopyJson, Boolean.FALSE));
			children.add(Triple.of("crossSelling", crossSellingJson, Boolean.FALSE));
			
			JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(productsJson, mappings, children);			
			OutputWriter.writeResult(populatedProductsJson, business, format);
		}
		else{ //xls
			//get all attribute types
			List<String> productAttrTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/" + business.toString() + "/all-product-attribute-types.sql");
			List<String> itemAttrTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/" + business.toString() + "/all-item-attribute-types.sql");
			List<String> adCopyTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/" + business.toString() + "/ad-copy-types.sql");
			List<String> crossSellingTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/" + business.toString() + "/cross-selling-types.sql");
			List<String> keywordTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/" + business.toString() + "/keyword-types.sql");
			
			//build excel sheets
			List<ExcelOutputData> excelOutput = new ArrayList<ExcelOutputData>();
			
			excelOutput.add(new ExcelOutputData("Products", productsJson, productAttrTypesList, ExcelOutputFormat.TABLE));
			excelOutput.add(new ExcelOutputData("Items", itemsJson, itemAttrTypesList, ExcelOutputFormat.TABLE));
			excelOutput.add(new ExcelOutputData("Ad Copy", adCopyJson, adCopyTypesList, ExcelOutputFormat.TABLE));
			excelOutput.add(new ExcelOutputData("Cross Selling", crossSellingJson, crossSellingTypesList, ExcelOutputFormat.EAV));
			excelOutput.add(new ExcelOutputData("Keywords", keywordsJson, keywordTypesList, ExcelOutputFormat.TABLE));
			
			//output results
			OutputWriter.writeResult(excelOutput, business, format);
		}

		//close connection
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("Failed to close DB connection.");
			e.printStackTrace();
		}
		
		System.out.println("Done!");		
	}
}
