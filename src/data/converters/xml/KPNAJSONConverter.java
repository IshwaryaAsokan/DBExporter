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

public class KPNAJSONConverter extends JSONConverter {
	
	private final static String EXCLUDED_FINISH_COLOR = "Indicates No Finish";

	@Override
	public JSONArray convert(JSONObject originalJson) {
		JSONArray transformedJson = new JSONArray();
		
		try {
			Iterator<String> iter = originalJson.keys();
			while(iter.hasNext()){
				String key = iter.next();
				JSONObject product = originalJson.getJSONObject(key);
				List<JSONObject> productSkus = readObject(product);

				for(JSONObject sku : productSkus){
					transformedJson.put(sku);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return transformedJson;
	}
	
	private static List<JSONObject> readObject(JSONObject json){
		List<JSONObject> products = new ArrayList<JSONObject>();
		
		try {
			String productTitleDesc = getValue(json, "$.DESCRIPTION_PRODUCT");
			String brandNameShowroom = getValue(json, "$.BRAND_NAME_SHOWROOM");
			String defaultCategory = getValue(json, "$.ATG_Default_Category");
			String narrativeDesc = getValue(json, "$.adCopy.NARRATIVE_DESCRIPTION");
			String productId = getValue(json, "$.adCopy.Item_No");
			
			JSONArray bulletPoints = new JSONArray();
			String[] bulletTitles = {"WEB_FEATURES_", "WEB_TECHNOLOGY_", "WEB_MATERIAL_", "WEB_INSTALLATION_", "WEB_REBATES_"}; 
			String[] bulletNumbers = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10"};
			for (String bTitle : bulletTitles){
				for(String bNumber : bulletNumbers){
					String bKey = bTitle + bNumber;
					String bullet = getValue(json, "$.adCopy." + bKey);
					if(bullet != null){
						bulletPoints.put(bullet);
					}
				}
			}
			
			//iterate through skus list
			List<JSONObject> skus = getArrayValue(json, "$.[skus]");
			for(JSONObject sku : skus){
				JSONObject product = new JSONObject();
				product.put("g:brand", "KOHLER");
				product.put("g:product_line", removeTmAndR(brandNameShowroom));
				product.put("g:product_type", defaultCategory);
				product.put("g:description", narrativeDesc);
				product.put("g:bullet_point", bulletPoints);
				product.put("g:item_group_id", productId);
				
				String skuCode = "K-" + getValue(sku, "$.Item_No"); 
				product.put("g:id", skuCode);
				product.put("g:mpn", skuCode);

				product.put("g:suggested_retail_price", getValue(sku, "$.LIST_PRICE"));
				product.put("g:gtin", "00" + getValue(sku, "$.UPC_Code"));
				
				String colorFinish = getValue(sku, "$.COLOR_FINISH_NAME");
				if(StringUtils.isNotEmpty(colorFinish) && !colorFinish.equalsIgnoreCase(EXCLUDED_FINISH_COLOR)){
						product.put("g:color", colorFinish);
				}

				String imgItemIso = getValue(sku, "$.IMG_ITEM_ISO");
				if(StringUtils.isNotEmpty(imgItemIso)){
					String params = "$gradient_src=PAWEB%2Forganic-gradient&$product_src=is{PAWEB%2F" + imgItemIso + "}&wid=2800";
					URI uri = new URI(
						"http",
						"s7d4.scene7.com",
						"/is/image/PAWEB/EH_GS_1600",
						params,
						null
					);
					product.put("g:image_link", uri.toASCIIString());
				}
				
				String productTitle = "KOHLER " + skuCode + " " + productTitleDesc;
				if(StringUtils.isNotEmpty(colorFinish) && !colorFinish.equalsIgnoreCase("Not Applicable") && !colorFinish.equalsIgnoreCase("null")){
					productTitle = productTitle + " - " + colorFinish;
				}
				product.put("g:title", productTitle);
				
				products.add(product);
				System.out.println(product);
			}
		} catch (JSONException e) {
			System.out.println("Error creating product.");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return products;
	}

	@Override
	public String getStartXmlWrapper() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\" xmlns:g=\"http://base.google.com/ns/1.0\"><channel>";
	}

	@Override
	public String getEndXmlWrapper() {
		return "</channel></rss>";
	}

	@Override
	public List<Pair<String, String>> getReplacements() {
		List<Pair<String, String>> replacements = new ArrayList<Pair<String, String>>();
		replacements.add(Pair.of("<array>", "<item>"));
		replacements.add(Pair.of("</array>", "</item>"));
		replacements.add(Pair.of("&lt;", "<"));
		replacements.add(Pair.of("&gt;", ">"));
		return replacements;
	}

	private static String removeTmAndR(String text){
		if(text == null || text.equalsIgnoreCase("No Brand")){
			return StringUtils.EMPTY;
		}
		else {
			return text.replace("(R)", "").replace("(TM)", "");	
		}		
	}	
}
