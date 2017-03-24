/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机构Controller
 * @author ThinkGem
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeController extends BaseController {

	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute("office")
	public Office get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return officeService.get(id);
		}else{
			return new Office();
		}
	}

	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = {""})
	public String index(Office office, Model model) {
		return "modules/sys/officeIndex";
	}

	
	
	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "load/{id}")
	@ResponseBody
	public Office load(@PathVariable String id) {
		Office office = new Office();
		office.setId(id);
		Office o = officeService.get(office);
		if("0".equals(o.getParentId())){
			o.setParentName("无");
		} else {
			office.setId(o.getParentId());
			Office parent = officeService.get(office);
			o.setParentName(parent.getName());
		}
		
		
		
		return o;
	}
	
	@RequiresPermissions("sys:office:edit")
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(Office office) {
		Office anoOf = officeService.getOfficeByCode(office.getCode());
		int resultFlag = 0;
		if(anoOf != null && !anoOf.getId().equals(office.getId())) {
			resultFlag = 1;
		}
		if(resultFlag == 0) {
			officeService.save(office);
		}
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", resultFlag);
		return JsonMapper.getInstance().toJson(result);
	}
	
	@RequiresPermissions("sys:office:edit")
	@RequestMapping(value = "remove/{id}")
	@ResponseBody
	public String remove(@PathVariable String id) {
		Office o = new Office();
		o.setId(id);
		officeService.delete(o);
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", 0);
		return JsonMapper.getInstance().toJson(result);
	}

	/**
	 * 树形结构展示机构
	 * @param id 父机构id
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Office> treeData(@RequestParam(required=false) String id) {
		List<Office> list = null;
		if(StringUtils.isNotBlank(id)) {
			list = officeService.findChilds(id);
		} else {
			User user = UserUtils.getUser();
			List<Role> roleList = user.getRoleList();
			// 0 普通用户 1.系统管理员 2.区县管理员 3.机构管理员
			String manager = "0";
			if(roleList != null && roleList.size()>0) {
				for (Role role : roleList) {
					if(role != null && StringUtils.isNotBlank(role.getEnname())){
						if(role.getEnname().equals("dept")){
							manager = "1";
						}else if(role.getEnname().equals("AREA_MANAGE")){
							manager = "2";
						}else if(role.getEnname().equals("ORG_MANAGE")){
							manager = "3";
						}
						break;
					}
				}
			}

			if(user.isAdmin()){
				//超级管理员
				list = officeService.findTopOffice();
			}else{
				if(manager.equals("1")){
					//系统管理员
                    list = officeService.findTopOffice();
				}else if(manager.equals("2")){
					//区县管理员
					Office office = officeService.getOfficeByCode(user.getCompany().getArea().getCode());
					list = new ArrayList<Office>();
                    list.add(office);
				}else if(manager.equals("3")){
					//机构管理员
					Office office = officeService.getOfficeByCode(user.getCompany().getCode());
                    list = new ArrayList<Office>();
                    list.add(office);
				}else{
					//普通用户
					list = new ArrayList<Office>();
				}
			}
		}
		
		return list;
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "findsczx")
	public List<Office> treeData() {
		List<Office> list =officeService.findAll();
		List<Office> lists =Lists.newArrayList();
		for(Office o:list){
			if("0".equals(o.getCszx())){
			lists.add(o);	
			}
			}
		return lists;
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "findzdzx")
	public List<Office> tree() {
		List<Office> list =officeService.findAll();
		List<Office> lists =Lists.newArrayList();
		for(Office o:list){
			if("0".equals(o.getZdzx())){
			lists.add(o);	
			}
			}
		return lists;
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "findjg")
	public List<Office> findjg(@RequestParam("name")String name) {
		List<Office> list =officeService.findJg(name);
		List<Office> lists =Lists.newArrayList();
		for(Office o:list){
			if("0".equals(o.getZdzx())){
			lists.add(o);	
			}
			}
		return list;
	}
}
