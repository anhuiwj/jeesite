package com.thinkgem.jeesite.modules.sys.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.sys.entity.*;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
import com.thinkgem.jeesite.modules.sys.service.CodeService;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CodeUtils {
	
	public static final String CACHE_DICT = "dicCache";
	public static final String CACHE_DICT_UNIQUE = "dicCacheUNI";
	
	private static CodeService codeService = SpringContextHolder.getBean(CodeService.class);
	private static AreaService areaService = SpringContextHolder.getBean(AreaService.class);
	private static OfficeService officeService = SpringContextHolder.getBean(OfficeService.class);
	private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);

	
	
	
	
	
	
	/**
	 * 为combo，combobox获取json串
	 * @param path 字典路径
	 * @return
	 */
	public static String getComboDict(String path) {
		return getComboDict(path,false);
	}
	
	
	/**
	 * 为combo，combobox获取json串
	 * @param path 字典路径
	 * @param showCode 是否显示代码
	 * @return
	 */
	public static String getComboDict(String path,boolean showCode) {
		
		List<ComboNode> result  = Lists.newArrayList();
		List<Code> list = getDictList(path);
		
		for(Code node:list) {
			ComboNode cn = new ComboNode();
			cn.setValue(node.getValue());
			cn.setDesc(node.getDescription());  //新增字典描述
			if(showCode) {
				cn.setText(node.getValue()+"-"+node.getName());
			} else {
				cn.setText(node.getName());
			}
			result.add(cn);
		}
		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}
	
	/**
	 * 根据字典获取字典路径、值获取字典名称
	 * @param path
	 * @param value
	 * @return
	 */
	public static String getComboDictName(String path,String value) {
		List<Code> list = getDictList(path);
		
		if(value==null || "".equals(value)){
			
			return "";
		}
		for(Code node:list) {
			if(node.getValue().equals(value)){
				return node.getName();
			}
		}
		
		return "";
	}
	
	/**
	 * 为用户管理获取角色
	 * @return
	 */
	public static  String getRoleForAdmin() {
		List<ComboNode> result  = Lists.newArrayList();
		List<Role> list = systemService.findRoleForAdmin();
		for(Role node:list) {
			ComboNode cn = new ComboNode();
			cn.setValue(node.getId());
			cn.setText(node.getName());
			result.add(cn);
		}
		
		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}

    /**
     * 为当前用户获取可授权的角色
     * 系统管理员： 勾选 除了超级管理员外的所有角色
     * 区县管理员： 除了超管和系统管理员外，都可勾选
     * 机构管理员： 除了超级管理员、系统管理员、区县管理员外的所有角色
     * 当前自己不可勾选
     */
	public static  String getCurrUserRoleList() {
		User user = UserUtils.getUser();
		List<Role> roleList = user.getRoleList();
		// 0 普通用户 1.系统管理员 2.区县管理员 3.机构管理员
		String manager = "0";
		if(roleList != null && roleList.size()>0){
			for (Role role : roleList){
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


		List<ComboNode> result  = Lists.newArrayList();
		List<Role> list = systemService.findRoleForAdmin();
		for(Role node:list) {
			ComboNode cn = new ComboNode();
			cn.setValue(node.getId());
			cn.setText(node.getName());
            if(user.isAdmin()) {
                result.add(cn);
            }else{
                if(manager.equals("1")){
                    if(!node.getEnname().equals("dept")){
                        result.add(cn);
                    }
                }else if(manager.equals("2")){
                    if(!node.getEnname().equals("dept") && !node.getEnname().equals("AREA_MANAGE")){
                        result.add(cn);
                    }
                }else if(manager.equals("3")){
                    if(!node.getEnname().equals("dept") && !node.getEnname().equals("AREA_MANAGE") && !node.getEnname().equals("ORG_MANAGE")){
                        result.add(cn);
                    }
                }
            }
		}

		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}

	/**
	 * 为combo，combobox获取json串
	 * @param path 字典路径
	 * @param showCode 是否显示代码
	 * @return
	 */
	public static String getComboDictDesc(String tag,String path,boolean showCode) {
		String str ="";
		List<Code> list = getDictList(path);
		for(Code node:list) {
			if(node.getDescription() != null && !"".equals(node.getDescription())){
				str += node.getValue()+","+node.getName()+"("+tag+")["+node.getDescription()+"]#";
			}
		}
		return str;
	}
	
	
	
	/**
	 * 根据路径获得节点 先从缓存中找,再从数据库中找
	 * 
	 * @param path
	 *            节点路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->GXYROOT->1->2");
	 *            必须是全路径,对于象性别,证件类型这种平面结构的代码,这没问题,很容易就能给出全路径
	 *            对于象机构代码,行政区划代码这种树形结构的代码,难以给出全路径,此时建议使用getUniqueNode方法
	 * @return
	 */
	public static Code getNode(String path) {
		Code node = (Code) CacheUtils.get(CACHE_DICT, path);
		if(node ==  null) {
			node = codeService.getNode(path,false);
			if(node != null) {
				CacheUtils.put(CACHE_DICT, path, node);
			}
		}
		return node;
	}
	
	
	
	/**
	 * 根据路径获得所有子节点 先从缓存中找,再从数据库中找
	 * 
	 * @param path
	 *            节点路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->GXYROOT->1->2"); 必须是全路径
	 * @return
	 */
	public static List<Code> getDictList(String path) {
		Code node = getNode(path);
		if(node != null) {
			return node.getChilds();
		}
		return null;
	}
	
	/**
	 * 根据路径获得节点 名
	 * @param path
	 * 			       节点路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->GXYROOT->1->2");
	 *            必须是全路径,对于象性别,证件类型这种平面结构的代码,这没问题,很容易就能给出全路径
	 *            对于象机构代码,行政区划代码这种树形结构的代码,难以给出全路径,此时建议使用getUniqueNodeName方法
	 * @return
	 */
	public static String getDictLabel(String path){
		Code node = getNode(path);
		if(node != null) {
			return node.getName();
		}
		return "";
	}
	
	/**
	 * 根据组合码路径获得所对应的节点
	 * 该路径下的直接子节点必须都定义组合码
	 * @param comCodePath 组合码路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->SYM->6");
	 *                    必须是全路径,最后一位必须是组合码
	 * @return
	 */
	public static List<Code> getNodesByComCode(String comCodePath) {
		if(StringUtils.isEmpty(comCodePath)){
			throw new IllegalArgumentException("comCodePath is empty");
		}
		String[] arr = StringUtils.split(comCodePath, Code.PATH_SPERATOR);
		if(arr.length < 2){
			throw new IllegalArgumentException("path is invalid");
		}
		List<Code> result  = new ArrayList<Code>();
		int pos = comCodePath.lastIndexOf(Code.PATH_SPERATOR);
		String parentPath = comCodePath.substring(0,pos);
		String comCodeStr = comCodePath.substring(pos+Code.PATH_SPERATOR.length());
		long comCode = Long.parseLong(comCodeStr);
		List<Code> nodeList = getDictList(parentPath);
		if(nodeList != null && nodeList.size()>0){
			Code node = null;
			for(int i = 0; i < nodeList.size(); i++){
				node = nodeList.get(i);
				if(node.getComCode() == null){
					throw new UnsupportedOperationException("node comcode is null");
				}
				long calcResult = comCode & node.getComCode().longValue();
				if(calcResult == node.getComCode().longValue()){
					result.add(node);
				}
			}
		}
		return result;
	}
	
	/**
	 * 根据组合码路径获得转换后的 名
	 * 该路径下的直接子节点必须都定义组合码
	 * @param comCodePath 组合码路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->SYM->6");
	 *                    必须是全路径,最后一位必须是组合码
	 * @return
	 */
	public static List<String> getNameByComCode(String comCodePath){
		List<String> result  = new ArrayList<String>();
		List<Code> nodes = getNodesByComCode(comCodePath);
		if(nodes != null && nodes.size()>0){
			Code node = null;
			for(int i = 0; i < nodes.size(); i++){
				node = nodes.get(i);
				result.add(node.getName());
			}
		}
		return result;
	}

    /**
     * 根据机构类型查询所有的office
     * @param jgType 机构类型
     * @return
     */
	public static String getOfficeByType(String jgType){
        List<ComboNode> result  = Lists.newArrayList();
		List<Office> nodes = officeService.getOfficeByType(jgType);
		if(nodes != null && nodes.size()>0){
			for(int i = 0; i < nodes.size(); i++){
                ComboNode comboNode = new ComboNode();
                Office office = nodes.get(i);
                comboNode.setText(office.getName());
                comboNode.setValue(office.getCode());
				result.add(comboNode);
			}
		}
        return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}

	/**
	 * 根据路径获得代码唯一的节点 先从缓存中找,再从数据库中找
	 * 对于难以给出全路径的情况可以使用此方法
	 * 此时必须注意,要保证字典节点树下的代码唯一
	 * @param path 唯一节点路径 由字典大类代码 +'->'+唯一代码构成 如"COMMON->ORG_CODE->310111712568
	 * @return
	 */
	public static Code getUniqueNode(String path){
		Code node = (Code) CacheUtils.get(CACHE_DICT_UNIQUE, path);
		if(node ==  null) {
			node = codeService.getNode(path,true);
			if(node != null) {
				CacheUtils.put(CACHE_DICT_UNIQUE, path, node);
			}
		}
		return node;
	}
	
	/**
	 * 根据路径获得代码唯一的节点 名先从缓存中找,再从数据库中
	 * 对于难以给出全路径的情况可以使用此方法
	 * 此时必须注意,要保证字典节点树下的代码唯一
	 * @param path 唯一节点路径 由字典大类代码 +'->'+唯一代码构成 如"COMMON->ORG_CODE->310111712568
	 * @return
	 */
	public static String getUniqueNodeName(String path) {
		Code node = getUniqueNode(path);
		if(node != null){
			return node.getName();
		}
		return "";
	}
	
	/**
	 * 获取行政区划树
	 * @return json
	 */
	public static String getAreaTree() {
		
		List<Area> list = areaService.findAll();
		Map<String,Area> areaCodeMap = Maps.newHashMap();
		Map<String,Area> areaIdMap = Maps.newHashMap();
		Map<String,List<Area>> childCodeMap = Maps.newHashMap();
		for(Area a :list) {
			areaCodeMap.put(a.getCode(), a);
			areaIdMap.put(a.getId(), a);
			childCodeMap.put(a.getCode(), new ArrayList<Area>());
		}
		
		for(Area a :list) {
			if(a.getParent() != null && StringUtils.isNotBlank(a.getParent().getId())) {
				if(areaIdMap.containsKey(a.getParent().getId())) {
					Area parent = areaIdMap.get(a.getParent().getId());
					List<Area> childList = childCodeMap.get(parent.getCode());
					childList.add(a);
				}
			}
		}
		
		List<Area> tops = areaService.findTopArea();
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		for(Area a :tops) {
			SimpleTreeNode  root = new SimpleTreeNode();
			root.setText(a.getName());
			root.setId(a.getCode());
			List<SimpleTreeNode> childStList = recurChildArea(areaIdMap,childCodeMap,a);
			
			root.setChildren(childStList);	
			resultList.add(root);
		}
		
		
		
		
		
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		return result;
	}
	
	/**
	 * 获取组织机构树
	 * @return json
	 */
	public static String getOfficeTree() {
		List<Office> list = officeService.findAll();
		Map<String,Office> officeCodeMap = Maps.newHashMap();
		Map<String,Office> officeIdMap = Maps.newHashMap();
		Map<String,List<Office>> childCodeMap = Maps.newHashMap();
		for(Office o :list) {
			officeCodeMap.put(o.getCode(), o);
			officeIdMap.put(o.getId(), o);
			childCodeMap.put(o.getCode(), new ArrayList<Office>());
		}
		
		for(Office o :list) {
			if(o.getParent() != null && StringUtils.isNotBlank(o.getParent().getId())) {
				if(officeIdMap.containsKey(o.getParent().getId())) {
					Office parent = officeIdMap.get(o.getParent().getId());
					List<Office> childList = childCodeMap.get(parent.getCode());
					childList.add(o);
				}
			}
		}
		
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		for(Office o :list) {
			if(o.getParent() == null || StringUtils.isBlank(o.getParent().getId()) || "0".equals(o.getParent().getId())) {
				SimpleTreeNode  root = new SimpleTreeNode();
				root.setText(o.getName());
				root.setId(o.getCode());
				List<SimpleTreeNode> childStList = recurChildOffice(officeIdMap,childCodeMap,o);
				root.setChildren(childStList);	
				resultList.add(root);
				
			}
		}
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		return result;
	}
	
	/**
	 * 为系统设置获取机构树
	 * @param flag 是否包含部门（0：包含；1：不包含）
	 * @return
	 */
	public static String getOfficeTreeForAdmin(String flag) {
		List<Office> list = null;
		User user = UserUtils.getUser();
		if(user.isAdmin()) {
			String deployRegion = Global.getConfig("deploy_region");
			Office root = officeService.getOfficeByCode(deployRegion);
			list = officeService.findAllChilds(root, flag);
		} else {
			Office root = user.getCompany();
			list = officeService.findWithAllChilds(root);
		}
		
		Map<String,Office> officeCodeMap = Maps.newHashMap();
		Map<String,Office> officeIdMap = Maps.newHashMap();
		Map<String,List<Office>> childCodeMap = Maps.newHashMap();
		for(Office o :list) {
			officeCodeMap.put(o.getCode(), o);
			officeIdMap.put(o.getId(), o);
			childCodeMap.put(o.getCode(), new ArrayList<Office>());
		}
		
		for(Office o :list) {
			if(o.getParent() != null && StringUtils.isNotBlank(o.getParent().getId())) {
				if(officeIdMap.containsKey(o.getParent().getId())) {
					Office parent = officeIdMap.get(o.getParent().getId());
					List<Office> childList = childCodeMap.get(parent.getCode());
					childList.add(o);
				}
			}
		}
		
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		for(Office o :list) {
			if(o.getParent() == null || StringUtils.isBlank(o.getParent().getId()) || "0".equals(o.getParent().getId()) || !officeIdMap.containsKey(o.getParent().getId())) {
				SimpleTreeNode  root = new SimpleTreeNode();
				root.setText(o.getName());
				root.setId(o.getCode());
				List<SimpleTreeNode> childStList = recurChildOffice(officeIdMap,childCodeMap,o);
				root.setChildren(childStList);	
				resultList.add(root);
				
			}
		}
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		return result;
	}

	/**
	 * 为系统设置获取够一个区县下的所有机构树
	 * @param areaCode 区县编码
	 * @return
	 */
	public static String getOfficeTreeForArea(String areaCode) {
		List<Office> list = null;

		//顶层节点
		Office top = officeService.getOfficeByCode(areaCode);
		list = officeService.findWithAllChilds(top);

		Map<String,Office> officeCodeMap = Maps.newHashMap();
		Map<String,Office> officeIdMap = Maps.newHashMap();
		Map<String,List<Office>> childCodeMap = Maps.newHashMap();
		for(Office o :list) {
			officeCodeMap.put(o.getCode(), o);
			officeIdMap.put(o.getId(), o);
			childCodeMap.put(o.getCode(), new ArrayList<Office>());
		}

		for(Office o :list) {
			if(o.getParent() != null && StringUtils.isNotBlank(o.getParent().getId())) {
				if(officeIdMap.containsKey(o.getParent().getId())) {
					Office parent = officeIdMap.get(o.getParent().getId());
					List<Office> childList = childCodeMap.get(parent.getCode());
					childList.add(o);
				}
			}
		}

		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		for(Office o :list) {
			if(o.getParent() == null || StringUtils.isBlank(o.getParent().getId()) || "0".equals(o.getParent().getId()) || !officeIdMap.containsKey(o.getParent().getId())) {
				SimpleTreeNode  root = new SimpleTreeNode();
				root.setText(o.getName());
				root.setId(o.getCode());
				List<SimpleTreeNode> childStList = recurChildOffice(officeIdMap,childCodeMap,o);
				root.setChildren(childStList);
				resultList.add(root);
			}
		}
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		return result;
	}

	/**
	 * 为系统设置获取够一个区县下的所有机构树
	 * @param areaCode 区县编码
	 * @return
	 */
	public static String getOfficeTreeForAreas(String areaCode) {
		List<Office> list = null;

		//顶层节点
		Office top = officeService.getOfficeByCode(areaCode);
		list = officeService.findWithAllChilds(top);

		Map<String,Office> officeCodeMap = Maps.newHashMap();
		Map<String,Office> officeIdMap = Maps.newHashMap();
		Map<String,List<Office>> childCodeMap = Maps.newHashMap();
		for(Office o :list) {
			officeCodeMap.put(o.getCode(), o);
			officeIdMap.put(o.getId(), o);
			childCodeMap.put(o.getCode(), new ArrayList<Office>());
		}

		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		for(Office o :list) {
			SimpleTreeNode  root = new SimpleTreeNode();
				root.setText(o.getName());
				root.setId(o.getCode());
				List<SimpleTreeNode> childStList = recurChildOffice(officeIdMap,childCodeMap,o);
				root.setChildren(childStList);
				resultList.add(root);
		}
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		return result;
	}
	
	
	/**
	 * 获取所属区县的组织机构树
	 * @return json
	 */
	public static String getOfficeTrees(String tag,String code) {
		if(tag != null && code != null&&!"".equals(code)){
			if("0".equals(tag)){ //全市
				code = code.substring(0,3);
			}else{ //区县
				code = code.substring(0,6);
			}
		}else{
			code = "0";
		}
		
		List<Office> list = officeService.findNotDel();
		Map<String,Office> officeCodeMap = Maps.newHashMap();
		Map<String,Office> officeIdMap = Maps.newHashMap();
		Map<String,List<Office>> childCodeMap = Maps.newHashMap();
		for(Office o :list) {
			officeCodeMap.put(o.getCode(), o);
			officeIdMap.put(o.getId(), o);
			childCodeMap.put(o.getCode(), new ArrayList<Office>());
		}
		
		for(Office o :list) {
			if(o.getParent() != null && StringUtils.isNotBlank(o.getParent().getId())) {
				if(officeIdMap.containsKey(o.getParent().getId())) {
					Office parent = officeIdMap.get(o.getParent().getId());
					List<Office> childList = childCodeMap.get(parent.getCode());
					childList.add(o);
				}
			}
		}
		
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		for(Office o :list) {
			if(o.getParent() == null || StringUtils.isBlank(o.getParent().getId()) || "0".equals(o.getParent().getId())) {
				if("0".equals(tag)) {
					List<Office> l = childCodeMap.get(o.getCode());
					for(Office oo:l) {
						SimpleTreeNode  root = new SimpleTreeNode();
						root.setText(oo.getName());
						root.setId(oo.getCode());
						List<SimpleTreeNode> childStList = recurChildOffices(officeIdMap,childCodeMap,oo,code);
						root.setChildren(childStList);	
						resultList.add(root);
					}
				} else {
					List<Office> l = childCodeMap.get(o.getCode());
					for(Office oo:l) {
						if(oo.getArea().getCode().indexOf(code) != -1) {
							List<Office> ll = childCodeMap.get(oo.getCode());
							for(Office ooo:ll) {
								SimpleTreeNode  root = new SimpleTreeNode();
								root.setText(ooo.getName());
								root.setId(ooo.getCode());
								List<SimpleTreeNode> childStList = recurChildOffices(officeIdMap,childCodeMap,ooo,code);
								root.setChildren(childStList);	
								resultList.add(root);
							}
						}
					}
				}
				
				
			}
		}
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		//System.out.println("{ "+result+" }");
		return result;
	}
	/**
	 * 递归处理组织机构
	 * @param officeIdMap
	 * @param childCodeMap
	 * @param a
	 * @return
	 */
	private static List<SimpleTreeNode> recurChildOffices(Map<String,Office> officeIdMap,Map<String,List<Office>> childCodeMap,Office a,String code) {
		List<SimpleTreeNode> childStList = Lists.newArrayList();
		List<Office> childList = childCodeMap.get(a.getCode());
		if(childList.size()>0) {
			for(Office child: childList) {
				//System.out.println("{"+child.getGrade()+"} {"+child.getCode()+"}{"+child.getName()+"} {"+child.getArea().getCode()+"}{"+child.getArea().getName()+"}");
				
				//机构类型=1（机构） 所属区县层级 
				if(child.getArea().getCode().indexOf(code) != -1){
					SimpleTreeNode st = new SimpleTreeNode();
					st.setId(child.getCode());
					st.setText(child.getName());
					
					List<SimpleTreeNode> list = recurChildOffices(officeIdMap,childCodeMap,child,code);
					st.setChildren(list);
					
					childStList.add(st);
				}
			}
		}
		return childStList;
	}
	
	
	
	/**
	 * 递归处理行政区划
	 * @param areaIdMap
	 * @param childCodeMap
	 * @param a
	 * @return
	 */
	private static List<SimpleTreeNode> recurChildArea(Map<String,Area> areaIdMap,Map<String,List<Area>> childCodeMap,Area a) {
		List<SimpleTreeNode> childStList = Lists.newArrayList();
		List<Area> childList = childCodeMap.get(a.getCode());
		if(childList.size()>0) {
			for(Area child: childList) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(child.getCode());
				st.setText(child.getName());
				
				
				List<SimpleTreeNode> list = recurChildArea(areaIdMap,childCodeMap,child);
				st.setChildren(list);
				
				childStList.add(st);
			}
		}
		return childStList;
	}
	
	
	/**
	 * 递归处理组织机构
	 * @param officeIdMap
	 * @param childCodeMap
	 * @param a
	 * @return
	 */
	private static List<SimpleTreeNode> recurChildOffice(Map<String,Office> officeIdMap,Map<String,List<Office>> childCodeMap,Office a) {
		List<SimpleTreeNode> childStList = Lists.newArrayList();
		List<Office> childList = childCodeMap.get(a.getCode());
		if(childList.size()>0) {
			for(Office child: childList) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(child.getCode());
				st.setText(child.getName());
				
				
				List<SimpleTreeNode> list = recurChildOffice(officeIdMap,childCodeMap,child);
				st.setChildren(list);
				
				childStList.add(st);
			}
		}
		return childStList;
	}
	
	/**
	 * 获取树
	 * @return json
	 */
	public  static String getCodeTree(String path) {
		
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		
		Code root = codeService.getNode(path, false);
		List<Code> list = codeService.findChildsByPath(path);
		
		Map<String,Code> codeCodeMap = Maps.newHashMap();
		Map<String,Code> codeIdMap = Maps.newHashMap();
		Map<String,List<Code>> childIdMap = Maps.newHashMap();
		for(Code a :list) {
			codeCodeMap.put(a.getValue(), a);
			codeIdMap.put(a.getId(), a);
			childIdMap.put(a.getId(), new ArrayList<Code>());
		}
		
		for(Code a :list) {
			if(a.getParent() != null && StringUtils.isNotBlank(a.getParent().getId())) {
				if(codeIdMap.containsKey(a.getParent().getId())) {
					Code parent = codeIdMap.get(a.getParent().getId());
					List<Code> childList = childIdMap.get(parent.getId());
					childList.add(a);
				}
			}
		}
		for(Code a :list) {
			if(a.getParent() != null && StringUtils.isNotBlank(a.getParent().getId()) && a.getParent().getId().equals(root.getId())) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(a.getValue());
				st.setText(a.getName());
				
				List<SimpleTreeNode> childStList = recurChildCode(codeIdMap,childIdMap,a);
				
				st.setChildren(childStList);
				
				resultList.add(st);
			}
		}
		
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		
		//System.out.println("------------------"+result);
		
		
		return result;
		
	}


	/**
	 * 递归处理字典代码
	 * @param codeIdMap
	 * @param childIdMap
	 * @param a
     * @return
     */
	private static List<SimpleTreeNode> recurChildCode(Map<String,Code> codeIdMap,Map<String,List<Code>> childIdMap,Code a) {
		List<SimpleTreeNode> childStList = Lists.newArrayList();
		List<Code> childList = childIdMap.get(a.getId());
		if(childList.size()>0) {
			for(Code child: childList) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(child.getValue());
				st.setText(child.getName());
				
				
				List<SimpleTreeNode> list = recurChildCode(codeIdMap,childIdMap,child);
				st.setChildren(list);
				
				childStList.add(st);
			}
		}
		return childStList;
	}
	
	/**
	 * 按行政区划代码查找所有祖先
	 * @param code
	 * @return
	 */
	public static List<Area> getAreaChainByCode(String code) {
		List<Area> result = Lists.newArrayList();
		Area area = areaService.getAreaByCode(code);
		if(StringUtils.isNotBlank(area.getParentIds())) {
			String[] arr = area.getParentIds().split(",");
			List<Area> pList = areaService.findAllParent(arr);
			result.addAll(pList);
		}
		
		
		result.add(area);
		return result;
	}
	
	
	/**
	 * 按行政区划代码查找所有祖先,构造完整行政区划名
	 * @param code
	 * @return
	 */
	public static String getAreaFullNameByCode(String code) {
		List<Area> list = getAreaChainByCode(code);
		if(list != null && list.size()>0) {
			StringBuffer result = new StringBuffer();
			for(Area area:list) {
				result.append(area.getName());
			}
			return result.toString();
		}
		return null;
	}

	/**
	 * 根据 传入的code拼接地址
	 * @param shen  省
	 * @param shi   市
	 * @param xian  区
	 * @param jiedao 街道
	 * @param juwei 居委
	 * @param textAddress 详细文本地址
	 * @param showAllAddress 是否顯示全地址 - true顯示所有 -false顯示textAddress
     * @return 组合拼接地址
     */
	public static String getAreaFullNameByCode(String shen,String shi,String xian,String jiedao,String juwei,String textAddress,Boolean showAllAddress) {
		String returnAddress = "";
		if(showAllAddress){
			if(StringUtils.isNotBlank(shen)){
				Area shenArea = areaService.getAreaByCode(shen);
				if(shenArea != null){
					returnAddress += shenArea.getName();
				}
			}
			if(StringUtils.isNotBlank(shi)){
				Area shiArea = areaService.getAreaByCode(shi);
				if(shiArea != null){
                    String shiAreaText = shiArea.getName();
                    if(StringUtils.isNotBlank(shiAreaText) && !shiAreaText.equals("市辖区") && !shiAreaText.equals("县")) {
                        returnAddress += shiAreaText;
                    }
				}
			}
			if(StringUtils.isNotBlank(xian)){
				Area xianArea = areaService.getAreaByCode(xian);
				if(xianArea != null){
					returnAddress += xianArea.getName();
				}
			}
			if(StringUtils.isNotBlank(jiedao)){
				Area jiedaoArea = areaService.getAreaByCode(jiedao);
				if(jiedaoArea != null){
					returnAddress += jiedaoArea.getName();
				}
			}
			if(StringUtils.isNotBlank(juwei)){
				Area juweiArea = areaService.getAreaByCode(juwei);
				if(juweiArea != null){
					returnAddress += juweiArea.getName();
				}
			}

		}

        if(StringUtils.isNotBlank(textAddress)){
            returnAddress += textAddress;
        }
		return returnAddress;
	}

	/**
	 * 根据行政区划代码，判断行政区划类型
	 * @param code
	 * @return 1 省 2地级市  3 区县  4 乡镇（街道） 5 村（居委）
	 */
	public static int getAreaTypeByCode(String code) {
		if(code.endsWith("0000000000")){
			return 1;
		} else {
			if(code.endsWith("00000000")){
				return 2;
			} else {
				if(code.endsWith("000000")){
					return 3;
				} else {
					if(code.endsWith("000")){
						return 4;
					} else {
						return 5;
					}
				}
			}
		}
		
	}
	
	/**
	 * 判断行政区划是否选到区以下
	 * @param code
	 * @return
	 */
	public static boolean isCounty(String code) {
		int i = getAreaTypeByCode(code);
		if(i >= 3) {
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * 根据机构代码返回机构
	 * @param code
	 * @return
	 */
	public static Office getOfficeByCode(String code) {
		return officeService.getOfficeByCode(code);
	}

	/**
	 * 根据机构code查询机构下面的用户
	 * @param jgCode
	 * @return
     */
	public static String getUserByOfficeCode(String jgCode) {

		List<ComboNode> result  = Lists.newArrayList();
		List<User> users = systemService.getOfficeByCode(jgCode);
		if(users!=null && users.size()>0){
			for(User user : users){
				ComboNode node = new ComboNode();
				node.setValue(user.getId());
				node.setText(user.getName());
				result.add(node);
			}
		}

		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}
	/**
	 * 根据机构code查询助产机构
	 * @param
	 * @return
	 */
	public static String getByOfficeCode() {
		List<ComboNode> result  = Lists.newArrayList();
		List<Office> office = systemService.findByZcjg();
		if(office!=null && office.size()>0){
			for(Office of : office){
				ComboNode node = new ComboNode();
				node.setValue(of.getCode());
				node.setText(of.getName());
				result.add(node);
			}
		}

		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}
	/**
	 * 获取多选字典文字
	 * @param path
	 * @param value
     * @return
     */
	public static String getComboDictLables(String path, String value){
		List<Code> list = getDictList(path);
		List<String> valueList = Lists.newArrayList();
		if(com.thinkgem.jeesite.common.utils.StringUtils.isNotBlank(path) && com.thinkgem.jeesite.common.utils.StringUtils.isNotBlank(value)){
			for(String val : value.split(",")){
				valueList.add(getComboDictName(path, val));
			}
		}
		return valueList.toString();
	}



}
