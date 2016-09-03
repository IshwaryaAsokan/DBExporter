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

public class KPNAJSONConverterForOlapic extends JSONConverter{
	private final static String[] EXCLUDED_COLORS = {"Indicates No Finish", "Indicates No finish", "Not Applicable", "Not Applicable "};
	private final static String EXCLUDED_BRAND = "no brand name";
	private final static String[] EXCLUDED_BULLET_TEXT = 
		{
				"(sold separately)", 
				"Coordinates with", 
				"Available exclusively", 
				"Available at",
				"RETAIL AVAILABILITY",
				"Please contact your distributor",
				"*",
				"AVAILABLE ONLY IN CANADA",
				"Home Center availability",
				"Low flow aerator",
				"Low-flow aerator",
				"The Oil-Rubbed Bronze finish on this product does not include copper highlights as shown.",
				"Mounting bracket (shown above) sold separately and available in four finishes."
		};
	private final static String[] EXCLUDED_BULLET_START_TEXT = 
		{
				"Oil-Rubbed Bronze finish"
		};

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
		System.out.println("transformedJson:"+transformedJson);
		
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
				
				String Imgparams = imageUrl + "?$s7product$";
				URI Imguri = new URI(
					"http",
					"s7d4.scene7.com",
					"/is/image/PAWEB/",
					Imgparams,
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
				else {
					System.out.println("Missing Image Url for product: " + productId);
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

	/*private static List<JSONObject> readObjectSkus(JSONObject json){
		
		List<JSONObject> products = new ArrayList<JSONObject>();
		
		try {
			String productId = getValue(json, "$.Item_No");
			String productUrl = getValue(json, "$.Product_URL");
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
							&& bulletPoints.length() < 10
							&& !bulletContainsExcludedText(bullet)
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
				
				if(StringUtils.isNotEmpty(imgItemIso) && StringUtils.isNotEmpty(productId) 
						&& StringUtils.isNotEmpty(productUrl) && !listPrice.equalsIgnoreCase("Note *")){
					String params = "$gradient_src=PAWEB%2Forganic-gradient&$product_src=is{PAWEB%2F" + imgItemIso + "}&wid=2800";
					URI uri = new URI(
						"http",
						"s7d4.scene7.com",
						"/is/image/PAWEB/EH_GS_1600",
						params,
						null
					);
					product.put("ProductName", productId);
					product.put("ProductUrl", productUrl);
					product.put("ImageUrl", uri.toASCIIString());
					//product.put("g:brand", "KOHLER");
					//product.put("g:product_line", removeTmAndR(brandNameShowroom));
					//product.put("g:product_type", defaultCategory); 
					product.put("Description", productTitleDesc);
					//product.put("g:bullet_point", bulletPoints);
										//product.put("g:id", skuCode);
					//product.put("g:mpn", skuCode);
					product.put("Price", listPrice);
					
					String colorFinish = getValue(sku, "$.Color_Finish_Name");
					if(StringUtils.isNotEmpty(colorFinish) && !Arrays.asList(EXCLUDED_COLORS).contains(colorFinish)){
							product.put("Color", colorFinish);
					}
					else {
						colorFinish = StringUtils.EMPTY;
					}
					
					String productTitle = "KOHLER " + skuCode + " " + productTitleDesc;
					if(StringUtils.isNotEmpty(colorFinish)){
						productTitle = productTitle + " - " + colorFinish;
					}

					//product.put("g:title", productTitle); //yes, but null color					
					product.put("UPC", upcCode);
					
					products.add(product);
				} //end: if(StringUtils.isNotEmpty(imgItemIso) && StringUtils.isNotEmpty(upcCode))
				else {
					if(StringUtils.isEmpty(productUrl)){
						System.out.println("Missing Product URL for product: " + skuCode);
					}
					if(StringUtils.isEmpty(imgItemIso)){
						System.out.println("Missing Image ISO for product: " + skuCode);
					}
					if(StringUtils.isEmpty(listPrice)){
						System.out.println("Missing Product Name for product: " + skuCode);
					}
					else if(listPrice.equalsIgnoreCase("Note *")){
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
*/
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
		replacements.add(Pair.of("�", "e"));
		replacements.add(Pair.of("�", "\""));
		replacements.add(Pair.of("�", "e"));
		replacements.add(Pair.of("�", ""));
		replacements.add(Pair.of("�", "")); //trademark
		replacements.add(Pair.of("�", " degrees "));
		replacements.add(Pair.of("�", "'"));
		replacements.add(Pair.of("&apos;", "'"));
		replacements.add(Pair.of("�", "-"));
		replacements.add(Pair.of("�", ""));
		replacements.add(Pair.of("�", ""));
		replacements.add(Pair.of("�", "'")); //fake apostrophes
		
		return replacements;
	}
	
	/*private static String removeTmAndR(String text){
		if(text == null || text.equalsIgnoreCase(EXCLUDED_BRAND)){
			return StringUtils.EMPTY;
		}
		else {
			return text.replace("(R)", "").replace("(TM)", "");	
		}		
	}
	
	private static boolean bulletContainsExcludedText(String bullet){
		for(String badText : EXCLUDED_BULLET_TEXT){
			if(bullet.contains(badText)){
				return true;
			}
		}
		
		for(String badStartText : EXCLUDED_BULLET_START_TEXT){
			if(bullet.startsWith(badStartText)){
				return true;				
			}
		}
		
		return false;
	}
*/

}
