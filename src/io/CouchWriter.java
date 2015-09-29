package io;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class CouchWriter {
	private CouchDbProperties properties;
	private CouchDbClient dbClient;
	
	public void writeToCouch(){ //this example works - do not touch for now
		CouchDbProperties properties = new CouchDbProperties();
		properties.setHost("127.0.0.1");
		properties.setPort(5984);
		properties.setProtocol("http");
		properties.setDbName("people");
		properties.setCreateDbIfNotExist(false);
		
		CouchDbClient dbClient = new CouchDbClient(properties);
		
		Map<String, Object> map = new HashMap<>();
		map.put("_id", "fridayAMTest");
		map.put("K-100", "Birthday Bath");
		dbClient.save(map);
		
		dbClient.shutdown();
		System.out.println("End write to couch");
	}
	
	public CouchWriter(){
		properties = new CouchDbProperties();
		properties.setHost("127.0.0.1");
		properties.setPort(5984);
		properties.setProtocol("http");
		properties.setDbName("products");
		properties.setCreateDbIfNotExist(true);

		dbClient = new CouchDbClient(properties);
	}
	
	public void writeToCouch(JSONObject data){
		
		try {
			Iterator<String> iter = data.keys();
			while(iter.hasNext()){
				String key = iter.next();
				JSONObject product = data.getJSONObject(key);				
				String itemNo = product.getString("Item_No");		
				
				////////////////////////
				//i think this is better but needs to be tested
				
				Map<String, Object> productMap = new Gson().fromJson(product.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
				productMap.put("_id", itemNo);
				
				////////////////////////
				//this is running right now - fail
				
//				JsonObject json = new JsonObject();
//				json.addProperty("_id", itemNo);
//
//				Iterator<String> attrs = product.keys();
//				while(attrs.hasNext()){
//					String attr = attrs.next();
//					json.addProperty(attr, product.getString(attr));
//				}
				
				////////////////////////
				
				getDbClient().save(productMap);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeDbConnection(){
		dbClient.shutdown();
		System.out.println("End write to couch");		
	}
	
	public CouchDbProperties getProperties() {
		return properties;
	}
	public void setProperties(CouchDbProperties properties) {
		this.properties = properties;
	}
	public CouchDbClient getDbClient() {
		return dbClient;
	}
	public void setDbClient(CouchDbClient dbClient) {
		this.dbClient = dbClient;
	}
}
