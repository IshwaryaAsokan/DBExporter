package sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlService {
	public static String getSqlFile(String fileLocation){
		try {
			return new String(Files.readAllBytes(Paths.get(fileLocation)));
		} catch (IOException e) {
			System.out.println("Error reading: " + fileLocation);
			e.printStackTrace();
		}
		return null;
	}
	
	public static ResultSet getResults(Connection connection, String fileLocation){
		String sql = getSqlFile(fileLocation);		
		
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
