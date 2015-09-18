package io;

import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

public class OutputWriter {
	//if this program is expanded, generalize this to just be the root
	private static final String FILE_LOCATION = "C:/Applications/DBHumanReadableOutput/BAKR.txt";
	
	public static void writeResult(JSONObject obj){
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(FILE_LOCATION);
			fileWriter.append(obj.toString(4));
		}
		catch (Exception e) {
			System.out.println("Error writing out file: " + FILE_LOCATION);
			System.out.println(e.toString());
		}
		finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			}
			catch (IOException e){
				System.out.println("Error while flushing/closing fileWriter");
			}
		}

	}
}
