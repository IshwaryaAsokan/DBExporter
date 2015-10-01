package data.alteration.values;

import definitions.enums.JsonTransformationType;

public class Transformation {
	private JsonTransformationType transformation; 
	private String key; 
	private String value;
	
	public Transformation(JsonTransformationType transformation, String key, String value){
		setTransformation(transformation);
		setKey(key);
		setValue(value);
	}
	
	public JsonTransformationType getTransformation() {
		return transformation;
	}
	public void setTransformation(JsonTransformationType transformation) {
		this.transformation = transformation;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
