/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thinkgem.jeesite.common.persistence.TreeEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 菜单Entity
 * @author ThinkGem
 * @version 2013-05-15
 */
public class Menu extends TreeEntity<Menu> implements Comparable {

	private static final long serialVersionUID = 1L;
	private String href; 	// 链接
	private String target; 	// 目标（ mainFrame、_blank、_self、_parent、_top）
	private String icon; 	// 图标
	private String type; 	// 是否在菜单中显示（1：菜单；2：按钮）
	private String permission; // 权限标识
	
	private String sysflag;//系统资源标识（0：系统级；1：业务级）
	
	private String userId;//非持久
	
	private String roleId;//非持久
	
	private String parentName;//父机构名称,非持久
	
	
	public Menu getParent() {
		return parent;
	}


	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public Menu(){
		super();
	}
	
	public Menu(String id){
		super(id);
	}
	
	
	
	

	@Length(min=0, max=2000)
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Length(min=0, max=20)
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	@Length(min=0, max=100)
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Length(min=0, max=200)
	public String getPermission() {
		return permission;
	}

	@Length(min=1, max=1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}

	

	@JsonIgnore
	public static String getRootId(){
		return "1";
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	

	public String getRoleId() {
		return roleId;
	}


	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}


	public String getSysflag() {
		return sysflag;
	}


	public void setSysflag(String sysflag) {
		this.sysflag = sysflag;
	}
	
	


	public String getParentName() {
		return parentName;
	}


	public void setParentName(String parentName) {
		this.parentName = parentName;
	}


	@Override
	public String toString() {
		return name;
	}
	
	public String getTypeName() {
		if("1".equals(type)) {
			return "菜单";
		}
		if("2".equals(type)) {
			return "按钮";
		}
		return "";
	}
	
	public String getSysflagName() {
		if("0".equals(sysflag)) {
			return "系统级";
		}
		if("1".equals(sysflag)) {
			return "业务级";
		}
		return "";
	}


	@Override
	public int compareTo(Object other) {
		Menu m =(Menu)other;
		return this.sort-m.sort;
	}
	
	
	
}