package com.thinkgem.jeesite.modules.sys.service;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.service.TreeService;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.modules.sys.dao.CodeDao;
import com.thinkgem.jeesite.modules.sys.entity.Code;
import com.thinkgem.jeesite.modules.sys.utils.CodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Transactional(readOnly = true)
public class CodeService extends TreeService<CodeDao, Code> {
	
	public List<Code> findByParent(String id) {
		Code code = new Code();
		
		if(StringUtils.isNotBlank(id)) {
			code.setId(id);
			return dao.findByParent(code);
		} else {
			return dao.findTopList(code);
		}
	}
	
	public Code loadDicNode(String id) {
		return dao.get(id);
	}
	
	public boolean checkDicNodeCode(String id,String value,String parentId) {
		Code po = new Code(id);
		po.setValue(value);
		Code parent = new Code(parentId);
		po.setParent(parent);
		List<Code> list = dao.findForCheckCode(po);
		
		if(list == null || list.size()<=0) {
			return true;
		}
		return false;
	}
	
	public boolean checkDicNodeComCode(String id,Long comCode,String parentId) {
		Code po = new Code(id);
		po.setComCode(comCode);
		Code parent = new Code(parentId);
		po.setParent(parent);
		
		List<Code> list = dao.findForCheckComCode(po);
		
		if(list == null || list.size()<=0) {
			return true;
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public int saveNode(Code entity) {
		boolean flag = this.checkDicNodeCode(entity.getId(), entity.getValue(), entity.getParentId());
		if(!flag) {
			return 1;
		}
		if(entity.getComCode() != null && entity.getComCode() >0) {
			flag = this.checkDicNodeComCode(entity.getId(), entity.getComCode(), entity.getParentId());
			if(!flag) {
				return 2;
			}
		}
		super.save(entity);
		removeCache();
		return 0;
	}
	
	
	@Transactional(readOnly = false)
	public void removeDicNode(String id) {
		Code entity = new Code(id);
		super.delete(entity);
		removeCache();
	}
	
	
	public Code getNode(String path,boolean unique) {
		path = path.trim();
		String[] pathArray = this.convertPath(path);
		return this.findNodeByPath(pathArray, unique);
	}
	
	
	/**
	 * 按路径数组查找字典节点
	 * @param path 路径数组
	 * @param unique 是否唯一节点路径
	 * @return
	 */
	private Code findNodeByPath(String[] path,boolean unique){
		Code node = null;
		String parentId = "0";
		String value = null;
		if(unique){//查找代码唯一节点树
			//求出父节点
			Code parentNode = null;
			for(int i = 0; i < path.length-1; i++){
				value = path[i];
				Code qr = new Code();
				Code qrParent = new Code();
				qrParent.setId(parentId);
				qr.setParent(qrParent);
				qr.setValue(value);
				parentNode = dao.findByValueWithParent(qr);
				if(parentNode != null){
					parentId = parentNode.getId();
				}else{
					break;
				}
			}
			if(parentNode != null){
				value = path[path.length-1];
				//在父节点下查找唯一节点
				
				Code qr = new Code();
				Code qrParent = new Code();
				qrParent.setId(parentNode.getId());
				qr.setParent(qrParent);
				qr.setValue(value);
				
				node =dao.findUniqueValueByParent(qr);
			}
		} else {//查找非代码唯一节点树
			for(int i = 0; i < path.length; i++){
				value = path[i];
				Code qr = new Code();
				Code qrParent = new Code();
				qrParent.setId(parentId);
				qr.setParent(qrParent);
				qr.setValue(value);
				node = dao.findByValueWithParent(qr);
				
				if(node != null){
					parentId = node.getId();
				}else{
					break;
				}
			}
			
		}
		if(node != null) {
			String nodePath = calcNodePath(node, Code.PATH_SPERATOR);
			node.setNodePath(nodePath);
			Code qr = new Code(node.getId());
			List<Code> childs = dao.findByParent(qr);
			if(childs != null && childs.size()>0){
				Code child = null;
				for(int i = 0; i < childs.size(); i++){
					child = childs.get(i);
					child.setNodePath(nodePath + Code.PATH_SPERATOR + child.getValue());
				}
			}
			node.setChilds(childs);
		}
		return node;
	}
	
	private String calcNodePath(Code node, String sperator) {
		StringBuffer path = new StringBuffer();
		
		
		Code parent = null;
		if(node.getParent() != null && !node.getParent().getId().equals("0")) {
			parent = dao.get(node.getParent().getId());
		}
		List<String> pathList = new ArrayList<String>();
		while (parent != null) {
			pathList.add(parent.getValue());
			if(parent.getParent() != null && !parent.getParent().getId().equals("0")) {
				parent = dao.get(parent.getParent().getId());
			} else {
				parent = null;
			}
		}
		if (pathList.size() > 0) {
			for (int i = pathList.size() - 1; i >= 0; i--) {
				path.append(pathList.get(i));
				path.append(sperator);
			}
		}
		path.append(node.getValue());

		return path.toString();
	}
	
	
	public List<Code> tree(String path) {
		Code qr = this.getNode(path, false);
		qr.setParentIds("%"+qr.getId()+"%");
		List<Code> list =  dao.findByParentIdsLike(qr);
		Map<String,String> parentValueMap = Maps.newHashMap();
		for(Code code :list) {
			if(code.getParent() != null && StringUtils.isNotBlank(code.getParent().getId())) {
				if(code.getParent().getId().equals(qr.getId()) ||  code.getParent().getId().equals("0") ) {
					code.getParent().setId("-1");
				} else {
					
					if(StringUtils.isNotBlank(code.getParent().getValue())) {
						code.getParent().setId(code.getParent().getValue());
						parentValueMap.put(code.getParent().getValue(), "1");
					} else {
						code.getParent().setId("-1");
					}
					
				}
				
			} else {
				code.getParent().setId("-1");
			}
			
		}
		for(Code code :list) {
			if(parentValueMap.containsKey(code.getValue())){
				code.setLeaf(false);
			} else {
				code.setLeaf(true);
			}
		}
		return list;
	}
	
	
	public List<Code> findChildsByPath(String path) {
		Code qr = this.getNode(path, false);
		qr.setParentIds("%"+qr.getId()+"%");
		List<Code> list =  dao.findByParentIdsLike(qr);
		
		
		return list;
	}
	
	public List<Code> lazyTree(String path,String pid) {
		return null;
	}
	
	
	/**
	 * 将路径串转换成数组
	 * @param path 节点路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->GXYROOT->1->2");
	 * @param unique 是否唯一节点路径
	 * @return
	 */
	private String[] convertPath(String path){
		if(path.indexOf(Code.PATH_SPERATOR) >0) {
			path = path.replaceAll(Code.PATH_SPERATOR, "%");
		} else {
			path = path.replaceAll("-", "%");
		}
		String[] result = StringUtils.split(path, "%");
		if(result.length < 2){
			throw new RuntimeException("path is invalid");
		}
		return result;
	}
	
	
	private void removeCache() {
		CacheUtils.getCacheManager().removeCache(CodeUtils.CACHE_DICT);
		CacheUtils.getCacheManager().removeCache(CodeUtils.CACHE_DICT_UNIQUE);
	}

}
