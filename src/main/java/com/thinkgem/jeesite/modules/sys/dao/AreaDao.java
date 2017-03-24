/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.TreeDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;

import java.util.List;


/**
 * 区域DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {
	
	public List<Area> findByParentCode(Area area);

	public Area getAreaByCode(Area paramsArea);
	
	/**
	 * 按父id集合，查找所有的祖先
	 * @param parentIdArr
	 * @return
	 */
	public List<Area> findAllParent(String[] parentIdArr);
	
	public List<Area> findTopList(Area area);

	public Area queryAreaByUserId(Area params);
	
}
