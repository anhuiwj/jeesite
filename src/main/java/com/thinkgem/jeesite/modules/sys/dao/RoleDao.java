/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.Role;

import java.util.List;

/**
 * 角色DAO接口
 * @author ThinkGem
 * @version 2013-12-05
 */
@MyBatisDao
public interface RoleDao extends CrudDao<Role> {

	public Role getByName(Role role);
	
	public Role getByEnname(Role role);

	/**
	 * 维护角色与菜单权限关系
	 * @param role
	 * @return
	 */
	public int deleteRoleMenu(Role role);

	public int insertRoleMenu(Role role);
	
	
	
	/**
	 * 查询公共角色和机构特有角色
	 * @param o
	 * @return
	 */
	public List<Role> findOrgList(Office o);
	
	public int insertRoleUser(Role role);
	
	/**
	 * 删除角色用户关联
	 * @param role
	 * @return
	 */
	public int deleteRoleUser(Role role);
	
	/**
	 * 删除角色用户关联
	 * @param role
	 * @return
	 */
	public int deleteRoleAllUser(Role role);

}
