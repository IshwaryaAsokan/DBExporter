package data.converters.xml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KPNAJSONConverter extends JSONConverter {
	
	private final static String[] EXCLUDED_COLORS = {"Indicates No Finish", "Indicates No finish", "Not Applicable", "Not Applicable "};
	private final static String EXCLUDED_BRAND = "no brand name";
	private final static String SEPARATELY = "(sold separately)";
	private final static String COORDINATES = "Coordinates with";
	private final static String EXCLUSIVELY = "Available exclusively";

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
			String productId = getValue(json, "$.Item_No");
			String productTitleDesc = getValue(json, "$.Description_Product");
			String brandNameShowroom = getValue(json, "$.Brand_Name");
			String defaultCategory = getValue(json, "$.ATG_Default_Category");
			String narrativeDesc = getValue(json, "$.adCopy.Narrative_Description");
			
			JSONArray bulletPoints = new JSONArray();
			String[] bulletTitles = {"Web_Features_", "Web_Technology_", "Web_Material_", "Web_Installation_", "Web_Rebates_"}; 
			String[] bulletNumbers = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10"};
			for (String bTitle : bulletTitles){
				for(String bNumber : bulletNumbers){
					String bKey = bTitle + bNumber;
					String bullet = getValue(json, "$.adCopy." + bKey);
					if(StringUtils.isNotBlank(bullet) 
							&& !bullet.contains("*") 
							&& !bullet.contains(SEPARATELY) 
							&& !bullet.contains(COORDINATES)
							&& !bullet.contains(EXCLUSIVELY) 
							&& bulletPoints.length() < 10
							&& !bullet.startsWith("Available at")
							&& !bullet.startsWith("RETAIL AVAILABILITY")
							&& !bullet.startsWith("Please contact your distributor")
							&& StringUtils.countMatches(bullet, ".") < 2){
						bulletPoints.put(bullet);
					}
				}
			}
			
			//iterate through skus list
			List<JSONObject> skus = getArrayValue(json, "$.[skus]");
			for(JSONObject sku : skus){
				JSONObject product = new JSONObject();
				String imgItemIso = getValue(sku, "$.IMG_ITEM_ISO");
				String upcCode = getValue(sku, "$.UPC_Code");
				String skuCode = "K-" + getValue(sku, "$.Item_No");
				String listPrice = getValue(sku, "$.List_Price");
				
				if(StringUtils.isNotEmpty(imgItemIso) && StringUtils.isNotEmpty(upcCode) 
						&& StringUtils.isNotEmpty(listPrice) && !listPrice.equalsIgnoreCase("Note *")){
					String params = "$gradient_src=PAWEB%2Forganic-gradient&$product_src=is{PAWEB%2F" + imgItemIso + "}&wid=2800";
					URI uri = new URI(
						"http",
						"s7d4.scene7.com",
						"/is/image/PAWEB/EH_GS_1600",
						params,
						null
					);
					product.put("g:image_link", uri.toASCIIString());
					product.put("g:brand", "KOHLER");
					product.put("g:product_line", removeTmAndR(brandNameShowroom));
					product.put("g:product_type", defaultCategory); 
					product.put("g:description", narrativeDesc);
					product.put("g:bullet_point", bulletPoints);
					product.put("g:item_group_id", productId);
					product.put("g:id", skuCode);
					product.put("g:mpn", skuCode);
					product.put("g:suggested_retail_price", listPrice);
					
					String colorFinish = getValue(sku, "$.Color_Finish_Name");
					if(StringUtils.isNotEmpty(colorFinish) && !Arrays.asList(EXCLUDED_COLORS).contains(colorFinish)){
							product.put("g:color", colorFinish);
					}
					else {
						colorFinish = StringUtils.EMPTY;
					}
					
					String productTitle = "KOHLER " + skuCode + " " + productTitleDesc;
					if(StringUtils.isNotEmpty(colorFinish)){
						productTitle = productTitle + " - " + colorFinish;
					}

					product.put("g:title", productTitle); //yes, but null color					
					product.put("g:gtin", "00" + upcCode);
					
					products.add(product);
				} //end: if(StringUtils.isNotEmpty(imgItemIso) && StringUtils.isNotEmpty(upcCode))
				else {
					if(StringUtils.isEmpty(upcCode)){
						System.out.println("Missing UPC Code for product: " + skuCode);
					}
					if(StringUtils.isEmpty(imgItemIso)){
						System.out.println("Missing Image ISO for product: " + skuCode);
					}
					if(StringUtils.isEmpty(listPrice)){
						System.out.println("Missing List Price for product: " + skuCode);
					}
					if(listPrice.equalsIgnoreCase("Note *")){
						System.out.println("List price of 'Note *' for product: " + skuCode);
					}
				}
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
		//for Google XML, the following characters must be encoded: ' " < > &
		return replacements;
	}

	private static String removeTmAndR(String text){
		if(text == null || text.equalsIgnoreCase(EXCLUDED_BRAND)){
			return StringUtils.EMPTY;
		}
		else {
			return text.replace("(R)", "").replace("(TM)", "");	
		}		
	}	
}
