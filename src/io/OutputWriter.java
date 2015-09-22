package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jxl.*;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;

import definitions.Business;
import definitions.ExcelOutputFormat;
import definitions.OutputFormat;

public class OutputWriter {
	private static final String FILE_LOCATION_ROOT = "C:/Applications/DBHumanReadableOutput/";
	
	public static void writeResult(JSONObject obj, Business business, OutputFormat format){
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(FILE_LOCATION_ROOT + business.toString() + "." + format.toString());
			fileWriter.append(obj.toString(4));
		}
		catch (Exception e) {
			System.out.println("Error writing out file: " + FILE_LOCATION_ROOT + business.toString() + "." + format.toString());
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
	
	public static void writeResult(List<ExcelOutputData> excelOutput, Business business, OutputFormat format){
		//format of Triple: tab name, items to output, list of all attributes		
	    try {
	    	WritableWorkbook wworkbook;
			wworkbook = Workbook.createWorkbook(new File(FILE_LOCATION_ROOT + business.toString() + "." + format.toString()));
			int worksheetIndex = 0;
			
			for(ExcelOutputData sheet : excelOutput){
				String tabName = (String) sheet.getSheetName();
				JSONObject data = (JSONObject) sheet.getData();
				List<String> attrs = (List<String>) sheet.getTypes();
				ExcelOutputFormat excelFormat = sheet.getExcelFormat();
				
				WritableSheet wsheet = wworkbook.createSheet(tabName, worksheetIndex);
				
				if(excelFormat == ExcelOutputFormat.TABLE){
					int columnIndex = 0;
					int rowIndex = 0;
					//output attributes across top before marching through objects!
					for(String attr : attrs){
						wsheet.addCell(new Label(columnIndex, rowIndex, attr)); 
						columnIndex++;
					}
					
					rowIndex++;
					Iterator<String> iter = data.keys();
					
					while(iter.hasNext()){
						String key = iter.next();
						JSONObject obj = (JSONObject) data.get(key);
						
						columnIndex = 0;
						for(String attr : attrs){			
							if(obj.has(attr)){
								wsheet.addCell(new Label(columnIndex, rowIndex, obj.getString(attr))); 
							}
							columnIndex++;
						}					
						rowIndex++;					
					}
				}
				else if (excelFormat == ExcelOutputFormat.EAV){
					Iterator<String> iter = data.keys();

					//output headers
					int rowIndex = 0;
					
					while(iter.hasNext()){
						String key = iter.next(); //item_no
						JSONObject obj = (JSONObject) data.get(key);
						
						for(String attr : attrs){			
							if(obj.has(attr)){
								String[] values = obj.getString(attr).split("|");
								
								for(String value : values){
									wsheet.addCell(new Label(0, rowIndex, key));
									wsheet.addCell(new Label(1, rowIndex, attr));
									wsheet.addCell(new Label(2, rowIndex, value));
									System.out.println("Just wrote row: " + rowIndex);
									rowIndex++;
								}
							}
						}
					}
				}

				worksheetIndex++;
			}
			
			wworkbook.write();
			wworkbook.close();
			
		} catch (IOException e) {
			System.out.println("Error writing excel sheet (output)");
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Error writing excel sheet (json)");
			e.printStackTrace();
		} catch (RowsExceededException e) {
			System.out.println("Error writing excel sheet (rows)");
			e.printStackTrace();
		} catch (WriteException e) {
			System.out.println("Error writing excel sheet (write)");
			e.printStackTrace();
		}
	}
}
