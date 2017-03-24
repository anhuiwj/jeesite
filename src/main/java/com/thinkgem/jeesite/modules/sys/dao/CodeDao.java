package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.TreeDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Code;

import java.util.List;

@MyBatisDao
public interface CodeDao extends TreeDao<Code> {
	
	public List<Code> findByParent(Code entity);
	
	public List<Code> findTopList(Code entity);
	
	public List<Code> findForCheckCode(Code entity);
	
	public List<Code> findForCheckComCode(Code entity);
	
	public Code findByValueWithParent(Code entity);
	public Code findBySort(String sort);
	public Code findUniqueValueByParent(Code entity);
}
