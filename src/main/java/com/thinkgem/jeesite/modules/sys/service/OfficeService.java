/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.service.TreeService;
import com.thinkgem.jeesite.modules.sys.dao.OfficeDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.utils.ComboNode;
import com.thinkgem.jeesite.modules.sys.utils.SimpleTreeNode;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 机构Service
 * 
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<OfficeDao, Office> {

	@Autowired
	private AreaService areaService;

	@Autowired
	private OfficeDao officeDao;

	@DataSource(name = DataSource.fy)
	public List<Office> findAll() {
		return UserUtils.getOfficeList();
	}

	@DataSource(name = DataSource.fy)
	public List<Office> findList(Boolean isAll) {
		if (isAll != null && isAll) {
			return UserUtils.getOfficeAllList();
		} else {
			return UserUtils.getOfficeList();
		}
	}

	@DataSource(name = DataSource.fy)
	public List<Office> findNotDel() {
		return dao.findAllList(new Office());
	}

	@DataSource(name = DataSource.fy)
	public List<Office> findList(Office office) {
		office.setParentIds(office.getParentIds() + "%");
		return dao.findByParentIdsLike(office);
	}

	@DataSource(name = DataSource.fy)
	@Transactional(readOnly = false)
	public void save(Office office) {
		if (office.getAddress() != null) {
			if (StringUtils.isNotBlank(office.getArea().getCode())) {
				Area area = areaService.getAreaByCode(office.getArea().getCode());
				office.setArea(area);

			}
		}
		super.save(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	@DataSource(name = DataSource.fy)
	@Transactional(readOnly = false)
	public void delete(Office office) {
		super.delete(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	@DataSource(name = DataSource.fy)
	@Transactional(readOnly = false)
	public Office getOfficeByCode(String code) {
		Office o = new Office();
		o.setCode(code);
		return dao.getByCode(o);
	}

	/**
	 * 查询顶级机构
	 * 
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public List<Office> findTopOffice() {
		List<Role> roleList = UserUtils.getRoleList();
		String areaCode = "";
		Office params = new Office();
		//区县管理员可以查询区县下面所有的机构
		if(roleList != null && roleList.size()>0){
			for (Role role : roleList){
				if(role.getEnname().equals("AREA_MANAGE")){
					areaCode = UserUtils.getUser().getCompany().getArea().getCode();
					//区县机构下的用户（不是市用户）
					if(areaCode.endsWith("000000") && !areaCode.endsWith("000000000")){
						//由于区域代码和机构代码中的 XX区 的代码相同，所以这里就塞入相同的机构
						params.setCode(areaCode);
					}
				}
			}
		}
		return dao.findTop(params);
	}

	/**
	 * 查询子机构
	 * 
	 * @param pid
	 *            父id
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public List<Office> findChilds(String pid) {
		return dao.findChilds(new Office(pid));
	}

	/**
	 * 查询所有子机构（包含孙机构）
	 * 
	 * @param o
	 * @param flag
	 *            是否包含部门（0：包含；1：不包含）
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public List<Office> findAllChilds(Office o, String flag) {
		if ("0".equals(flag)) {
			o.setType(null);
		} else {
			o.setType("6");
		}
		return dao.findAllChilds(o);
	}

	/**
	 * 查询自身和所有子机构（包含孙机构）
	 * 
	 * @param o
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public List<Office> findWithAllChilds(Office o) {
		return dao.findWithAllChilds(o);
	}

	@DataSource(name = DataSource.fy)
	public String getRolist(Office o) {
		if ("3".equals(o.getType())) {
			if ("310000000000".equals(o.getArea().getCode())) {
				return "sfy";
			} else {
				return "qfy";
			}
		}

		return "";

	}

	@DataSource(name = DataSource.fy)
	public List<ComboNode> findByQxcode(String parentCode) {
		List<ComboNode> childStList = Lists.newArrayList();
		List<Office> list = dao.findByQxcode(parentCode);
		for (Office o : list) {
			if ("5".equals(o.getType())) {
				ComboNode code = new ComboNode(o.getCode(), o.getName());
				childStList.add(code);
			}
		}
		return childStList;
	}

	@DataSource(name = DataSource.fy)
	public List<ComboNode> findYiyuanByQxcode(String parentCode) {
		List<ComboNode> childStList = Lists.newArrayList();
		List<Office> list = dao.findByQxcode(parentCode);
		for (Office o : list) {
			if ("4".equals(o.getType())) {
				ComboNode code = new ComboNode(o.getCode(), o.getName());
				childStList.add(code);
			}
		}
		return childStList;
	}

	@DataSource(name = DataSource.fy)
	public List<Office> findJg(String name) {
		return dao.findJg(name);
	}

	/**
	 * 其中区县下机构，选择机构时，只能选择同区县下的机构；  * 区县妇幼所，可以选择市级或者其他区县妇幼所  *
	 * 市级妇幼所，可以选择所有区县妇幼所
	 * 
	 * @param
	 * @return
	 */
	@DataSource(name = DataSource.fy)
	public List<SimpleTreeNode> getMessageOrg() {
		List<SimpleTreeNode> simpleTreeNodeList = new ArrayList<SimpleTreeNode>();

		String orgType = getRolist(UserUtils.getUser().getCompany());
		String regionCode = UserUtils.getUser().getCompany().getArea().getCode();

		if (orgType.equals("sfy")) {
			// 市妇幼
		} else if (orgType.equals("qfy")) {
			// 区妇幼
		} else {
			// 其他机构
			orgType = "qtjg";
		}

		Office params = new Office();
		params.setType(orgType);
		params.setCode(regionCode); // 行政区划的code和行政区划机构节点code是一样的

		List<Office> officeList = officeDao.getMessageOrg(params);

		if (officeList != null && officeList.size() > 0) {
			for (int i = 0; i < officeList.size(); i++) {
				Office office = officeList.get(i);
				SimpleTreeNode node = new SimpleTreeNode();
				node.setId(office.getCode());
				node.setText(office.getText());
				node.setChecked(false);
				// loopOffice(office,node);
				simpleTreeNodeList.add(node);
			}
		}
		return simpleTreeNodeList;

	}

	@DataSource(name = DataSource.fy)
	public List<Office> getSq(String code) {
		return dao.getSq(code);
	}

	@DataSource(name = DataSource.fy)
	public List<ComboNode> getxySq(String code) {
		List<Office> list = dao.getSq(code);
		List<ComboNode> lists = Lists.newArrayList();
		for (Office v : list) {
			ComboNode vo = new ComboNode();
			vo.setText(v.getName());
			vo.setValue(v.getCode());
			lists.add(vo);
         }
		return lists;
	}

	@DataSource(name = DataSource.fy)
	public List<Office> getOfficeByType(String jgType) {
		return dao.getOfficeByType(jgType);
	}

	@DataSource(name = DataSource.fy)
	public  List<Office> findByZcjg()
	{
		return dao.findByZcjg();
	}

}
