/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.MiniPage;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.JdbcUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller
 * @author ThinkGem
 * @version 2013-8-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

	@Autowired
	private SystemService systemService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private JdbcUtils jdbc;
	
	
	@ModelAttribute
	public User get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getUser(id);
		}else{
			return new User();
		}
	}

	

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"index"})
	public String index() {
		return "modules/sys/userList";
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@RequiresUser
	@RequestMapping(value = "pageQuery")
	@ResponseBody
	public MiniPage pageQuery(User user,
			HttpServletRequest request, HttpServletResponse response) {
		MiniPage p = new MiniPage(request,response);
		return systemService.pageQueryUser(p, user);
	}
	
	

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "form")
	public String form(User user, Model model) {
		
		return "modules/sys/userForm";
	}
	
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "edit")
	public String edit(User user, Model model) {
		model.addAttribute("user", user);
		return "modules/sys/userEdit";
	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(User user) {
		user.setNewPassword(user.getPassword());
		Office org = officeService.getOfficeByCode(user.getOrgCode());
		if("6".equals(org.getType())) {
			//部门
			user.setOffice(org);
			org = officeService.get(org.getParent().getId());
			user.setCompany(org);
		} else {
			user.setCompany(org);
			user.setOffice(new Office("0"));
		}
		
		// 如果密码为空，则不更换密码
		if (StringUtils.isNotBlank(user.getPassword())) {
			user.setPassword(SystemService.entryptPassword(user.getPassword()));
		}
		
		User anoUser = systemService.getUserByLoginName(user.getLoginName());
		int resultFlag = 0;
		if(anoUser != null && !anoUser.getId().equals(user.getId())) {
			//登录名重复
			resultFlag = 1;
		}
		
		anoUser = systemService.getUserByNo(user.getCompany().getId(),user.getNo());
		if(anoUser != null && !anoUser.getId().equals(user.getId())) {
			//工号重复
			resultFlag = 2;
		}
		
		if(resultFlag == 0) {
			//儿保接口维护############
			String rbRole = "";
			
			// 角色数据有效性验证，过滤不在授权内的角色
			if(StringUtils.isNotBlank(user.getRoleIds())){
				List<Role> roleList = Lists.newArrayList();
				List<String> roleIdList =Lists.newArrayList();
				String[] rids = user.getRoleIds().split(",");
				for(String rid:rids){
					roleIdList.add(rid);
				}
				for (Role r : systemService.findRoleForAdmin()){
					if (roleIdList.contains(r.getId())){
						roleList.add(r);
						//儿保权限判定(存在儿保角色)###########
						if(r.getEnname().indexOf("chms.") != -1){
							rbRole = r.getEnname();
						}
					}
				}
				user.setRoleList(roleList);
			}
			
			// 受控资源有效性验证，过滤不在授权内的受控资源
			if(StringUtils.isNotBlank(user.getResTreeIds())){
				List<Menu> menuList = Lists.newArrayList();
				List<String> menuIdList =Lists.newArrayList();
				String[] mids = user.getResTreeIds().split(",");
				for(String mid:mids){
					menuIdList.add(mid);
				}
				for (Menu m : systemService.findAllMenuForAdmin()){
					if (menuIdList.contains(m.getId())){
						menuList.add(m);
					}
				}
				user.setMenuList(menuList);
			}
			
			int is = 2;
			if(user.getId() == null || "".equals(user.getId())){
				is = 1;
			}
			// 保存用户信息
			systemService.saveUser(user);
			// 清除当前用户缓存
			if (user.getLoginName().equals(UserUtils.getUser().getLoginName())){
				UserUtils.clearCache();

			}
			
			//儿保接口维护############
			if("0".equals(Global.getConfig("synErBao")) && !"".equals(rbRole) ){
				String jgCode = officeService.get(user.getCompany().getId()).getCode();
				erBao(user,rbRole,jgCode,is+""); //新增/修改
			}
		}
		
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", resultFlag);
		return JsonMapper.getInstance().toJson(result);
	}
	/*
	 * 儿保用户新增维护接口，调用存储过程，远程新增
	 */
	private void erBao(User user,String role,String jgCode,String tag){
		//儿保接口的兼容模式（登录名、昵称、机构代码、角色、密码）业务标识（1新增/2修改/3作废）
		Map<String, String> m = new HashMap<String, String>();
		m.put("name", user.getLoginName());
		m.put("pass", user.getPassword()); //user.getNewPassword()
		m.put("title", user.getName());
		m.put("role", role);
		m.put("jg", jgCode);
		m.put("tag", tag);
		
		//远程存储过程调用
		jdbc.getJdbcTemplate().execute("begin eb1.func.set_personnel@dblink2('"+m.get("name")+"','"+m.get("title")+"','"+m.get("jg")+"','"+m.get("role")+"','"+m.get("pass")+"',"+m.get("tag")+"); commit; end;"); 
		//System.out.println("["+m.get("name")+"]["+m.get("pass")+"] ["+m.get("title")+"] ["+m.get("role")+"] ["+m.get("jg")+"] ["+m.get("tag")+"]");
	}
	
	
	@RequiresPermissions("sys:area:edit")
	@RequestMapping(value = "remove/{id}")
	@ResponseBody
	public String remove(@PathVariable String id) {
		User user = systemService.getUser(id);
		systemService.deleteUser(user);
		if("0".equals(Global.getConfig("synErBao"))){
			//通知儿保删除用户
			erBao(user,"","","3");
		}
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", 0);
		return JsonMapper.getInstance().toJson(result);
	}
	
	

	
	
	/**
	 * 下载导入用户数据模板
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户数据导入模板.xlsx";
    		List<User> list = Lists.newArrayList(); list.add(UserUtils.getUser());
    		new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
    }

	

	/**
	 * 用户信息显示及保存
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "info")
	public String info(User user, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			currentUser.setEmail(user.getEmail());
			currentUser.setPhone(user.getPhone());
			currentUser.setMobile(user.getMobile());
			currentUser.setRemarks(user.getRemarks());
			currentUser.setPhoto(user.getPhoto());
			systemService.updateUserInfo(currentUser);
			model.addAttribute("message", "保存用户信息成功");
		}
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userInfo";
	}

	/**
	 * 返回用户信息
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoData")
	public User infoData() {
		return UserUtils.getUser();
	}
	
	/**
	 * 修改个人用户密码
	 * @param oldPassword
	 * @param newPassword
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userModifyPwd";
			}
			if (SystemService.validatePassword(oldPassword, user.getPassword())){
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				model.addAttribute("message", "修改密码成功");
			}else{
				model.addAttribute("message", "修改密码失败，旧密码错误");
			}
		}
		model.addAttribute("user", user);
		return "modules/sys/userModifyPwd";
	}
	
	
	
	/**
	 * 显示可授权资源树
	 * @param userId
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "menuTree")
	public List<Menu> menuTree(@RequestParam(required=false) String userId) {
		List<Menu> result = Lists.newArrayList();
		List<Menu> list = null;
		User user = UserUtils.getUser();
		if(user.isAdmin()) {
			list = systemService.findAllMenuForAdmin();
		} else {
			list = systemService.findNonSysMenu();
		}
		Map<String,List<Menu>> childMap = Maps.newHashMap();
		for(Menu m :list) {
			childMap.put(m.getId(), new ArrayList<Menu>());
		}
		for(Menu m :list) {
			if(m.getParent() != null && StringUtils.isNotBlank(m.getParent().getId())) {
				List<Menu> childList = childMap.get(m.getParent().getId());
				if(childList != null) {
					childList.add(m);
				}
				
			}
		}
		for(Menu m :list) {
			if(!"1".equals(m.getId())) {
				List<Menu> childList = childMap.get(m.getId());
				if(childList.size()<=0) {
					m.setLeaf(true);
				}
				result.add(m);
			}
		}
		if(StringUtils.isNotBlank(userId)) {
			List<Menu> umList = systemService.findUserMenu(userId);
			if(umList != null && umList .size()>0) {
				Map<String,String> umMap = Maps.newHashMap();
				for(Menu m:umList) {
					umMap.put(m.getId(), null);
				}
				for(Menu m: result) {
					if(umMap.containsKey(m.getId())) {
						m.setChecked(true);
					}
				}
			}
			
			
			
		}
		
		
		return result;
	}
    
	
	
	
	
}
