<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>角色管理</title>
	<meta name="decorator" content="miniui"/>
	<link rel="stylesheet" href="${ctxStatic}/css/my-mini.css" />
	<style type="text/css">
		body {
			margin: 0;
			padding: 0;
			border: 0;
			width: 100%;
			height: 100%;
			overflow: hidden;
		}
</style>
</head>
<body>
<div id="mainTabs"  class="mini-tabs" activeIndex="0" style="width:100%;height:100%;">
    <div name="roleList" title="角色管理" style="width:100%;" >
	
	<div style="width:100%;">
        <div class="mini-toolbar" style="border-bottom:0;padding:0px;">
            <table style="width:100%;">
                <tr>
                    <td style="width:100%;">
                        <a class="mini-button" iconCls="icon-add" onclick="addRole()" >添加角色</a>           
                    </td>
                </tr>
            </table>           
        </div>
    </div>
	<div id="rGrid" class="mini-datagrid" fit="true"  url="${ctx}/sys/role/pageQuery" idField="id" allowResize="true" pageSize="10" >
    	<div property="columns">
        	<div type="indexcolumn"></div>
        	<div name="id" field="id"  visible="false">id</div>
        	<div name="name" field="name" width="100" >名称</div>
        	<div field="enname" field="enname" width="100">英文名称</div>
        	<div name="companyCode" field="companyCode"  visible="false">companyCode</div>
        	<div name="companyName" field="companyName" width="160">归属机构</div>
        	<div name="opr" width="100" renderer="onOprRenderer">操作</div>       
    	</div>
	</div>
	</div>
</div>
	
	
	
	<script type="text/javascript">
		mini.parse();
		var tabs=mini.get('mainTabs');
		var grid = mini.get('rGrid');
		grid.load();
		
		
		
		
		function addRole() {
			var url="${ctx}/sys/role/form";
	    	var formTab = tabs.getTab("formTab");
	    	if(!formTab) {
	    		formTab = { name:"formTab",title: "添加角色", url:url, showCloseButton: true};
	    		tabs.addTab(formTab);
	    	    tabs.activeTab(formTab);
	    	} else {
	    		tabs.removeTab(formTab);
	    		formTab = { name:"formTab",title: "添加角色", url:url, showCloseButton: true };
	    		tabs.addTab(formTab);
	    		tabs.activeTab(formTab);
	    	}
		}
		
		function onOprRenderer(e) {
    		var record = e.record;
    		
    		var returnStr = "";
    		<c:choose>
    		<c:when test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->
    		returnStr += '<a href="javascript:assign(\''+record.id+'\')">分配</a>&nbsp;';
    		returnStr += '<a href="javascript:modify(\''+record.id+'\')">修改</a>&nbsp;';
    		returnStr += '<a href="javascript:remove(\''+record.id+'\')">删除</a>&nbsp;';
    		</c:when>
    		<c:otherwise>
    		returnStr += '<a href="javascript:assign(\''+record.id+'\')">分配</a>&nbsp;';
    		var userOrgCode = '${fns:getUser().companyCode}';
			if(userOrgCode == record.companyCode) {
				returnStr += '<a href="javascript:modify(\''+record.id+'\')">修改</a>&nbsp;';
	    		returnStr += '<a href="javascript:remove(\''+record.id+'\')">删除</a>&nbsp;';
			}
    		</c:otherwise> 
    		</c:choose>
    		
    		return returnStr;
    	}
		
		function assign(id) {
			var url="${ctx}/sys/role/assign?id="+id;
	    	var assignTab = tabs.getTab("assignTab");
	    	if(!assignTab) {
	    		assignTab = { name:"assignTab",title: "分配角色用户", url:url, showCloseButton: true};
	    		tabs.addTab(assignTab);
	    	    tabs.activeTab(assignTab);
	    	} else {
	    		tabs.removeTab(assignTab);
	    		formTab = { name:"assignTab",title: "分配角色用户", url:url, showCloseButton: true };
	    		tabs.addTab(assignTab);
	    		tabs.activeTab(assignTab);
	    	}
		}
		
		function modify(uid) {
			var url="${ctx}/sys/role/edit?id="+uid;
	    	var formTab = tabs.getTab("editTab");
	    	if(!formTab) {
	    		formTab = { name:"editTab",title: "修改角色", url:url, showCloseButton: true};
	    		tabs.addTab(formTab);
	    	    tabs.activeTab(formTab);
	    	} else {
	    		tabs.removeTab(formTab);
	    		formTab = { name:"editTab",title: "修改角色", url:url, showCloseButton: true };
	    		tabs.addTab(formTab);
	    		tabs.activeTab(formTab);
	    	}
		}
		
		function remove(id) {
			mini.confirm("确定删除记录？", "确定？",
	    	    function (action) {
	    	        if(action == "ok") {
	    	        	$.ajax({
	    	            	url: "${ctx}/sys/role/remove/"+id,
	    	                type: "post",
	    	                success: function (text) {
	    	                	mini.alert("删除成功");
	    	                   	grid.reload();
	    		                	
	    	                }
	    	            });
	    	       	}        
	    		}
	    	);
		}
		
		function reload(flag) {
			
			if(flag == '1') {
				var formTab = tabs.getTab("formTab");
				tabs.removeTab(formTab);
			}
			if(flag == '2') {
				var editTab = tabs.getTab("editTab");
				tabs.removeTab(editTab);
			}
			var listTab = tabs.getTab("roleList");
			tabs.activeTab(listTab);
			grid.reload();
			mini.alert("保存成功");
		}


    </script>
</body>
</html>