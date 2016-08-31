package data.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import connection.ConnectionService;
import sql.SqlService;
import data.JSONObjectBuilder;
import definitions.enums.Business;

public class UnalteredJSONService {
	private Business business;
	private Connection connection;
	private SqlService sqlService;
	private String sqlRoot;
	
	public UnalteredJSONService(Business business, String sqlRoot){
		setBusiness(business);
		ConnectionService connectionService = new ConnectionService(business);
		setConnection(connectionService.getConnection());
		setSqlService(new SqlService());
		setSqlRoot(StringUtils.EMPTY);
	}
	
	public JSONObject getPopulatedJSON(){
		JSONObject productsJson = JSONObjectBuilder.buildItemInfo(getSqlService().getResults(getConnection(), "all-product-attributes.sql", getBusiness(), getSqlRoot()));
		System.out.println("JSON populated: products");
		JSONObject itemsJson = JSONObjectBuilder.buildItemInfo(getSqlService().getResults(getConnection(), "all-item-attributes.sql", getBusiness(), getSqlRoot()));
		System.out.println("JSON populated: items");
		JSONObject adCopyJson = JSONObjectBuilder.buildItemInfo(getSqlService().getResults(getConnection(), "ad-copy.sql", getBusiness(), getSqlRoot()));
		System.out.println("JSON populated: ad-copy");
		JSONObject crossSellingJson = JSONObjectBuilder.buildItemInfo(getSqlService().getResults(getConnection(), "cross-selling.sql", getBusiness(), getSqlRoot()));
		System.out.println("JSON populated: cross selling");
		JSONObject keywordsJson = JSONObjectBuilder.buildItemInfo(getSqlService().getResults(getConnection(), "keywords.sql", getBusiness(), getSqlRoot()));
		System.out.println("JSON populated: keywords");
		
		Map<String,String> mappings = JSONObjectBuilder.mapItemsToProducts(getSqlService().getResults(getConnection(), "parent-child.sql", getBusiness(), getSqlRoot()));
		
		List<Triple<String,JSONObject,Boolean>> children = new ArrayList<Triple<String,JSONObject,Boolean>>();
		children.add(Triple.of("skus", itemsJson, Boolean.TRUE));
		children.add(Triple.of("adCopy", adCopyJson, Boolean.FALSE));
		children.add(Triple.of("crossSelling", crossSellingJson, Boolean.FALSE));
		children.add(Triple.of("keywords", keywordsJson, Boolean.FALSE));
		
		JSONObject populatedProductsJson = JSONObjectBuilder.buildProducts(productsJson, mappings, children);
		System.out.println("JSON populated");
		return populatedProductsJson;
	}
	
	public Business getBusiness() {
		return business;
	}
	public void setBusiness(Business business) {
		this.business = business;
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
	public void closeConnection(){
		try {
			getConnection().close();
		} catch (SQLException e) {
			System.out.println("Failed to close DB connection [UnalteredJSONService].");
			e.printStackTrace();
		}
	}
	public String getSqlRoot() {
		return sqlRoot;
	}
	public void setSqlRoot(String sqlRoot) {
		this.sqlRoot = sqlRoot;
	}
}
