package com.aurionpro.model;

public class GuitarSpec {
	private Builder builder;
	private String model;
	private Type type;
	private Wood backWood;
	private Wood topWood;

	public GuitarSpec(Builder builder, String model, Type type, Wood backWood, Wood topWood) {
		this.builder = builder;
		this.model = model;
		this.type = type;
		this.backWood = backWood;
		this.topWood = topWood;
	}

	public boolean matches(GuitarSpec otherSpec) {
		if (otherSpec.builder != null && builder != otherSpec.builder)
			return false;
		if (otherSpec.model != null && !otherSpec.model.equalsIgnoreCase(model))
			return false;
		if (otherSpec.type != null && type != otherSpec.type)
			return false;
		if (otherSpec.backWood != null && backWood != otherSpec.backWood)
			return false;
		if (otherSpec.topWood != null && topWood != otherSpec.topWood)
			return false;
		return true;
	}

	public Builder getBuilder() {
		return builder;
	}

	public String getModel() {
		return model;
	}

	public Type getType() {
		return type;
	}

	public Wood getBackWood() {
		return backWood;
	}

	public Wood getTopWood() {
		return topWood;
	}
}
