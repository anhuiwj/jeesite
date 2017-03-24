<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.thinkgem.jeesite.modules.sys.entity.User" %>
<%@ page import="com.thinkgem.jeesite.modules.sys.utils.UserUtils" %>
<%@ page import="com.thinkgem.jeesite.modules.sys.entity.Role" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
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
	<%
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
		request.setAttribute("manager",manager);
		request.setAttribute("areaCode",user.getCompany().getArea().getCode());
	%>
</head>
<body>
<div id="mainTabs"  class="mini-tabs" activeIndex="0" style="width:100%;height:100%;">
    <div name="userList" title="用户管理" style="width:100%;" >
	
	<div style="width:100%;">
        <div class="mini-toolbar" style="border-bottom:0;padding:0px;">
            <table style="width:100%;">
                <tr>
                    <td style="width:100%;">
                        <a class="mini-button" iconCls="icon-add" onclick="addUser()" >添加用户</a>           
                    </td>
                    <td style="white-space:nowrap;">
                       	姓名：<input id="name" name="name" class="mini-textbox"  style="width:100px;"/>   
                       	登录名：<input id="loginName" name="loginName" class="mini-textbox"  style="width:100px;"/>

                       	<c:if test="${fns:getUser().admin}"><!-- 如果是超级管理员-->
                       		归属机构：<div id="orgCode" name="orgCode" class="mini-treeselect" resultAsTree="false" style="width:260px;" data="${fns:getOfficeTreeForAdmin('1')}" ></div>
                       	</c:if>
						<c:if test="${! fns:getUser().admin}"><!-- 如果不是超级管理员-->
							<c:if test="${manager == '1'}">	<!--系统管理员-->
								归属机构：<div id="orgCode" name="orgCode" class="mini-treeselect" resultAsTree="false" style="width:260px;" data="${fns:getOfficeTreeForArea("310000000000")}" ></div>
							</c:if>
							<c:if test="${manager == '2'}">	<!--区县管理员-->
								归属机构：<div id="orgCode" name="orgCode" class="mini-treeselect" resultAsTree="false" style="width:260px;" data="${fns:getOfficeTreeForArea(areaCode)}" ></div>
							</c:if>
							<c:if test="${manager == '3'}">	<!--机构管理员，当前自己机构，不需要做任何事情-->

							</c:if>
						</c:if>

                        <a class="my-btn green"  plain="false" onclick="queryUser()">查询</a>
                        <a class="my-btn"  plain="false" onclick="onUndo()">重置</a>
                    </td>
                </tr>
            </table>           
        </div>
    </div>
	<div id="uGrid" class="mini-datagrid" fit="true"  url="${ctx}/sys/user/pageQuery" idField="id" allowResize="true" pageSize="10" >
    	<div property="columns">
        	<div type="indexcolumn"></div>
        	<div name="id" field="id"  visible="false">id</div>
        	<div name="name" field="name" width="60" >姓名</div>
        	<div field="loginName" field="loginName" width="80">登录名</div>
        	<div field="companyName" field="companyName" width="160">归属机构</div>
        	<div field="no" field="no" width="60" align="right">工号</div>
        	<div field="phone" field="phone" width="80" align="right">电话</div>
        	<div name="opr" width="100" renderer="onOprRenderer">操作</div>       
    	</div>
	</div>
	</div>
</div>
	
	
	
	<script type="text/javascript">
		mini.parse();
		var tabs=mini.get('mainTabs');
		var grid = mini.get('uGrid');
		grid.load();
		
		function queryUser() {
            //姓名
			var name = mini.get('name').getValue();

            //登录名
            var loginName = mini.get('loginName').getValue();

            //机构
            var orgCode = '';
			<c:if test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->
			orgCode = mini.get('orgCode').getValue();
           	</c:if>
            <c:if test="${! fns:getUser().admin}"><!-- 如果不是超级管理员 而是 区县管理员和系统管理员-->
                <c:if test="${manager == '1' or manager == '2'}">
                    orgCode = mini.get('orgCode').getValue();
                </c:if>
            </c:if>


			var param = {
				name:name,
				loginName:loginName,
				orgCode:orgCode
			}
			grid.load(param);
		}
		
		function onUndo() {
			mini.get('name').setValue('');
			mini.get('loginName').setValue('');
            var orgCode = mini.get('orgCode');
            if(orgCode){
                orgCode.setValue('');
            }

		}
		
		function addUser() {
			var url="${ctx}/sys/user/form";
	    	var formTab = tabs.getTab("formTab");
	    	if(!formTab) {
	    		formTab = { name:"formTab",title: "添加用户", url:url, showCloseButton: true};
	    		tabs.addTab(formTab);
	    	    tabs.activeTab(formTab);
	    	} else {
	    		tabs.removeTab(formTab);
	    		formTab = { name:"formTab",title: "添加用户", url:url, showCloseButton: true };
	    		tabs.addTab(formTab);
	    		tabs.activeTab(formTab);
	    	}
		}
		
		function onOprRenderer(e) {
    		var record = e.record;
    		
    		var returnStr = "";
    		returnStr += '<a href="javascript:modify(\''+record.id+'\')">修改</a>&nbsp;';
    		returnStr += '<a href="javascript:remove(\''+record.id+'\')">删除</a>&nbsp;';
    		
    		return returnStr;
    	}
		
		function modify(uid) {
			var url="${ctx}/sys/user/edit?id="+uid;
	    	var formTab = tabs.getTab("editTab");
	    	if(!formTab) {
	    		formTab = { name:"editTab",title: "修改用户", url:url, showCloseButton: true};
	    		tabs.addTab(formTab);
	    	    tabs.activeTab(formTab);
	    	} else {
	    		tabs.removeTab(formTab);
	    		formTab = { name:"editTab",title: "修改用户", url:url, showCloseButton: true };
	    		tabs.addTab(formTab);
	    		tabs.activeTab(formTab);
	    	}
		}
		
		function remove(uid) {
			mini.confirm("确定删除记录？", "确定？",
	    	    function (action) {
	    	        if(action == "ok") {
	    	        	$.ajax({
	    	            	url: "${ctx}/sys/user/remove/"+uid,
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
			var listTab = tabs.getTab("userList");
			tabs.activeTab(listTab);
			grid.reload();
			mini.alert("保存成功");
		}


    </script>
</body>
</html>