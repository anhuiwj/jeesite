/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.MiniPage;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色Controller
 * @author ThinkGem
 * @version 2013-12-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/role")
public class RoleController extends BaseController {

	@Autowired
	private SystemService systemService;
	
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute("role")
	public Role get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getRole(id);
		}else{
			return new Role();
		}
	}
	
	@RequiresPermissions("sys:role:view")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/sys/roleList";
	}
	
	

	@RequiresPermissions("sys:role:view")
	@RequestMapping(value = "form")
	public String form() {
		return "modules/sys/roleForm";
	}
	
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "edit")
	public String edit(Role role, Model model) {
		model.addAttribute("role", role);
		return "modules/sys/roleEdit";
	}
	
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "assign")
	public String assign(Role role, Model model) {
		model.addAttribute("role", role);
		return "modules/sys/roleAssign";
	}
	
	/**
	 * 查询角色用户
	 * @return
	 */
	@RequiresUser
	@RequestMapping(value = "queryUserByRole")
	@ResponseBody
	public List<User> queryUserByRole(@RequestParam(required=false) String rid) {
		User user = new User();
		Role r = new Role(rid);
		user.setRole(r);
		
		User cu = UserUtils.getUser();
		if(!cu.isAdmin()) {
			user.setOrgCode(cu.getCompany().getCode());
		}
		
		return systemService.findUser(user);
	}
	
	/**
	 * 查询角色待分配的用户
	 * @return
	 */
	@RequiresUser
	@RequestMapping(value = "queryUserForAssign")
	@ResponseBody
	public List<User> queryUserForAssign(User user,@RequestParam(required=false) String rid) {
		Role r = new Role(rid);
		user.setRole(r);
		
		User cu = UserUtils.getUser();
		if(!cu.isAdmin()) {
			user.setOrgCode(cu.getCompany().getCode());
		}
		
		return systemService.findNotInRoleUser(user);
	}
	
	
	
	/**
	 * 分页查询
	 * @return
	 */
	@RequiresUser
	@RequestMapping(value = "pageQuery")
	@ResponseBody
	public MiniPage pageQuery(HttpServletRequest request, HttpServletResponse response) {
		MiniPage p = new MiniPage(request,response);
		return systemService.pageQueryRole(p);
	}
	
	@RequiresPermissions("sys:role:edit")
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(Role role) {
		Office org = null;
		User u = UserUtils.getUser();
		if(u.isAdmin()) {
			if(StringUtils.isNotBlank(role.getOrgCode())) {
				org = officeService.getOfficeByCode(role.getOrgCode());
			}
		} else {
			org = officeService.getOfficeByCode(u.getCompany().getCode());
		}
		if(org != null) {
			role.setOffice(org);
		}
		
		Role anoRole = systemService.getRoleByName(role.getName());
		int resultFlag = 0;
		if(anoRole != null && !anoRole.getId().equals(role.getId())) {
			//名称重复
			resultFlag = 1;
		}
		
		anoRole = systemService.getRoleByEnname(role.getEnname());
		if(anoRole != null && !anoRole.getId().equals(role.getId())) {
			//英文名重复
			resultFlag = 2;
		}
		
		if(resultFlag == 0) {
			// 受控资源有效性验证，过滤不在授权内的受控资源
			if(StringUtils.isNotBlank(role.getResTreeIds())){
				List<Menu> menuList = Lists.newArrayList();
				List<String> menuIdList =Lists.newArrayList();
				String[] mids = role.getResTreeIds().split(",");
				for(String mid:mids){
					menuIdList.add(mid);
				}
				for (Menu m : systemService.findAllMenuForAdmin()){
					if (menuIdList.contains(m.getId())){
						menuList.add(m);
					}
				}
				role.setMenuList(menuList);
			}
			
			systemService.saveRole(role);
		}
		
		
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", resultFlag);
		return JsonMapper.getInstance().toJson(result);
	}
	
	@RequiresPermissions("sys:role:edit")
	@RequestMapping(value = "remove/{id}")
	@ResponseBody
	public String remove(@PathVariable String id) {
		Role r = new Role(id);
		systemService.deleteRole(r);
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", 0);
		return JsonMapper.getInstance().toJson(result);
	}
	
	@RequiresPermissions("sys:role:edit")
	@RequestMapping(value = "removeUser")
	@ResponseBody
	public String removeUser(@RequestParam(required=false) String uid, @RequestParam(required=false) String rid) {
		Role r = new Role(rid);
		User user = new User(uid);
		r.setUser(user);
		systemService.deleteRoleUser(r);
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", 0);
		return JsonMapper.getInstance().toJson(result);
	}
	
	
	
	
	/**
	 * 角色分配
	 * @param role
	 * @param idsArr
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:role:edit")
	@RequestMapping(value = "assignUserToRole")
	@ResponseBody
	public String assignUserToRole(@RequestParam(required=false) String uids, @RequestParam(required=false) String rid) {
		Role role = new Role(rid);
		String[] uidArr = uids.split(",");
		List<User> userList = Lists.newArrayList();
		for(String uid :uidArr) {
			User u = new User(uid);
			userList.add(u);
		}
		role.setUserList(userList);
		systemService.assignUserToRole(role);
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", 0);
		return JsonMapper.getInstance().toJson(result);
	}

	/**
	 * 显示可授权资源树
	 * @param userId
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "menuTree")
	public List<Menu> menuTree(@RequestParam(required=false) String roleId) {
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
		if(StringUtils.isNotBlank(roleId)) {
			List<Menu> umList = systemService.findRoleMenu(roleId);
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
