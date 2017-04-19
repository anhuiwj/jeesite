/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.service.TreeService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 区域Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class AreaService extends TreeService<AreaDao, Area> {
	
	@Autowired
	private AreaDao areaDao;
	
	/**
	 * 存放顶级行政区域
	 */
	private static List<Area> topList = Lists.newArrayList();
	
	/**
	 * 按code存放区域
	 */
	private static Map<String,Area> areaMap = Maps.newHashMap();
	
	/**
	 * 按code存放子区域
	 */
	private static Map<String,List<Area>> areaChildMap = Maps.newHashMap();

	@DataSource(name = DataSource.fy)
	public List<Area> findAll(){
		return UserUtils.getAreaList();
	}

	@DataSource(name = DataSource.fy)
	public List<Area> findByParentCode(String parentCode){

		return areaChildMap.get(parentCode);
	}

	@DataSource(name = DataSource.fy)
	@Transactional(readOnly = false)
	public void save(Area area) {
		Area p = area.getParent();
		if(p == null || StringUtils.isBlank(p.getId()) || "0".equals(p.getId())) {
			area.setType("2");//省级
		} else {
			Area a = new Area();
			a.setId(p.getId());
			a = dao.get(a);
			if("2".equals(a.getType())) {
				area.setType("3");//地市级
			}
			if("3".equals(a.getType())) {
				area.setType("4");//区县级
			}
			if("4".equals(a.getType())) {
				area.setType("5");//乡镇级
			}
			if("5".equals(a.getType())) {
				area.setType("6");//居委级
			}
		}
		super.save(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
		init();
	}

	@DataSource(name = DataSource.fy)
	@Transactional(readOnly = false)
	public void delete(Area area) {
		super.delete(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
		init();
	}
	
	/**
	 * 按代码查询区域
	 * @param code
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public Area getAreaByCode(String code) {

		return areaMap.get(code);
	}
	
	/**
	 * 按父id集合，查找所有的祖先
	 * @param parentIdArr
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public List<Area> findAllParent(String[] parentIdArr) {
		return dao.findAllParent(parentIdArr);
		
	}

	@DataSource(name = DataSource.fy)
	public List<Area> findTopArea()  {
		List<Role> roleList = UserUtils.getRoleList();
		String areaCode = "";
        Area params = new Area();
		//区县管理员可以查询区县下面所有的机构
		if(roleList != null && roleList.size()>0){
			for (Role role : roleList){
				if(role.getEnname().equals("AREA_MANAGE")){
					areaCode = UserUtils.getUser().getCompany().getArea().getCode();
					//区县机构下的用户（不是市用户）
					if(areaCode.endsWith("000000") && !areaCode.endsWith("000000000")){
                        params.setCode(areaCode);
                        List<Area> areaTopList = Lists.newArrayList();
                        Area area = areaDao.getAreaByCode(params);
                        areaTopList.add(area);
                        return areaTopList;
					}
				}
			}
		}
		return topList;
	}


	public List<Area> findTopAreaComboboxList()  {
		return topList;
	}

	
	/**
	 * 按代码获取区域名称
	 * @param code
	 * @return
	 */
	public String getNameByCode(String code) {
		Area area = areaMap.get(code);
		if(area != null) {
			return area.getName();
		}
		
		return "";
	}
	
	/**
	 * 从区域代码获取到对应的省份
	 * @param id
	 * @return
	 */
	public String getSperParentNode(String id){
		Area a  = this.get(id);
		String pname = a.getName();
		if(!a.getParentId().equals("0")){
			pname = this.getSperParentNode(a.getParentId());
		}
		return pname;
	}
	
	/**
	 * 初始化方法，加载区域缓存
	 */
	@PostConstruct 
	public void init() {
		areaMap.clear();
		areaChildMap.clear();
		Area a = new Area();
		a.setCode("0");
		topList = dao.findTopList(a);
		
		List<Area> list = dao.findAllList(new Area());
		Map<String,Area> areaIdMap = Maps.newHashMap();
		for(Area area : list) {
			areaMap.put(area.getCode(), area);
			areaIdMap.put(area.getId(), area);
		}
		
		for(Area area : list) {
			if(area.getParent() != null && !"0".equals(area.getParent().getId())) {
				Area parent = areaIdMap.get(area.getParent().getId());
				if(parent != null) {
					String code = parent.getCode();
					List<Area> childList = areaChildMap.get(code);
					if(childList == null) {
						childList = Lists.newArrayList();
						areaChildMap.put(code, childList);
					}
					childList.add(area);
					
				}
				
			}
		}
	}

	@DataSource(name = DataSource.fy)
	public Area queryAreaByUserId(String userId) {
		Area params = new Area();
		params.setUserId(userId);
		return areaDao.queryAreaByUserId(params);
	}

}
