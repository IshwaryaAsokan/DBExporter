package sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import definitions.enums.Business;

public class SqlService {
	private final static String[] STANDARD_PUNI_BUSINESSES = {"PORT", "ANNS", "NKUK", "NBKR", 
			"MIRA", "KBRZ", "KALL", "NDST", "ENGN", "INDA", "RESI"};
	private final static String PUNI_ROOT = "parameterized/puni/";
	
	public String getSqlFile(String fileName, Business business){
		if(Arrays.asList(STANDARD_PUNI_BUSINESSES).contains(business.toString())){
			String fileLocation = PUNI_ROOT + fileName;
			String sql = getSqlFile(fileLocation);
			sql = sql.replace("{{business}}", business.toString());
			return sql;
		}
		else {
			String fileLocation = business.toString() + "/" + fileName;
			return getSqlFile(fileLocation);
		}			
	}
	
	public String getSqlFile(String fileLocation) {
		try {
			InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(fileLocation));
			BufferedReader buffReader = new BufferedReader(reader);
			return IOUtils.toString(buffReader);
		} catch (IOException e) {
			System.out.println("Error reading SQL: " + fileLocation);
			e.printStackTrace();
		} catch (NullPointerException npe) {
			System.out.println("There is no file at: " + fileLocation);
		}
		return null;
	}
	
	public ResultSet getResults(Connection connection, String fileName, Business business){
		String sql = getSqlFile(fileName, business);
		return executeQuery(connection, sql);
	}
	
	public ResultSet getResults(Connection connection, String fileLocation){
		String sql = getSqlFile(fileLocation);
		return executeQuery(connection, sql);
	}
	
	private static ResultSet executeQuery(Connection connection, String sql){
		if(sql != null){
			try {
				Statement statement = connection.createStatement();
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
