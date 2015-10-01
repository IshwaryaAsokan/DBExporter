package connection;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import definitions.enums.Business;

public class ConnectionService {
	
	static Properties properties = new Properties();
	static InputStream input;
	
	public static Connection getConnection(Business business){ //if this program is expanded create an enum for business
		try {
			input = new FileInputStream("src/connection/connection.properties");
			properties.load(input);

			String connectionString = properties.getProperty(business.toString() + ".connection.string");
			String user = properties.getProperty(business.toString() + ".connection.username");
			String password = properties.getProperty(business.toString() + ".connection.password");
		
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection connection = DriverManager.getConnection(connectionString, user, password);	
			
			return connection;
			
		} catch (ClassNotFoundException e1) {
			System.out.println("Unable to find Oracle driver.");
		} catch (SQLException e) {
			System.out.println("Error creating connection.");
		} catch (FileNotFoundException e) {
			System.out.println("Connection properties file not found.");
		} catch (IOException e) {
			System.out.println("Error loading properties file.");
		}
		
		return null;
	}
}
