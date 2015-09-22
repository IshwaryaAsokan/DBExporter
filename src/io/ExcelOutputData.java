package io;

import java.util.List;
import org.json.JSONObject;
import definitions.ExcelOutputFormat;

public class ExcelOutputData {
	private String sheetName;
	private JSONObject data;
	private List<String> types;
	private ExcelOutputFormat excelFormat;
	
	public ExcelOutputData(String sheetName, JSONObject data, List<String> types, ExcelOutputFormat excelFormat){
		setSheetName(sheetName);
		setData(data);
		setTypes(types);
		setExcelFormat(excelFormat);
	}
	
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public JSONObject getData() {
		return data;
	}
	public void setData(JSONObject data) {
		this.data = data;
	}
	public List<String> getTypes() {
		return types;
	}
	public void setTypes(List<String> types) {
		this.types = types;
	}
	public ExcelOutputFormat getExcelFormat() {
		return excelFormat;
	}
	public void setExcelFormat(ExcelOutputFormat excelFormat) {
		this.excelFormat = excelFormat;
	}
}
