package com.thinkgem.jeesite.modules.sys.utils;

import java.io.Serializable;

public class ComboNode implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = -7939449388023831819L;

	private String value;
	private String text;
	
	private String desc;
	
	public ComboNode() {
		
	}
	
	public ComboNode(String value, String text) {
		this.value = value;
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
