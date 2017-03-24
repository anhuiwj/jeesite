/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;


import com.thinkgem.jeesite.common.persistence.TreeEntity;
import org.hibernate.validator.constraints.Length;


/**
 * 机构Entity
 * @author ThinkGem
 * @version 2013-05-15
 */
public class Office extends TreeEntity<Office> {

	private static final long serialVersionUID = 1L;

	private Area area;		// 归属区域
	private String code; 	// 机构编码

	private String type; 	// 机构类型（1：虚拟节点；2：卫计委；3：妇幼中心；4：医院；5：社区；6：部门）
	private String grade; 	// 机构分级（0：无极别；1：一级；2：二级；3：三级）
	private String lv;//机构分等（1：甲等；2：乙等；3：丙等）
	private String cszx;//是否产筛中心（0：是；1：否）
	private String zdzx;//是否诊断中心（0：是；1：否）
	private String zcjg;//是否助产机构（0：是；1：否）
	private String address; // 联系地址
	private String zipCode; // 邮政编码
	private String master; 	// 负责人
	private String phone; 	// 电话
	private String fax; 	// 传真
	private String email; 	// 邮箱
	
	private String parentName;//父机构名称,非持久
	
	
	//业务字段
	private String text;
	
	
	public Office(){
		super();
	}

	public Office(String id){
		super(id);
	}
	
	public Office getParent() {
		return parent;
	}

	public void setParent(Office parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=1, max=1)
	public String getGrade() {
		return grade;
	}

	
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	
	@Length(min=1, max=1)
	public String getLv() {
		return lv;
	}

	public void setLv(String lv) {
		this.lv = lv;
	}
	
	
	@Length(min=1, max=1)
	public String getCszx() {
		return cszx;
	}

	public void setCszx(String cszx) {
		this.cszx = cszx;
	}

	@Length(min=1, max=1)
	public String getZdzx() {
		return zdzx;
	}

	public void setZdzx(String zdzx) {
		this.zdzx = zdzx;
	}
	
	
	@Length(min=1, max=1)
	public String getZcjg() {
		return zcjg;
	}

	public void setZcjg(String zcjg) {
		this.zcjg = zcjg;
	}

	@Length(min=0, max=255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(min=0, max=100)
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Length(min=0, max=100)
	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	@Length(min=0, max=200)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min=0, max=200)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Length(min=0, max=200)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Length(min=0, max=100)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	

	
	
	
	
	public String getTypeName() {
		
		if("1".equals(type)) {
			return "虚拟节点";
		}
		if("2".equals(type)) {
			return "卫计委";
		}
		if("3".equals(type)) {
			return "妇幼中心(所)";
		}
		if("4".equals(type)) {
			return "医院";
		}
		if("5".equals(type)) {
			return "社区";
		}
		if("6".equals(type)) {
			return "部门";
		}
		return "";
	}

	

	public String getGradeName() {
		if("0".equals(grade)) {
			return "无级别";
		}
		if("1".equals(grade)) {
			return "一级";
		}
		if("2".equals(grade)) {
			return "二级";
		}
		if("3".equals(grade)) {
			return "三级";
		}
		return "";
	}

	

	public String getLvName() {
		if("1".equals(lv)) {
			return "甲等";
		}
		if("2".equals(lv)) {
			return "乙等";
		}
		if("3".equals(lv)) {
			return "丙等";
		}
		return "";
	}

	

	@Override
	public String toString() {
		return name;
	}

	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}