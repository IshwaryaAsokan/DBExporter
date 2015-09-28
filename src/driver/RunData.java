package driver;

import io.ExcelOutputData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;

import connection.ConnectionService;
import sql.SqlService;
import data.DataBuilder;
import data.JSONObjectBuilder;
import definitions.Business;
import definitions.ExcelOutputFormat;
import definitions.OutputFormat;

public class RunData {
	private Business business;
	private OutputFormat format;
	private Connection connection;
	private JSONObject productsJson;
	private JSONObject itemsJson;
	private JSONObject adCopyJson;
	private JSONObject crossSellingJson;
	private JSONObject keywordsJson;
	private List<String> productAttrsList;
	private List<String> itemAttrsList;
	private List<String> adCopyAttrsList;
	private List<String> keywordAttrsList;
	private List<String> crossSellingAttrsList;
	private List<String> productAttrHeaders;
	private List<String> itemAttrHeaders;
	private List<String> adCopyHeaders;
	private List<String> keywordHeaders;
	private List<String> crossSellingHeaders;
	private Map<String,String> mappings;

	public RunData(Business business, OutputFormat format){
		setBusiness(business);
		setFormat(format);
		setConnection(ConnectionService.getConnection(business));
	}
	
	public void populateJson(){
		setProductsJson(JSONObjectBuilder.buildItemInfo(SqlService.getResults(getConnection(), "all-product-attributes.sql", business)));
		
		setItemsJson(JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "all-item-attributes.sql", business)));
		setAdCopyJson(JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "ad-copy.sql", business)));
		setCrossSellingJson(JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "cross-selling.sql", business)));
		setKeywordsJson(JSONObjectBuilder.buildItemInfo(SqlService.getResults(connection, "keywords.sql", business)));
	}
	
	public void populateAttributeTypesLists(){
		setProductAttrsList(DataBuilder.getAttributeTypes(connection, "all-product-attribute-types.sql", business));
		setItemAttrsList(DataBuilder.getAttributeTypes(connection, "all-item-attribute-types.sql", business));
		setAdCopyAttrsList(DataBuilder.getAttributeTypes(connection, "ad-copy-types.sql", business));
		setKeywordAttrsList(DataBuilder.getAttributeTypes(connection, "keyword-types.sql", business));
		setCrossSellingAttrsList(DataBuilder.getAttributeTypes(connection, "cross-selling-types.sql", business));
	}
	
	public void populateHeaders(){
		String[] crossSellingHeadersArray = {"Item", "Relationship", "Related Item"};
		setCrossSellingHeaders(Arrays.asList(crossSellingHeadersArray));

		setProductAttrHeaders(getProductAttrsList());
		setItemAttrHeaders(getItemAttrsList());
		setAdCopyHeaders(getAdCopyAttrsList());
		setKeywordHeaders(getKeywordAttrsList());
	}
	
	public List<ExcelOutputData> getExcelOutputFormat(){
		List<ExcelOutputData> excelOutput = new ArrayList<ExcelOutputData>();
		
		excelOutput.add(new ExcelOutputData("Products", getProductsJson(), getProductAttrsList(), getProductAttrHeaders(), ExcelOutputFormat.TABLE));
		excelOutput.add(new ExcelOutputData("Items", getItemsJson(), getItemAttrsList(), getItemAttrHeaders(), ExcelOutputFormat.TABLE));
		excelOutput.add(new ExcelOutputData("Ad Copy", getAdCopyJson(), getAdCopyAttrsList(), getAdCopyHeaders(), ExcelOutputFormat.TABLE));
		excelOutput.add(new ExcelOutputData("Cross Selling", getCrossSellingJson(),getAdCopyAttrsList(), getCrossSellingHeaders(), ExcelOutputFormat.EAV));
		excelOutput.add(new ExcelOutputData("Keywords", getKeywordsJson(), getKeywordAttrsList(), getKeywordHeaders(), ExcelOutputFormat.TABLE));
		
		return excelOutput;
	}
	
	public List<Triple<String,JSONObject,Boolean>> getJsonOutputFormat(){
		List<Triple<String,JSONObject,Boolean>> children = new ArrayList<Triple<String,JSONObject,Boolean>>();
		
		children.add(Triple.of("skus", getItemsJson(), Boolean.TRUE));
		children.add(Triple.of("adCopy", getAdCopyJson(), Boolean.FALSE));
		children.add(Triple.of("crossSelling", getCrossSellingJson(), Boolean.FALSE));
		children.add(Triple.of("keywords", getKeywordsJson(), Boolean.FALSE));
		
		return children;
	}
	
	public void populateMappings(){
		this.setMappings(JSONObjectBuilder.mapItemsToProducts(SqlService.getResults(connection, "parent-child.sql", business)));
	}
	
	public void closeConnection(){
		try {
			getConnection().close();
		} catch (SQLException e) {
			System.out.println("Failed to close DB connection.");
			e.printStackTrace();
		}
	}
	
	public Business getBusiness() {
		return business;
	}
	public void setBusiness(Business business) {
		this.business = business;
	}
	public OutputFormat getFormat() {
		return format;
	}
	public void setFormat(OutputFormat format) {
		this.format = format;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public JSONObject getProductsJson() {
		return productsJson;
	}
	public void setProductsJson(JSONObject productsJson) {
		this.productsJson = productsJson;
	}
	public JSONObject getItemsJson() {
		return itemsJson;
	}
	public void setItemsJson(JSONObject itemsJson) {
		this.itemsJson = itemsJson;
	}
	public JSONObject getAdCopyJson() {
		return adCopyJson;
	}
	public void setAdCopyJson(JSONObject adCopyJson) {
		this.adCopyJson = adCopyJson;
	}
	public JSONObject getCrossSellingJson() {
		return crossSellingJson;
	}
	public void setCrossSellingJson(JSONObject crossSellingJson) {
		this.crossSellingJson = crossSellingJson;
	}
	public JSONObject getKeywordsJson() {
		return keywordsJson;
	}
	public void setKeywordsJson(JSONObject keywordsJson) {
		this.keywordsJson = keywordsJson;
	}
	public List<String> getProductAttrsList() {
		return productAttrsList;
	}
	public void setProductAttrsList(List<String> productAttrsList) {
		this.productAttrsList = productAttrsList;
	}
	public List<String> getItemAttrsList() {
		return itemAttrsList;
	}
	public void setItemAttrsList(List<String> itemAttrsList) {
		this.itemAttrsList = itemAttrsList;
	}
	public List<String> getAdCopyAttrsList() {
		return adCopyAttrsList;
	}
	public void setAdCopyAttrsList(List<String> adCopyAttrsList) {
		this.adCopyAttrsList = adCopyAttrsList;
	}
	public List<String> getKeywordAttrsList() {
		return keywordAttrsList;
	}
	public void setKeywordAttrsList(List<String> keywordAttrsList) {
		this.keywordAttrsList = keywordAttrsList;
	}
	public List<String> getCrossSellingAttrsList() {
		return crossSellingAttrsList;
	}
	public void setCrossSellingAttrsList(List<String> crossSellingAttrsList) {
		this.crossSellingAttrsList = crossSellingAttrsList;
	}
	public List<String> getProductAttrHeaders() {
		return productAttrHeaders;
	}
	public void setProductAttrHeaders(List<String> productAttrHeaders) {
		this.productAttrHeaders = productAttrHeaders;
	}
	public List<String> getItemAttrHeaders() {
		return itemAttrHeaders;
	}
	public void setItemAttrHeaders(List<String> itemAttrHeaders) {
		this.itemAttrHeaders = itemAttrHeaders;
	}
	public List<String> getAdCopyHeaders() {
		return adCopyHeaders;
	}
	public void setAdCopyHeaders(List<String> adCopyHeaders) {
		this.adCopyHeaders = adCopyHeaders;
	}
	public List<String> getKeywordHeaders() {
		return keywordHeaders;
	}
	public void setKeywordHeaders(List<String> keywordHeaders) {
		this.keywordHeaders = keywordHeaders;
	}
	public List<String> getCrossSellingHeaders() {
		return crossSellingHeaders;
	}
	public void setCrossSellingHeaders(List<String> crossSellingHeaders) {
		this.crossSellingHeaders = crossSellingHeaders;
	}
	public Map<String, String> getMappings() {
		return mappings;
	}
	public void setMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}
}
