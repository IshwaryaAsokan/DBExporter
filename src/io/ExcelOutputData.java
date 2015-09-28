package io;

import java.util.List;
import org.json.JSONObject;
import definitions.ExcelOutputFormat;

public class ExcelOutputData {
	private String sheetName;
	private JSONObject data;
	private List<String> headers;
	private List<String> attributes;
	private ExcelOutputFormat excelFormat;
	
	public ExcelOutputData(String sheetName, JSONObject data, List<String> attrs, List<String> headers, ExcelOutputFormat excelFormat){
		setSheetName(sheetName);
		setData(data);
		setHeaders(headers);
		setExcelFormat(excelFormat);
		setAttributes(attrs);
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
	public List<String> getHeaders() {
		return headers;
	}
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	public ExcelOutputFormat getExcelFormat() {
		return excelFormat;
	}
	public void setExcelFormat(ExcelOutputFormat excelFormat) {
		this.excelFormat = excelFormat;
	}
	public List<String> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
}
