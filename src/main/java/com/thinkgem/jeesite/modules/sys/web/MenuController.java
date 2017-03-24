/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单Controller
 * @author ThinkGem
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

	@Autowired
	private SystemService systemService;
	
	@ModelAttribute("menu")
	public Menu get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getMenu(id);
		}else{
			return new Menu();
		}
	}

	@RequiresPermissions("sys:menu:view")
	@RequestMapping(value = {"list", ""})
	public String list(Model model) {
		return "modules/sys/menuList";
	}

	@RequiresPermissions("sys:menu:view")
	@RequestMapping(value = "load/{id}")
	@ResponseBody
	public Menu load(@PathVariable String id) {
		
		Menu m = systemService.getMenu(id);
		if("0".equals(m.getParentId())){
			m.setParentName("无");
		} else {
			Menu parent = systemService.getMenu(m.getParentId());
			m.setParentName(parent.getName());
		}
		
		return m;
	}
	
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(Menu menu) {
		systemService.saveMenu(menu);
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", 0);
		return JsonMapper.getInstance().toJson(result);
	}
	
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "remove/{id}")
	@ResponseBody
	public String remove(@PathVariable String id) {
		Menu m = new Menu(id);
		systemService.deleteMenu(m);
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", 0);
		return JsonMapper.getInstance().toJson(result);
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "tree")
	public String tree() {
		return "modules/sys/menuTree";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "treeselect")
	public String treeselect(String parentId, Model model) {
		model.addAttribute("parentId", parentId);
		return "modules/sys/menuTreeselect";
	}
	
	
	
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Menu> treeData(@RequestParam(required=false) String id) {
		List<Menu> list = null;
		if(StringUtils.isNotBlank(id)) {
			list = systemService.findMenuByParentId(id);
		} else {
			list = systemService.findTopMenu();
		}
		return list;
	}
}
