/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.utils.ComboNode;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 区域Controller
 * @author ThinkGem
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/area")
public class AreaController extends BaseController {

	@Autowired
	private AreaService areaService;
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute("area")
	public Area get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return areaService.get(id);
		}else{
			return new Area();
		}
	}

	@RequiresPermissions("sys:area:view")
	@RequestMapping(value = {"list", ""})
	public String list(Area area, Model model) {
		return "modules/sys/areaList";
	}

	@RequiresPermissions("sys:area:view")
	@RequestMapping(value = "load/{id}")
	@ResponseBody
	public Area load(@PathVariable String id) {
		Area area = new Area();
		area.setId(id);
		Area a = areaService.get(area);
		if("1".equals(a.getType())) {
			a.setTypeName("国家");
		}
		if("2".equals(a.getType())) {
			a.setTypeName("省份、直辖市");
		}
		if("3".equals(a.getType())) {
			a.setTypeName("地市");
		}
		if("4".equals(a.getType())) {
			a.setTypeName("区县");
		}
		if("5".equals(a.getType())) {
			a.setTypeName("街道（镇）");
		}
		if("6".equals(a.getType())) {
			a.setTypeName("居委（村）");
		}
		
		if("0".equals(a.getParentId())){
			a.setParentName("无");
		} else {
			area.setId(a.getParentId());
			Area parent = areaService.get(area);
			a.setParentName(parent.getName());
		}
		return a;
	}
	
	@RequiresPermissions("sys:area:edit")
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(Area area) {
		
		Area anoArea = areaService.getAreaByCode(area.getCode());
		int resultFlag = 0;
		if(anoArea != null && !anoArea.getId().equals(area.getId())) {
			resultFlag = 1;
		}
		if(resultFlag == 0) {
			areaService.save(area);
		}
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("result", resultFlag);
		return JsonMapper.getInstance().toJson(result);
	}
	
	@RequiresPermissions("sys:area:edit")
	@RequestMapping(value = "remove/{id}")
	@ResponseBody
	public String remove(@PathVariable String id) {
			Area a = new Area();
			a.setId(id);
			areaService.delete(a);
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("result", 0);
			return JsonMapper.getInstance().toJson(result);
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Area> treeData(@RequestParam(required=false) String code) {
		
		List<Area> list = null;
		if(StringUtils.isNotBlank(code)) {
			list = areaService.findByParentCode(code);
		} else {
			list = areaService.findTopArea();
		}
		if(list != null && list.size()>0) {
			for(Area a: list) {
				if("1".equals(a.getType())) {
					a.setTypeName("国家");
				}
				if("2".equals(a.getType())) {
					a.setTypeName("省份、直辖市");
				}
				if("3".equals(a.getType())) {
					a.setTypeName("地市");
				}
				if("4".equals(a.getType())) {
					a.setTypeName("区县");
				}
				if("5".equals(a.getType())) {
					a.setTypeName("街道（镇）");
				}
				if("6".equals(a.getType())) {
					a.setTypeName("居委（村）");
					a.setLeaf(true);
				}
				if("3".equals(a.getType()) || "4".equals(a.getType()) || "5".equals(a.getType())) {
					List<Area> childs = areaService.findByParentCode(a.getCode());
					if(childs == null || childs.size()<=0) {
						a.setLeaf(true);
					}
				}
				
			}
		}
		
		return list;
	}
	
	/**
	 * 按父区域查询子区域
	 * @param parentCode
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "findChildArea")
	public List<ComboNode> findChildArea(@RequestParam(required=true) String parentCode) {
		List<ComboNode> result  = Lists.newArrayList();
		if(parentCode.equals("310000000000")){
			ComboNode cn = new ComboNode();
			cn.setValue("310101000000");
			cn.setText("黄浦区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310104000000");
			cn.setText("徐汇区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310105000000");
			cn.setText("长宁区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310106000000");
			cn.setText("静安区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310107000000");
			cn.setText("普陀区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310108000000");
			cn.setText("闸北区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310109000000");
			cn.setText("虹口区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310110000000");
			cn.setText("杨浦区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310112000000");
			cn.setText("闵行区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310113000000");
			cn.setText("宝山区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310114000000");
			cn.setText("嘉定区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310115000000");
			cn.setText("浦东新区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310116000000");
			cn.setText("金山区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310117000000");
			cn.setText("松江区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310118000000");
			cn.setText("青浦区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310120000000");
			cn.setText("奉贤区");
			result.add(cn);
			cn = new ComboNode();
			cn.setValue("310230000000");
			cn.setText("崇明县");
			result.add(cn);
		} else {
			List<Area> lists = areaService.findByParentCode(parentCode);
			
			if(lists != null){ //兼容初始 000000时，报错
				for(Area node:lists) {
					ComboNode cn = new ComboNode();
					cn.setValue(node.getCode());
					cn.setText(node.getName());
					result.add(cn);
				}
			}
		}
		return result;
	}
	/**
	 * 按父区域查询子区域
	 * @param parentCode
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "findChildAreas")
	public List<ComboNode> findChildAreas(@RequestParam(required=true) String parentCode) {
		List<ComboNode> result  = Lists.newArrayList();
		List<Area> lists = areaService.findByParentCode(parentCode);
		if(lists != null){ //兼容初始 000000时，报错
			for(Area node:lists) {
				ComboNode cn = new ComboNode();
				cn.setValue(node.getCode());
				cn.setText(node.getName());
				result.add(cn);
			}
		}
		return result;
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "getsqByqxCode")//根据区县拿到区县下所有的社区
	public List<ComboNode> getsqByqxCode(@RequestParam(required=true) String parentCode) {
		return officeService.findByQxcode(parentCode);
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "getSq/{code}")//根据街道code拿到当前社区
	public List<Office> getSq(@PathVariable String code) {
		return officeService.getSq(code);
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "getxySq/{code}")//根据街道code拿到当前社区
	public List<ComboNode> getySq(@PathVariable String code) {

		return officeService.getxySq(code);
	}
}
