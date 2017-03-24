/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Menu;

import java.util.List;

/**
 * 菜单DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface MenuDao extends CrudDao<Menu> {

	public List<Menu> findByParentIdsLike(Menu menu);
	
	public List<Menu> findByParentId(Menu menu);

	/**
	 * 查询用户通过角色拥有的资源
	 * @param menu
	 * @return
	 */
	public List<Menu> findRoleMenuByUserId(Menu menu);
	
	/**
	 * 查询用户关联的资源
	 * @param menu
	 * @return
	 */
	public List<Menu> findUserMenuList(Menu menu);
	
	/**
	 * 查询角色关联的资源
	 * @param menu
	 * @return
	 */
	public List<Menu> findRoleMenuList(Menu menu);
	
	
	
	public int updateParentIds(Menu menu);
	
	public List<Menu> findTop(Menu menu);
	
	/**
	 * 查询业务菜单
	 * @param menu
	 * @return
	 */
	public List<Menu> findNonSysList(Menu menu);
	
	/**
	 * 删除用户菜单关联关系
	 * @param menu
	 * @return
	 */
	public int deleteUserMenu(Menu menu);
	
	/**
	 * 删除角色菜单关联关系
	 * @param menu
	 * @return
	 */
	public int deleteRoleMenu(Menu menu);
	
	
	
}
