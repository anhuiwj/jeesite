/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.utils;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.sys.dao.*;
import com.thinkgem.jeesite.modules.sys.entity.*;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm.Principal;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.mybatis.spring.DataSource;
import org.mybatis.spring.DataSourceContextHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户工具类
 * @author ThinkGem
 * @version 2013-12-05
 */
public class UserUtils {

	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
	private static AreaDao areaDao = SpringContextHolder.getBean(AreaDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);

	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";
	public static final String CACHE_USER = "cacheUser";

	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_AREA_LIST = "areaList";
	public static final String CACHE_OFFICE_LIST = "officeList";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";
	
	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	@DataSource(name=DataSource.fy)
	public static User get(String id){
		User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user ==  null){
			user = userDao.get(id);
			if (user == null){
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			
			Menu m = new Menu();
			m.setUserId(id);
			
			
			List<Menu> menuList = menuDao.findRoleMenuByUserId(m);
			Map<String,String> map = Maps.newHashMap();
			if(menuList != null && menuList.size()>0) {
				for(Menu mt:menuList) {
					map.put(mt.getId(), null);
				}
			}
			
			List<Menu> mList = menuDao.findUserMenuList(m);
			if(mList != null && mList.size()>0) {
				for(Menu mt:mList) {
					if(!map.containsKey(mt.getId())) {
						menuList.add(mt);
					}
				}
			}
			if(menuList != null && menuList.size()>0) {
				Collections.sort(menuList);
			}
			
			user.setMenuList(menuList);
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	@DataSource(name = DataSource.fy)
	public static User getByLoginName(String loginName){
		User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null){
			user = userDao.getByLoginName(new User(null, loginName));
			if (user == null){
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			
			Menu m = new Menu();
			m.setUserId(user.getId());
			
			List<Menu> menuList = menuDao.findRoleMenuByUserId(m);
			Map<String,String> map = Maps.newHashMap();
			if(menuList != null && menuList.size()>0) {
				for(Menu mt:menuList) {
					map.put(mt.getId(), null);
				}
			}
			
			List<Menu> mList = menuDao.findUserMenuList(m);
			if(mList != null && mList.size()>0) {
				for(Menu mt:mList) {
					if(!map.containsKey(mt.getId())) {
						menuList.add(mt);
					}
				}
			}
			if(menuList != null && menuList.size()>0) {
				Collections.sort(menuList);
			}
			
			user.setMenuList(menuList);
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache(){
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_MENU_LIST);
		removeCache(CACHE_AREA_LIST);
		removeCache(CACHE_OFFICE_LIST);
		removeCache(CACHE_OFFICE_ALL_LIST);
		UserUtils.clearCache(getUser());
	}
	
	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(User user){
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
		if (user.getOffice() != null && user.getOffice().getId() != null){
			CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
		}
	}
	
	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getUser(){
		Principal principal = getPrincipal();
		if (principal!=null){
			User user = get(principal.getId());
			if (user != null){
				return user;
			}
			return new User();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User();
	}

	/**
	 * 根据用户ID获取工号
	 * @param userId 用户Id
	 * @return 用户工号
	 */
	public static String getWorkNo(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        //查询用户
		User user = UserUtils.get(userId);
		if(user != null){
			return user.getNo();
		}
		return null;
	}

	/**
	 * 获取当前用户角色列表
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public static List<Role> getRoleList(){
		List<Role> roleList = (List<Role>)getCache(CACHE_ROLE_LIST);
		if (roleList == null){
			User user = getUser();
			if (user.isAdmin()){
				roleList = roleDao.findAllList(new Role());
			}else{
				Role role = new Role(user);
				//role.getSqlMap().put("dsf", BaseService.dataScopeFilter(user.getCurrentUser(), "o", "u"));
				roleList = roleDao.findList(role);
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}
	
	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public static List<Menu> getMenuList(){
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			User user = getUser();
			if (user.isAdmin()){
				menuList = menuDao.findAllList(new Menu());
			}else{
				Menu m = new Menu();
				m.setUserId(user.getId());
				menuList = menuDao.findRoleMenuByUserId(m);
				Map<String,String> map = Maps.newHashMap();
				if(menuList != null && menuList.size()>0) {
					for(Menu mt:menuList) {
						map.put(mt.getId(), null);
					}
				}
				
				List<Menu> mList = menuDao.findUserMenuList(m);
				if(mList != null && mList.size()>0) {
					for(Menu mt:mList) {
						if(!map.containsKey(mt.getId())) {
							menuList.add(mt);
						}
					}
				}
				if(menuList != null && menuList.size()>0) {
					Collections.sort(menuList);
				}
				
				
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}
	
	/**
	 * 获取当前用户授权的区域
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public static List<Area> getAreaList(){
		List<Area> areaList = (List<Area>)getCache(CACHE_AREA_LIST);
		if (areaList == null){
			areaList = areaDao.findAllList(new Area());
			putCache(CACHE_AREA_LIST, areaList);
		}
		return areaList;
	}
	
	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public static List<Office> getOfficeList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
		if (officeList == null){
			User user = getUser();
			if (user.isAdmin()){
				officeList = officeDao.findAllList(new Office());
			}else{
				Office office = new Office();
				//office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
				officeList = officeDao.findList(office);
			}
			putCache(CACHE_OFFICE_LIST, officeList);
		}
		return officeList;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public static List<Office> getOfficeAllList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_ALL_LIST);
		if (officeList == null){
			officeList = officeDao.findAllList(new Office());
		}
		return officeList;
	}
	
	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject(){
		return SecurityUtils.getSubject();
	}
	
	/**
	 * 获取当前登录者对象
	 */
	public static Principal getPrincipal(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			if (principal != null){
				return principal;
			}
//			subject.logout();
		}catch (UnavailableSecurityManagerException e) {
			
		}catch (InvalidSessionException e){
			
		}
		return null;
	}

	/**
	 * 获取当前登录用户的所有信息 no(工号) name(昵称) jgType(机构类型sfy-qfy-zcjg-sq) jgCode(机构编码)
	 * jgName(机构名称) qxCode(所属区县编码) qxName(所属区县名称) jgGrade（2 二级，3三级）(机构级别)
	 */
	public Map<String, String> getOnline() {
		Map<String, String> m = new HashMap<String, String>();
		// 登录数据
		User u = getUser();
		m.put("no", u.getId());
		m.put("name", u.getName());
		m.put("loginName", u.getLoginName());


		// 机构数据
		Office o = u.getCompany();
		m.put("jgCode", o.getCode());
		m.put("jgName", o.getName());

		List<Role> rList = u.getRoleList();
		Map<String, String> rMap = Maps.newHashMap();
		for (Role r : rList) {
			rMap.put(r.getEnname(), null);
		}

		if (rMap.containsKey("fyzx")) {
			m.put("jgType", "sfy");
		} else if (rMap.containsKey("qxfy")) {
			m.put("jgType", "qfy");
		} else if (rMap.containsKey("zcjg")) {
			m.put("jgType", "zcjg");
		} else if (rMap.containsKey("sqzx")) {
			m.put("jgType", "sq");
		} else if (rMap.containsKey("ptyy")) {
			m.put("jgType", "ptyy");
		} else if (rMap.containsKey("zdzx")) {
			m.put("jgType", "zdzx");
		} else if (rMap.containsKey("SCZX")) {
			m.put("jgType", "sczx");
		} else if (rMap.containsKey("fkyy")) {
			m.put("jgType", "fkyy");
		}

		if ("3".equals(o.getGrade())) {
			m.put("jgGrade", "3");
		} else {
			if ("2".equals(o.getGrade())) {
				m.put("jgGrade", "2");
			} else {
				m.put("jgGrade", "");
			}
		}

		// 所属区县
		m.put("qxCode", o.getArea().getCode());
		m.put("qxName", o.getArea().getName());
		return m;
	}
	
	public static Session getSession(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
//			subject.logout();
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	// ============== User Cache ==============
	
	public static Object getCache(String key) {
		return getCache(key, null);
	}
	
	public static Object getCache(String key, Object defaultValue) {

		Object obj = getSession().getAttribute(key);
		return obj==null?defaultValue:obj;
	}

	public static void putCache(String key, Object value) {

		getSession().setAttribute(key, value);
	}

	public static void removeCache(String key) {

		getSession().removeAttribute(key);
	}
	
}
