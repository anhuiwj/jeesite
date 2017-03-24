/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {
	
	/**
	 * 根据登录名称查询用户
	 * @param user
	 * @return
	 */
	public User getByLoginName(User user);
	/**
	 * 根据ID称查询用户
	 * @param user
	 * @return
	 */
	public User getByLoginById(User user);
	/**
	 * 根据机构和工号查询用户
	 * @param user
	 * @returngetByNoJg
	 */
	public User getByNo(User user);
	/**
	 * 根据机构代码和工号查询用户
	 * @param user
	 * @returngetByNoJg
	 */
	public User getByNoJg(User user);

	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByOfficeId(User user);
	
	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(User user);
	
	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(User user);
	
	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(User user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(User user);
	
	/**
	 * 删除用户资源关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRes(User user);
	
	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(User user);
	
	/**
	 * 插入用户资源关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRes(User user);
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(User user);
	
	/**
	 * 查询不在角色中的用户信息
	 * @param user
	 * @return
	 */
	public List<User> findNotInRole(User user);

	public List<User> getByCompanyIds(@Param("codes") List<String> list);

	/**
	 * 根据机构code查询机构下面的用户
	 * @param jgCode
	 * @return
     */
	List<User> getOfficeByCode(String jgCode);

	/**
	 * 根据机构和用户ID查询改机构下是否具有这个人，如果有这个人就返回这个人，如果没有这个人就返回null
	 * @param jgCode 机构码
	 * @param userId 人员ID
	 * @return 这个人的实体
	 */
	User findUserByJgCodeAndUserId(String jgCode, String userId);
}
