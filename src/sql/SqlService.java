package sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import definitions.enums.Business;

public class SqlService {
	private final static String[] STANDARD_PUNI_BUSINESSES = {"PORT", "ANNS", "NKUK", "NBKR", "MIRA"};
	private final static String SQL_ROOT = "src/sql/";
	private final static String PUNI_ROOT = "src/sql/parameterized/puni/";
	
	public static String getSqlFile(String fileName, Business business){
		if(Arrays.asList(STANDARD_PUNI_BUSINESSES).contains(business.toString())){
			String fileLocation = PUNI_ROOT + fileName;
			String sql = getSqlFile(fileLocation);
			sql = sql.replace("{{business}}", business.toString());
			return sql;
		}
		else {
			String fileLocation = SQL_ROOT + business.toString() + "/" + fileName;
			return getSqlFile(fileLocation);
		}			
	}
	
	public static String getSqlFile(String fileLocation) {
		try {
			return new String(Files.readAllBytes(Paths.get(fileLocation)));
		} catch (IOException e) {
			System.out.println("Error reading file: " + fileLocation);
		}
		return null;
	}
	
	public static ResultSet getResults(Connection connection, String fileName, Business business){
		String sql = getSqlFile(fileName, business);
		return executeQuery(connection, sql);
	}
	
	public static ResultSet getResults(Connection connection, String fileLocation){
		String sql = getSqlFile(fileLocation);
		return executeQuery(connection, sql);
	}
	
	private static ResultSet executeQuery(Connection connection, String sql){
		if(sql != null){
			Statement statement;
			
			try {
				statement = connection.createStatement();
				statement.execute(sql);
				return statement.getResultSet();
			} catch (SQLException e) {
				System.out.println("Error executing SQL: " + sql);
				e.printStackTrace();
			}						
		}
				
		return null;		
	}			
}
