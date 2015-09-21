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
		runBuilder(Business.BAKR, OutputFormat.JSON);
	}	
	
	private static void runBuilder(Business business, OutputFormat format){
		Connection connection = ConnectionService.getConnection(business);

		//build objects
		JSONObject productsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/all-product-attributes.sql"));

		//build child items
		JSONObject itemsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/all-item-attributes.sql"));
		JSONObject adCopyJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/ad-copy.sql"));
		JSONObject crossSellingJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/cross-selling.sql"));
				
		if(format == OutputFormat.JSON){			
			//get parent/child mapping
			Map<String,String> mappings = JSONObjectBuilder.mapItemsToProducts(SqlService.getResults(connection, "src/sql/parent-child.sql"));
			
			List<Pair<String,JSONObject>> children = new ArrayList<Pair<String,JSONObject>>();
			children.add(Pair.of("skus", itemsJson));
			children.add(Pair.of("adCopy", adCopyJson));
			children.add(Pair.of("crossSelling", crossSellingJson));
			
			JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(productsJson, mappings, children);			
			OutputWriter.writeResult(populatedProductsJson, business, format);
		}
		else{ //xls
			//get all attribute types
			List<String> productAttrTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/all-product-attribute-types.sql");
			List<String> itemAttrTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/all-item-attribute-types.sql");
			List<String> adCopyTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/ad-copy-types.sql");
			List<String> crossSellingTypesList = DataBuilder.getAttributeTypes(connection, "src/sql/cross-selling-types.sql");
			
			//build excel sheets: Triple.of("Sheet Name", products, columns)
			List<Triple<String,JSONObject,List<String>>> sheets = new ArrayList<Triple<String,JSONObject,List<String>>>();
			sheets.add(Triple.of("Products", productsJson, productAttrTypesList));
			sheets.add(Triple.of("Items", itemsJson, itemAttrTypesList));
			sheets.add(Triple.of("Ad Copy", adCopyJson, adCopyTypesList));
			sheets.add(Triple.of("Cross Selling", crossSellingJson, crossSellingTypesList));
			
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
