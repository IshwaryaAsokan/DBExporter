package data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import definitions.enums.Business;
import sql.SqlService;

public class DataBuilder {
	public static List<String> getAttributeTypes(Connection connection, String fileName, Business business, SqlService sqlService, String sqlRoot){
		ResultSet rs = sqlService.getResults(connection, fileName, business, sqlRoot);
		return getAttributeTypes(rs);
	}
	
	private static List<String> getAttributeTypes(ResultSet rs){
		List<String> attributes = new ArrayList<String>();
		
		try {
			while(rs != null && rs.next()){
				String attribute = rs.getString("TYPE");
				attributes.add(attribute);
			}
		} catch (SQLException e) {
			System.out.println("Error building item -> product mapping.");
			e.printStackTrace();
		}
		
		attributes.add(0, "Item_No");
		return attributes;
	}
}
