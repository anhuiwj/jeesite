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
			overflow:hidden;
		}
		
		
		
	</style>
</head>
<body>
    	
    	<input id="id" class="mini-hidden" name="id" value="${role.id }">
    	<fieldset style="border:solid 1px #aaa;padding:3px;">
    		<legend >基本信息</legend>
    		<table>
    			<tr>
       				<td style="width:10%;">名称：</td>
               		<td style="width:20%;">    
                   		<input id="name" name="name" class="mini-textbox" required="true" value="${role.name }" enabled="false"/>
               		</td>
               		<td style="width:10%;">英文名：</td>
               		<td style="width:30%;">    
                   		<input id="enname" name="enname" class="mini-textbox" required="true" value="${role.enname }" enabled="false"/>
               		</td>
               		<td style="width:10%;">机构：</td>
               		<td style="width:30%;">    
                   		<div id="orgCode" name="orgCode" enabled="false" value="${role.office.code }" class="mini-treeselect" resultAsTree="false" style="width:260px;" data="${fns:getOfficeTreeForAdmin('1')}" ></div>
               		</td>
       			</tr>
        		
    		</table>
    	</fieldset>
    	
    	
    
       <div class="mini-panel" title="包含用户列表" style="width:100%;height:80%;overflow:auto">

       	<div style="width:100%;">
        <div class="mini-toolbar" style="border-bottom:0;padding:0px;">
            <table style="width:100%;">
                <tr>
                    <td style="width:100%;">
                        <a class="mini-button" iconCls="icon-add" onclick="openUserWin()" >添加角色用户</a>           
                    </td>
                </tr>
            </table>           
        </div>
    </div>
	<div id="uGrid" class="mini-datagrid" fit="true"  url="${ctx}/sys/role/queryUserByRole?rid=${role.id }" idField="id" allowResize="true" showPager="false" >
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
    
    
    <div id="userWin" class="mini-window" title="添加角色用户" style="width:80%;height:80%;" 
    	showMaxButton="true" showCollapseButton="true" showShadow="true" 
    	showToolbar="true" showModal="false" allowResize="true" allowDrag="true">
    	
    	<div style="width:100%;">
        <div class="mini-toolbar" style="border-bottom:0;padding:0px;">
            <table style="width:100%;">
                <tr>
                    <td style="width:100%;">
                        <a class="mini-button" iconCls="icon-save" onclick="assignUser()" >保存分配</a>           
                    </td>
                    <td style="white-space:nowrap;">
                       	姓名：<input id="name" name="name" class="mini-textbox"  style="width:80px;"/>   
                       	登录名：<input id="loginName" name="loginName" class="mini-textbox"  style="width:80px;"/> 
                       	<c:if test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->  
                       	归属机构：<div id="orgCode" name="orgCode" class="mini-treeselect" resultAsTree="false" style="width:200px;" data="${fns:getOfficeTreeForAdmin('1')}" ></div>
                       	</c:if>
                        <a class="my-btn green"  plain="false" onclick="queryUser()">查询</a>
                        <a class="my-btn"  plain="false" onclick="onUndo()">重置</a>
                    </td>
                </tr>
            </table>           
        </div>
    </div>
	<div id="userSelectGrid" class="mini-datagrid" fit="true" multiSelect="true" url="${ctx}/sys/role/queryUserForAssign?rid=${role.id }" idField="id" allowResize="true" showPager="false" >
    	<div property="columns">
        	<div type="checkcolumn"></div>
        	<div name="id" field="id"  visible="false">id</div>
        	<div name="name" field="name" width="60" >姓名</div>
        	<div field="loginName" field="loginName" width="80">登录名</div>
        	<div field="companyName" field="companyName" width="160">归属机构</div>
        	<div field="no" field="no" width="60" align="right">工号</div>
        	<div field="phone" field="phone" width="80" align="right">电话</div>   
    	</div>
	</div>
    </div>
        
            
        
    
       

	
	
	
	<script type="text/javascript">
		mini.parse();
		var resTree = mini.get('resTree');
		var uGrid = mini.get('uGrid');
		uGrid.load();
		
		function openUserWin() {
			var win = mini.get("userWin");
			win.showAtPos('center', 'middle');
			var usGrid = mini.get('userSelectGrid');
			usGrid.load();
			
		}
		
		function queryUser() {
			var orgCode = '';
			var name = mini.get('name').getValue();
			var loginName = mini.get('loginName').getValue();
			<c:if test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->  
			orgCode = mini.get('orgCode').getValue();
           	</c:if>
			var loginName = mini.get('loginName').getValue();
			var param = {
				name:name,
				loginName:loginName,
				orgCode:orgCode
			}
			var usGrid = mini.get('userSelectGrid');
			usGrid.load(param);
		}
		
		function onUndo() {
			mini.get('name').setValue('');
			mini.get('loginName').setValue('');
			<c:if test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->  
			mini.get('orgCode').setValue('');
			</c:if>
		}
		
		function assignUser() {
			var usGrid = mini.get('userSelectGrid');
			var rows = usGrid.getSelecteds();
			if (rows.length > 0) {
				var ids = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    ids.push(r.id);
                }
                var id = ids.join(',');
                usGrid.loading("操作中，请稍后......");
                $.ajax({
	                url: "${ctx}/sys/role/assignUserToRole?uids="+id+"&rid=${role.id }",
	                type: "post",
	                success: function (text) {
	                	var r = mini.decode(text);
	                	if(r.result == 0) {
	                		var win = mini.get("userWin");
	                		win.hide();
	                		uGrid.load();
	                	} 
	                	
	                }
	            }); 
			} else {
				 alert("请选中一条记录");
			}
			
		}
		
		function onOprRenderer(e) {
    		var record = e.record;
    		var returnStr = '<a href="javascript:removeUserInRole(\''+record.id+'\')">删除</a>&nbsp;';
    		return returnStr;
    	}
		
		function removeUserInRole(uid) {
			mini.confirm("确定删除？", "确定？",
		   		function (action) {
		    		if(action == "ok") {
		    	    	$.ajax({
		    	    		url: "${ctx}/sys/role/removeUser?uid="+uid+"&rid=${role.id }",
		    	            type: "post",
		    	            success: function (text) {
		    	            	mini.alert("删除成功");
		    	            	uGrid.reload();        	
		    	            }
		    	        });
		    	    }        
		    	}
		    );
		}
		
		
    


    </script>
</body>
</html>