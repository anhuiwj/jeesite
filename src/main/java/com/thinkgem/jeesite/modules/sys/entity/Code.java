package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.persistence.TreeEntity;

import java.util.List;

public class Code extends TreeEntity<Code> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 294303616586768373L;
	
	public static final String PATH_SPERATOR = "->";
	
	private String value; 	//编码
	private Long comCode;//组合码
	private String description; 	//辅助描述
	
	private List<Code> childs;
	
	private String nodePath;

	public Code() {
		super();
	}
	
	
	public Code(String id) {
		super(id);
	}

	@Override
	public Code getParent() {
		return parent;
	}

	@Override
	public void setParent(Code parent) {
		this.parent = parent;
		
	}

	public String getValue() {
		return value;
	}

	public void setValue(String code) {
		this.value = code;
	}

	public Long getComCode() {
		return comCode;
	}

	public void setComCode(Long comCode) {
		this.comCode = comCode;
	}


	public List<Code> getChilds() {
		return childs;
	}
	public void setChilds(List<Code> childs) {
		this.childs = childs;
	}

	public String getNodePath() {
		return nodePath;
	}
	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
