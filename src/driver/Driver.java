package driver;

import io.ExcelOutputData;
import io.OutputWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;

import sql.SqlService;
import connection.ConnectionService;
import data.DataBuilder;
import data.JSONObjectBuilder;
import data.transformers.json.JSONTransformer;
import definitions.Business;
import definitions.ExcelOutputFormat;
import definitions.OutputFormat;

public class Driver {
	public static void main(String args[]){		
		//runBuilder(Business.PORT, OutputFormat.JSON);
		runBuilderSTRLPriceSpider(Business.STRL, OutputFormat.JSON);
	}	
	
	private static void runBuilder(Business business, OutputFormat format){
		Connection connection = ConnectionService.getConnection(business);

		//build objects
		JSONObject productsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "all-product-attributes.sql", business));

		//build child items
		JSONObject itemsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "all-item-attributes.sql", business));
		JSONObject adCopyJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "ad-copy.sql", business));
		JSONObject crossSellingJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "cross-selling.sql", business));
		JSONObject keywordsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "keywords.sql", business));

		if(format == OutputFormat.JSON){
			//get parent/child mapping
			Map<String,String> mappings = JSONObjectBuilder.mapItemsToProducts(SqlService.getResults(connection, "parent-child.sql", business));
			
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
			List<String> productAttrsList = DataBuilder.getAttributeTypes(connection, "all-product-attribute-types.sql", business);
			List<String> itemAttrsList = DataBuilder.getAttributeTypes(connection, "all-item-attribute-types.sql", business);
			List<String> adCopyAttrsList = DataBuilder.getAttributeTypes(connection, "ad-copy-types.sql", business);
			List<String> keywordAttrsList = DataBuilder.getAttributeTypes(connection, "keyword-types.sql", business);
			List<String> crossSellingAttrsList = DataBuilder.getAttributeTypes(connection, "cross-selling-types.sql", business);
			
			String[] crossSellingHeadersArray = {"Item", "Relationship", "Related Item"};
			List<String> crossSellingHeaders = Arrays.asList(crossSellingHeadersArray);
			
			//build excel sheets
			List<ExcelOutputData> excelOutput = new ArrayList<ExcelOutputData>();
			
			excelOutput.add(new ExcelOutputData("Products", productsJson, productAttrsList, productAttrsList, ExcelOutputFormat.TABLE));
			excelOutput.add(new ExcelOutputData("Items", itemsJson, itemAttrsList, itemAttrsList, ExcelOutputFormat.TABLE));
			excelOutput.add(new ExcelOutputData("Ad Copy", adCopyJson, adCopyAttrsList, adCopyAttrsList, ExcelOutputFormat.TABLE));
			excelOutput.add(new ExcelOutputData("Cross Selling", crossSellingJson, crossSellingHeaders, crossSellingAttrsList, ExcelOutputFormat.EAV));
			excelOutput.add(new ExcelOutputData("Keywords", keywordsJson, keywordAttrsList, keywordAttrsList, ExcelOutputFormat.TABLE));
			
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
	
	private static void runBuilderSTRLPriceSpider(Business business, OutputFormat format){
		Connection connection = ConnectionService.getConnection(business);

		//build objects
		JSONObject productsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/STRL/pricespider/all-product-attributes.sql"));

		//build child items
		JSONObject itemsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/STRL/pricespider/all-item-attributes.sql"));
		JSONObject keywordsJson = JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "src/sql/STRL/pricespider/keywords.sql"));
		
		//get parent/child mapping
		Map<String,String> mappings = JSONObjectBuilder.mapItemsToProducts(SqlService.getResults(connection, "src/sql/STRL/pricespider/parent-child.sql"));
		
		//build json objects: Triple.of("attribute name", data, attachInfoToParent)
		List<Triple<String,JSONObject,Boolean>> children = new ArrayList<Triple<String,JSONObject,Boolean>>();
		children.add(Triple.of("skus", itemsJson, Boolean.TRUE));
		children.add(Triple.of("keywords", keywordsJson, Boolean.FALSE));
		
		JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(productsJson, mappings, children);
		
		JSONArray skus = JSONTransformer.transformSTRL(populatedProductsJson);
		System.out.println(skus);
		//OutputWriter.writeResult(populatedProductsJson, business, format);
		
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
