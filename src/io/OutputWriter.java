package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.converters.xml.ConverterService;
import data.converters.xml.JSONConverter;
import definitions.enums.Business;
import definitions.enums.BusinessPurpose;
import definitions.enums.ExcelOutputFormat;
import definitions.enums.JSONNestingLevel;
import definitions.enums.OutputFormat;

public class OutputWriter {
	private static final String FILE_LOCATION_ROOT = "C:/Applications/DBHumanReadableOutput/";
	
	public static void writeResult(JSONObject obj, Business business, OutputFormat format){
		FileWriter fileWriter = null;
		
		try {
			ensureRootDirExists();
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
		System.out.println("Result written");
	}
	
	public static void writeResult(JSONArray obj, Business business, OutputFormat format, BusinessPurpose purpose){
		String fileDestination = FILE_LOCATION_ROOT + business.toString() + "." + format.toString();
		if(purpose == BusinessPurpose.OLAPIC_XML){
			fileDestination = FILE_LOCATION_ROOT + business.toString() +"_"+ purpose.toString() +"." + format.toString();
		}
		FileWriter fileWriter = null;
		
		try {
			ensureRootDirExists();
			fileWriter = new FileWriter(fileDestination);
			if(format == OutputFormat.XML){
				String outputValue = org.json.XML.toString(obj);
				JSONConverter converterService = ConverterService.getService(business, purpose);

				for(Pair<String, String> replacement : converterService.getReplacements()){
					outputValue = outputValue.replace(replacement.getLeft(), replacement.getRight());
				}
				
				outputValue = StringUtils.normalizeSpace(outputValue);
				
				outputValue = converterService.getStartXmlWrapper() + outputValue + converterService.getEndXmlWrapper();				
				fileWriter.append(outputValue);
			}
			else{ //json
				fileWriter.append(obj.toString(4));
			}
		}
		catch (Exception e) {
			System.out.println("Error writing out file: " + fileDestination);
			System.out.println(e.toString());
		}
		finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			}
			catch (IOException e){
				System.out.println("Error while flushing/closing writer (FileOutputStream)");
				e.printStackTrace();
			}
		}
		System.out.println("Result written");
	}
	
	public static void writeResult(JSONObject populatedProductsJson, List<ExcelOutputData> excelOutput, Business business, OutputFormat format){
	    try {
	    	ensureRootDirExists();
			Workbook wb = new SXSSFWorkbook();
			FileOutputStream fileOut = new FileOutputStream(FILE_LOCATION_ROOT + business.toString() + "." + format.toString());
			
			for(ExcelOutputData sheet : excelOutput){
				String tabName = sheet.getSheetName();
				String attributeName = sheet.getAttributeName();
				List<String> attrs = sheet.getAttributes();
				List<String> headers = sheet.getHeaders();
				ExcelOutputFormat excelFormat = sheet.getExcelFormat();
				JSONNestingLevel level = sheet.getLevel();
				Sheet currentSheet = wb.createSheet(tabName);
				
				int columnIndex = 0;										
				Row row = currentSheet.createRow(0);
				for(String header : headers){ //output headers across top before marching through objects!
					row.createCell(columnIndex).setCellValue(header);
					columnIndex++;
				}
				int rowIndex = 1;
				Iterator<String> keys = populatedProductsJson.keys();
				
				if(excelFormat == ExcelOutputFormat.TABLE){					
					while(keys.hasNext()){
						String key = keys.next();
						JSONObject product = populatedProductsJson.getJSONObject(key);
						JSONObject obj = null;
						
						if(!tabName.equalsIgnoreCase("Items")){
							if(level == JSONNestingLevel.PARENT){
								obj = product;
							}
							else if(level == JSONNestingLevel.CHILD){
								if(product.has(attributeName)){
									obj = product.getJSONObject(attributeName);
								}
							}

							if(obj != null){
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
						else { //skus
							if(product.has(attributeName)){
								JSONArray skusArr = product.getJSONArray("skus");
								if(skusArr.length() > 0){
									
									for(int index = 0; index < skusArr.length(); index++){
										JSONObject sku = skusArr.getJSONObject(index);
										Row currentRow = currentSheet.createRow(rowIndex);
										columnIndex = 0;
										
										for(String attr : attrs){
											if(sku.has(attr)){
												currentRow.createCell(columnIndex).setCellValue(sku.getString(attr));
											}										
											columnIndex++;
										} //end attr for loop
										rowIndex++;
									} //end skus loop
								}
							}
						} //end skus else
					}
				}
				else if (excelFormat == ExcelOutputFormat.EAV){
					while(keys.hasNext()){
						String key = keys.next(); //item_no
						JSONObject product = populatedProductsJson.getJSONObject(key);
						
						if(product.has(attributeName)){
							JSONObject obj = product.getJSONObject(attributeName);
							
							for(String attr : sheet.getAttributes()){			
								if(obj.has(attr)){
									String[] values = obj.getString(attr).split("\\|");
									
									for(String value : values){
										if(!attr.equalsIgnoreCase("Item_No")){
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
	    System.out.println("Result written");
	}
	
	private static void ensureRootDirExists(){
    	File targetDir = new File(FILE_LOCATION_ROOT);
    	if(!targetDir.exists() && !targetDir.mkdirs()){
    	    throw new IllegalStateException("Couldn't create dir: " + targetDir);
    	}
	}
}
