package data.converters.xml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OlapicConverter extends JSONConverter{
	
	@Override
	public JSONArray convert(JSONObject originalJson) {
		JSONArray transformedJson = new JSONArray();
		
		try {
			Iterator<String> iter = originalJson.keys();
			while(iter.hasNext()){
				
				String key = iter.next();
				JSONObject category = originalJson.getJSONObject(key);
				
				List<JSONObject> products = readObjectCategories(category);
				
				//List<JSONObject> productSkus = readObjectSkus(product);
				for(JSONObject product : products){
					transformedJson.put(product);
				}
				
				/*for(JSONObject sku : productSkus){
					transformedJson.put(sku);
				}*/
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return transformedJson;
	}
	
	private static List<JSONObject> readObjectCategories(JSONObject json) {
		List<JSONObject> products = new ArrayList<JSONObject>();
		
		try {
			
			String productName = getValue(json, "$.Description_Product");
			String productId = getValue(json, "$.Item_No");
			String imageUrl = getValue(json, "$.IMG_ISO");
			String webIncluded = getValue(json, "$.Web_Included");
			JSONObject product = new JSONObject();
			if(StringUtils.isNotEmpty(imageUrl) && StringUtils.isNotEmpty(productName) 
					 && StringUtils.isNotEmpty(productId) && webIncluded.equalsIgnoreCase("Yes")){
				String params ="productNumber="+ productId;
				URI uri = new URI(
					"http",
					"www.us.kohler.com",
					"/us/catalog/productDetails.jsp",
					params,
					null
				);
				String imgParams ="$s7product$";
				URI Imguri = new URI(
					"http",
					"s7d4.scene7.com",
					"/is/image/PAWEB/"+imageUrl,
					imgParams,
					null
				);
				product.put("Name", productName);
				product.put("ProductUniqueID", productId);
				product.put("ProductUrl", uri);
				product.put("ImageUrl", Imguri);
				products.add(product);
												
			} else {

				if (StringUtils.isEmpty(productName)) {
					System.out.println("Missing Product Name for product: " + productId);
				}
				if (StringUtils.isEmpty(productId)) {
					System.out.println("Missing Product Unique Id for product: " + productId);
				}				
				if (StringUtils.isNotEmpty(imageUrl)){
					System.out.println("Missing Image Url for product: " + productId);
				}
				else {
					
					System.out.println("webIncluded is No for the product: " + productId);
				}
			}
			
		} catch (JSONException e) {
			System.out.println("Error creating product.");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
		}	
		
		return products;
	}

	@Override
	public String getStartXmlWrapper() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Feed name=\"Kohler\"><Products>";
	}

	@Override
	public String getEndXmlWrapper() {
		return "</Products></Feed>";
	}

	@Override
	public List<Pair<String, String>> getReplacements() {
		List<Pair<String, String>> replacements = new ArrayList<Pair<String, String>>();

		replacements.add(Pair.of("<array>", "<Product>"));
		replacements.add(Pair.of("</array>", "</Product>"));
		//for Google XML, the following characters must be encoded: ' " < > &
		replacements.add(Pair.of("é", "e"));
		replacements.add(Pair.of("”", "\""));
		replacements.add(Pair.of("ê", "e"));
		replacements.add(Pair.of("®", ""));
		replacements.add(Pair.of("™", "")); //trademark
		replacements.add(Pair.of("°", " degrees "));
		replacements.add(Pair.of("·", "'"));
		replacements.add(Pair.of("&apos;", "'"));
		replacements.add(Pair.of("–", "-"));
		replacements.add(Pair.of("¿", ""));
		replacements.add(Pair.of("ª", ""));
		replacements.add(Pair.of("’", "'")); //fake apostrophes
		
		return replacements;
	}
	
	
}
