package io;

import java.util.List;

import org.json.JSONObject;

import definitions.enums.ExcelOutputFormat;
import definitions.enums.JSONNestingLevel;

public class ExcelOutputData {
	private String sheetName;
	private String attributeName;
	private List<String> headers;
	private List<String> attributes;
	private ExcelOutputFormat excelFormat;
	private JSONNestingLevel level;

	public ExcelOutputData(String sheetName, String attributeName, List<String> attrs, List<String> headers, ExcelOutputFormat excelFormat, JSONNestingLevel level){
		setSheetName(sheetName);
		setAttributeName(attributeName);
		setHeaders(headers);
		setExcelFormat(excelFormat);
		setAttributes(attrs);
		setLevel(level);
	}
	
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
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
	public JSONNestingLevel getLevel() {
		return level;
	}
	public void setLevel(JSONNestingLevel level) {
		this.level = level;
	}
}
