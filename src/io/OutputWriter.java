package io;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
	    try {
			Workbook wb = new SXSSFWorkbook();
			FileOutputStream fileOut = new FileOutputStream(FILE_LOCATION_ROOT + business.toString() + "." + format.toString());
			
			for(ExcelOutputData sheet : excelOutput){
				String tabName = (String) sheet.getSheetName();
				JSONObject data = (JSONObject) sheet.getData();
				List<String> attrs = (List<String>) sheet.getTypes();
				ExcelOutputFormat excelFormat = sheet.getExcelFormat();
				Sheet currentSheet = wb.createSheet(tabName);
				
				if(excelFormat == ExcelOutputFormat.TABLE){
					int columnIndex = 0;
										
					Row row = currentSheet.createRow(0);
					for(String attr : attrs){ //output attributes across top before marching through objects!
						row.createCell(columnIndex).setCellValue(attr);
						columnIndex++;
					}
					
					int rowIndex = 1;
					Iterator<String> iter = data.keys();
					
					while(iter.hasNext()){
						String key = iter.next();
						JSONObject obj = (JSONObject) data.get(key);
						Row currentRow = currentSheet.createRow(rowIndex);
						
						columnIndex = 0;
						for(String attr : attrs){			
							if(obj.has(attr)){
								currentRow.createCell(columnIndex).setCellValue(obj.getString(attr));
							}
							columnIndex++;
						}					
						rowIndex++;					
					}
				}
				else if (excelFormat == ExcelOutputFormat.EAV){
					Iterator<String> iter = data.keys();
					int rowIndex = 0;
					
					while(iter.hasNext()){
						String key = iter.next(); //item_no
						JSONObject obj = (JSONObject) data.get(key);
						
						for(String attr : attrs){			
							if(obj.has(attr)){
								String[] values = obj.getString(attr).split("\\|");
								
								for(String value : values){
									Row currentRow = currentSheet.createRow(rowIndex);
									currentRow.createCell(0).setCellValue(key);
									currentRow.createCell(1).setCellValue(attr);
									currentRow.createCell(2).setCellValue(value);
									rowIndex++;
								}
							}
						}
					}
				}
			}
			
			wb.write(fileOut);
		    fileOut.close();
			
		} catch (IOException e) {
			System.out.println("Error writing excel sheet (output)");
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Error writing excel sheet (json)");
			e.printStackTrace();
		}
	}
}
