/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.MiniPage;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.security.Digests;
import com.thinkgem.jeesite.common.security.shiro.session.SessionDAO;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.dao.MenuDao;
import com.thinkgem.jeesite.modules.sys.dao.RoleDao;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 * @author ThinkGem
 * @version 2013-12-05
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService implements InitializingBean {
	
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private SessionDAO sessionDao;
//	@Autowired
//	private SysFyUserService sysFyUserService;
	@Autowired
	private OfficeService officeService;
	
	
	public SessionDAO getSessionDao() {
		return sessionDao;
	}


	//-- User Service --//
	
	/**
	 * 获取用户
	 * @param id
	 * @return
	 */
	public User getUser(String id) {
		return UserUtils.get(id);
	}

	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName) {
		return UserUtils.getByLoginName(loginName);
	}
	
	/**
	 * 根据机构和工号查询用户
	 * @param orgId
	 * @param no
	 * @return
	 */
	public User getUserByNo(String orgId,String no) {
		Office o = new Office(orgId);
		User u = new User();
		u.setNo(no);
		u.setCompany(o);
		return userDao.getByNo(u);
		
	}
	
	public Page<User> findUser(Page<User> page, User user) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		user.getSqlMap().put("dsf", dataScopeFilter(user.getCurrentUser(), "o", "a"));
		// 设置分页参数
		user.setPage(page);
		// 执行分页查询
		page.setList(userDao.findList(user));
		return page;
	}
	
	/**
	 * 无分页查询人员列表
	 * @param user
	 * @return
	 */
	public List<User> findUser(User user){
		List<User> list = userDao.findList(user);
		return list;
	}
	
	/**
	 * 查询不在角色中的用户信息
	 * @param user
	 * @return
	 */
	public List<User> findNotInRoleUser(User user) {
		return userDao.findNotInRole(user);
	}
	
	public MiniPage pageQueryUser(MiniPage mp, User user) {
		Page<User> page = new Page<User>();
		page.setPageNo(mp.getPageNo()+1);
		page.setPageSize(mp.getPageSize());
		user.setPage(page);
		User cUser = UserUtils.getUser();
		if(!cUser.isAdmin()) {
			User currUser = UserUtils.getUser();
			List<Role> roleList = currUser.getRoleList();
			if(roleList != null && roleList.size()>0){
				for (Role role : roleList){
					if(role != null && StringUtils.isNotBlank(role.getEnname())){
						if(role.getEnname().equals("dept")){
							//系统管理员
							user.setUserType("dept");
						}else if(role.getEnname().equals("AREA_MANAGE")){
							//区县管理员--只能查询到当前自己区县内的用户
							user.setUserType("AREA_MANAGE");
							user.setAreaCode(currUser.getCompany().getArea().getCode());
						}else if(role.getEnname().equals("ORG_MANAGE")){
							//机构管理员 --只能查询当前自己机构的用户
							user.setOrgCode(currUser.getCompany().getCode());
							user.setUserType("ORG_MANAGE");
						}
						break;
					}
				}
			}
		}else{
			user.setUserType("");
		}

		List<User> list = userDao.findList(user);
		mp.setTotal(page.getCount());
		mp.setData(list);
		
		return mp;
	}

	
	
	@Transactional(readOnly = false)
	public void saveUser(User user) {
		if (StringUtils.isBlank(user.getId())){
			user.preInsert();
			userDao.insert(user);
		}else{
			// 更新用户数据
			user.preUpdate();
			userDao.update(user);
			if(StringUtils.isNotBlank(user.getPassword())) {
				userDao.updatePasswordById(user);
			}
		}
		if (StringUtils.isNotBlank(user.getId())){
			// 更新用户与角色关联
			userDao.deleteUserRole(user);
			if (user.getRoleList() != null && user.getRoleList().size() > 0){
				userDao.insertUserRole(user);
			}
			
			//更新用户与受控资源关联
			userDao.deleteUserRes(user);
			if (user.getMenuList() != null && user.getMenuList().size() > 0){
				userDao.insertUserRes(user);
			}
			// 清除用户缓存
			UserUtils.clearCache(user);
			
			//保存或者更新用户交换表
			//sysFyUserService.saveOrUpdateFyUser(user);
		}
	}
	
	@Transactional(readOnly = false)
	public void updateUserInfo(User user) {
		user.preUpdate();
		userDao.updateUserInfo(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
	}
	
	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		userDao.delete(user);
		userDao.deleteUserRes(user);
		userDao.deleteUserRole(user);
		
		// 清除用户缓存
		UserUtils.clearCache(user);
		
		//sysFyUserService.deleteFyUser(user);
	}
	
	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		User user = new User(id);
		user.setPassword(entryptPassword(newPassword));
		userDao.updatePasswordById(user);
		// 清除用户缓存
		user.setLoginName(loginName);
		UserUtils.clearCache(user);
	}
	
	@Transactional(readOnly = false)
	public void updateUserLoginInfo(User user) {
		// 保存上次登录信息
		user.setOldLoginIp(user.getLoginIp());
		user.setOldLoginDate(user.getLoginDate());
		// 更新本次登录信息
		user.setLoginIp(UserUtils.getSession().getHost());
		user.setLoginDate(new Date());
		userDao.updateLoginInfo(user);
	}
	
	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+ Encodes.encodeHex(hashPassword);
	}
	
	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+ Encodes.encodeHex(hashPassword));
	}
	
	/**
	 * 获得活动会话
	 * @return
	 */
	public Collection<Session> getActiveSessions(){
		return sessionDao.getActiveSessions(false);
	}
	
	//-- Role Service --//
	
	public Role getRole(String id) {
		return roleDao.get(id);
	}

	public Role getRoleByName(String name) {
		Role r = new Role();
		r.setName(name);
		return roleDao.getByName(r);
	}
	
	public Role getRoleByEnname(String enname) {
		Role r = new Role();
		r.setEnname(enname);
		return roleDao.getByEnname(r);
	}
	
	public List<Role> findRole(Role role){
		return roleDao.findList(role);
	}
	
	public List<Role> findAllRole(){
		return UserUtils.getRoleList();
	}
	
	public List<Role> findRoleForAdmin(){
		List<Role> result = null;
		//User user = UserUtils.getUser();
		//if(user.isAdmin()) {
			result = roleDao.findAllList(new Role());
		/*} else {
			Office o = new Office(user.getCompany().getId());
			result = roleDao.findOrgList(o);
		}*/
		
		return result;
	}
	
	
	
	@Transactional(readOnly = false)
	public void saveRole(Role role) {
		if (StringUtils.isBlank(role.getId())){
			role.preInsert();
			roleDao.insert(role);
			
		}else{
			role.preUpdate();
			roleDao.update(role);
		}
		// 更新角色与菜单关联
		roleDao.deleteRoleMenu(role);
		List<Menu> mList = role.getMenuList();
		if (mList != null && mList.size()>0){
			roleDao.insertRoleMenu(role);
		}
		
		// 清除用户角色缓存
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);

		
		//清除用户缓存
		CacheUtils.removeAll(UserUtils.USER_CACHE);
	}

	@Transactional(readOnly = false)
	public void deleteRole(Role role) {
		roleDao.delete(role);
		roleDao.deleteRoleMenu(role);
		roleDao.deleteRoleAllUser(role);
		// 清除用户角色缓存
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
		//清除用户缓存
		CacheUtils.removeAll(UserUtils.USER_CACHE);
		
	}
	
	@Transactional(readOnly = false)
	public void deleteRoleUser(Role role) {
		roleDao.deleteRoleUser(role);
		// 清除用户角色缓存
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
		//清除用户缓存
		CacheUtils.removeAll(UserUtils.USER_CACHE);
	}
	
	@Transactional(readOnly = false)
	public void assignUserToRole(Role role) {
		roleDao.insertRoleUser(role);
		// 清除用户角色缓存
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
		//清除用户缓存
		CacheUtils.removeAll(UserUtils.USER_CACHE);
	}

	//-- Menu Service --//
	
	public Menu getMenu(String id) {
		return menuDao.get(id);
	}

	public List<Menu> findAllMenu(){
		return UserUtils.getMenuList();
	}
	
	public List<Menu> findAllMenuForAdmin(){
		return menuDao.findAllList(new Menu());
	}
	
	public List<Menu> findTopMenu() {
		Menu m = new Menu();
		return menuDao.findTop(m);
	}
	
	public List<Menu> findMenuByParentId(String mid) {
		Menu p = new Menu(mid);
		Menu qr = new Menu();
		qr.setParent(p);
		return menuDao.findByParentId(qr);
	}
	
	/**
	 * 查询用户直接拥有的资源
	 * @param userId
	 * @return
	 */
	public List<Menu> findUserMenu(String userId) {
		Menu m = new Menu();
		m.setUserId(userId);
		return menuDao.findUserMenuList(m);
	}
	
	/**
	 * 查询角色拥有的资源
	 * @param
	 * @return
	 */
	public List<Menu> findRoleMenu(String roleId) {
		Menu m = new Menu();
		m.setRoleId(roleId);
		return menuDao.findRoleMenuList(m);
	}
	
	/**
	 * 查询非系统菜单
	 * @return
	 */
	public List<Menu> findNonSysMenu() {
		Menu menu = new Menu();
		return menuDao.findNonSysList(menu);
	}
	
	@Transactional(readOnly = false)
	public void saveMenu(Menu menu) {
		// 如果没有设置父节点，则代表为跟节点，有则获取父节点实体
		if (menu.getParent() == null || StringUtils.isBlank(menu.getParentId())
				|| "0".equals(menu.getParentId())){
			menu.setParent(null);
		}else{
			menu.setParent(this.getMenu(menu.getParentId()));
		}
		if (menu.getParent() == null){
			Menu parent = null;
			parent = new Menu("0");		
			menu.setParent(parent);
			menu.getParent().setParentIds(StringUtils.EMPTY);
		}
				
		// 设置新的父节点串
		menu.setParentIds(menu.getParent().getParentIds()+menu.getParent().getId()+",");
		
		// 保存或更新实体
		if (StringUtils.isBlank(menu.getId())){
			menu.preInsert();
			menuDao.insert(menu);
		}else{
			menu.preUpdate();
			menuDao.update(menu);
		}
		
		
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);

		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	

	@Transactional(readOnly = false)
	public void deleteMenu(Menu menu) {
		menuDao.delete(menu);
		menuDao.deleteRoleMenu(menu);
		menuDao.deleteUserMenu(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}
	
	/**
	 * 获取Key加载信息
	 */
	public static boolean printKeyLoadMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n======================================================================\r\n");
		sb.append("\r\n    欢迎使用 "+ Global.getConfig("productName")+"  - Powered By http://www.wondersgroup.com/\r\n");
		sb.append("\r\n======================================================================\r\n");
		System.out.println(sb.toString());
		return true;
	}
	
	///////////////// Synchronized to the Activiti //////////////////
	
	// 已废弃，同步见：ActGroupEntityServiceFactory.java、ActUserEntityServiceFactory.java

	/**
	 * 是需要同步Activiti数据，如果从未同步过，则同步数据。
	 */
	
	public void afterPropertiesSet() throws Exception {
		if (!Global.isSynActivitiIndetity()){
			return;
		}

	}
	

	
	
	
	
	public MiniPage pageQueryRole(MiniPage mp) {
		List<Role> list = null;
		Page<Role> page = new Page<Role>();
		page.setPageNo(mp.getPageNo()+1);
		page.setPageSize(mp.getPageSize());
		Role r = new Role();
		r.setPage(page);
		User cUser = UserUtils.getUser();
		if(!cUser.isAdmin()) {
			Office o = new Office();
			o.setId(cUser.getCompany().getId());
			r.setOffice(o);
			list = roleDao.findOrgList(o);
		} else {
			list = roleDao.findAllList(r);
		}
		
		mp.setTotal(page.getCount());
		mp.setData(list);
		
		return mp;
	}

	/**
	 *根据机构code查询机构下面的用户
	 * @param jgCode
	 * @return
     */
	public List<User> getOfficeByCode(String jgCode) {
		return userDao.getOfficeByCode(jgCode);
	}

    /**
     * 根据机构和用户ID查询改机构下是否具有这个人，如果有这个人就返回这个人，如果没有这个人就返回null
     * @param jgCode 机构码
     * @param userId 人员ID
     * @return 这个人的实体
     */
    public User findUserByJgCodeAndUserId(String jgCode, String userId) {
        return userDao.findUserByJgCodeAndUserId(jgCode, userId);
    }
	public List<Office> findByZcjg()
	{
		return officeService.findByZcjg();
	}

}
