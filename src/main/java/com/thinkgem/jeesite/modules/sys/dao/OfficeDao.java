/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.TreeDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 机构DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface OfficeDao extends TreeDao<Office> {
	
	public Office getByCode(Office o);
	public Office getOfficeByCode(String code);
	/**
	 * 获取助产机构
	 * */
	public List<Office> findByZcjg();
	
	/**
	 * 查询顶级机构
	 * @return
	 */
	public List<Office> findTop(Office o);
	
	/**
	 * 查询子机构
	 * @param pid 父id
	 * @return
	 */
	public List<Office> findChilds(Office o);
	
	/**
	 * 查询所有子机构（包含孙机构）
	 * @param o
	 * @return
	 */
	public List<Office> findAllChilds(Office o);
	
	/**
	 * 查询自身和所有子机构（包含孙机构）
	 * @param o
	 * @return
	 */
	public List<Office> findWithAllChilds(Office o);

	public List<Office> findByQxcode(@Param("parentCode") String parentCode);

	public List<Office> findJg(@Param("name") String name);
	
	public List<Office> getMessageOrg(Office params);

	public List<Office> getOfficeByType(String jgType);

	public List<Office> getSq(@Param("code") String code);


}
