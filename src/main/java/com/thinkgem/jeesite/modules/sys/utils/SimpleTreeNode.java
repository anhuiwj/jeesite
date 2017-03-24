package com.thinkgem.jeesite.modules.sys.utils;

import java.util.List;

public class SimpleTreeNode {
	
	private String id;
	private String text;
	
	
	private List<SimpleTreeNode> children;
	
	/**
	 * 是否选中
	 */
	protected boolean checked;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public List<SimpleTreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<SimpleTreeNode> children) {
		this.children = children;
	}
	
	

}
