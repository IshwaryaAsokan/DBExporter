package driver;

import io.OutputWriter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import sql.SqlService;
import connection.ConnectionService;
import data.ObjectBuilder;

public class Driver {
	public static void main(String args[]){		
		Connection connection = ConnectionService.getConnection("BAKR");

		//get SQL
		String productAttrSql = SqlService.getSqlFile("src/sql/all-parent-attributes.sql");
		String itemAttrSql = SqlService.getSqlFile("src/sql/all-item-attributes.sql");
		String parentChildSql = SqlService.getSqlFile("src/sql/parent-child.sql");
		
		//get DB results
		ResultSet productAttr = SqlService.getResults(connection, productAttrSql);
		ResultSet itemAttr = SqlService.getResults(connection, itemAttrSql);
		ResultSet parentChild = SqlService.getResults(connection, parentChildSql);
		
		//build objects
		JSONObject products = ObjectBuilder.buildItemInfo(productAttr);
		JSONObject items = ObjectBuilder.buildItemInfo(itemAttr);
		Map<String,String> mappings = ObjectBuilder.mapItemsToProducts(parentChild);
		
		//combine objects
		JSONObject populatedProducts = ObjectBuilder.buildProducts(products, mappings, items);
		
		//output results
		OutputWriter.writeResult(populatedProducts);
		
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
