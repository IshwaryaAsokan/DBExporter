package driver;

import io.OutputWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;

import sql.SqlService;
import connection.ConnectionService;
import data.DataBuilder;
import data.JSONObjectBuilder;
import definitions.Business;
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
			
			//build excel sheets: Triple.of("Sheet Name", products, columns)
			List<Triple<String,JSONObject,List<String>>> sheets = new ArrayList<Triple<String,JSONObject,List<String>>>();
			sheets.add(Triple.of("Products", productsJson, productAttrTypesList));
			sheets.add(Triple.of("Items", itemsJson, itemAttrTypesList));
			sheets.add(Triple.of("Ad Copy", adCopyJson, adCopyTypesList));
			sheets.add(Triple.of("Cross Selling", crossSellingJson, crossSellingTypesList));
			sheets.add(Triple.of("Keywords", keywordsJson, keywordTypesList));
			
			//output results
			OutputWriter.writeResult(sheets, business, format);
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
