package data.services;

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
import data.alteration.values.DataValueTransformer;
import data.alteration.values.Transformation;
import definitions.enums.Business;
import definitions.enums.ExcelOutputFormat;
import definitions.enums.JSONNestingLevel;
import definitions.enums.JsonTransformationType;
import definitions.enums.OutputFormat;

public class ExcelDataService {
	private Business business;
	private OutputFormat format;
	private Connection connection;
	private SqlService sqlService;
	private String sqlRoot;
	
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

	public ExcelDataService(Business business, OutputFormat format, String sqlRoot){
		setBusiness(business);
		setFormat(format);
		ConnectionService connectionService = new ConnectionService(business);
		setConnection(connectionService.getConnection());
		setSqlService(new SqlService());
	}
	
	public void populateAttributeTypesLists(){
		setProductAttrsList(DataBuilder.getAttributeTypes(connection, "all-product-attribute-types.sql", business, getSqlService(), getSqlRoot()));
		setItemAttrsList(DataBuilder.getAttributeTypes(connection, "all-item-attribute-types.sql", business, getSqlService(), getSqlRoot()));
		setAdCopyAttrsList(DataBuilder.getAttributeTypes(connection, "ad-copy-types.sql", business, getSqlService(), getSqlRoot()));
		setKeywordAttrsList(DataBuilder.getAttributeTypes(connection, "keyword-types.sql", business, getSqlService(), getSqlRoot()));
		setCrossSellingAttrsList(DataBuilder.getAttributeTypes(connection, "cross-selling-types.sql", business, getSqlService(), getSqlRoot()));
		System.out.println("Attribute types defined");
	}
	
	public void populateHeaders(){
		String[] crossSellingHeadersArray = {"Item", "Relationship", "Related Item"};
		setCrossSellingHeaders(Arrays.asList(crossSellingHeadersArray));

		setProductAttrHeaders(getProductAttrsList());
		setItemAttrHeaders(getItemAttrsList());
		setAdCopyHeaders(getAdCopyAttrsList());
		setKeywordHeaders(getKeywordAttrsList());
		System.out.println("Headers populated");
	}
	
	public List<ExcelOutputData> getExcelOutputFormat(){
		List<ExcelOutputData> excelOutput = new ArrayList<ExcelOutputData>();
		
		excelOutput.add(new ExcelOutputData("Products", "products", getProductAttrsList(), getProductAttrHeaders(), ExcelOutputFormat.TABLE, JSONNestingLevel.PARENT));
		excelOutput.add(new ExcelOutputData("Items", "skus", getItemAttrsList(), getItemAttrHeaders(), ExcelOutputFormat.TABLE, JSONNestingLevel.CHILD));
		excelOutput.add(new ExcelOutputData("Ad Copy", "adCopy", getAdCopyAttrsList(), getAdCopyHeaders(), ExcelOutputFormat.TABLE, JSONNestingLevel.CHILD));
		excelOutput.add(new ExcelOutputData("Cross Selling", "crossSelling", getCrossSellingAttrsList(), getCrossSellingHeaders(), ExcelOutputFormat.EAV, JSONNestingLevel.CHILD));
		excelOutput.add(new ExcelOutputData("Keywords", "keywords", getKeywordAttrsList(), getKeywordHeaders(), ExcelOutputFormat.TABLE, JSONNestingLevel.CHILD));

		System.out.println("Excel output defined");
		return excelOutput;
	}

	//this needs to be moved
//	public List<Triple<String,JSONObject,Boolean>> getJsonOutputFormat(){
//		List<Triple<String,JSONObject,Boolean>> children = new ArrayList<Triple<String,JSONObject,Boolean>>();
//		
//		children.add(Triple.of("skus", getItemsJson(), Boolean.TRUE));
//		children.add(Triple.of("adCopy", getAdCopyJson(), Boolean.FALSE));
//		children.add(Triple.of("crossSelling", getCrossSellingJson(), Boolean.FALSE));
//		
//		//merge keywords into productsJson
//		JSONObject outputData = JSONObjectBuilder.mergeJsonAttributes(getProductsJson(), getKeywordsJson());
//		
//		
//		setProductsJson(outputData);
//		
//		return children;
//	}
	
	//this needs to be moved
//	public void applyDataTransformations(){ //rename keys, append/prepend values
//		List<Transformation> transformations = new ArrayList<Transformation>();
//		transformations.add(new Transformation(JsonTransformationType.PREPEND, "JPG_Photo_Name", "www."));
//		transformations.add(new Transformation(JsonTransformationType.APPEND, "JPG_Photo_Name", "--testing the appending!"));
//		transformations.add(new Transformation(JsonTransformationType.NEW_KEY, "JPG_Photo_Name", "JPG"));
//		DataValueTransformer.transform(getProductsJson(), transformations);
//	}
	
	public void closeConnection(){
		try {
			getConnection().close();
		} catch (SQLException e) {
			System.out.println("Failed to close DB connection [DataManipulationService].");
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
	public SqlService getSqlService() {
		return sqlService;
	}
	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
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
	public String getSqlRoot() {
		return sqlRoot;
	}
	public void setSqlRoot(String sqlRoot) {
		this.sqlRoot = sqlRoot;
	}
}
