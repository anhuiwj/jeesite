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
			overflow:hidden;
		}
		
		.pwd{width:40px;height:20px;line-height:14px;padding-top:2px;} 
		.pwd_f{color:#BBBBBB;} 
		.pwd_c{background-color:#F3F3F3;border-top:1px solid #D0D0D0;border-bottom:1px solid #D0D0D0;border-left:1px solid #D0D0D0;} 
		.pwd_Weak_c{background-color:#FF4545;border-top:1px solid #BB2B2B;border-bottom:1px solid #BB2B2B;border-left:1px solid #BB2B2B;} 
		.pwd_Medium_c{background-color:#FFD35E;border-top:1px solid #E9AE10;border-bottom:1px solid #E9AE10;border-left:1px solid #E9AE10;} 
		.pwd_Strong_c{background-color:#3ABB1C;border-top:1px solid #267A12;border-bottom:1px solid #267A12;border-left:1px solid #267A12;} 
		.pwd_c_r{border-right:1px solid #D0D0D0;} 
		.pwd_Weak_c_r{border-right:1px solid #BB2B2B;} 
		.pwd_Medium_c_r{border-right:1px solid #E9AE10;} 
		.pwd_Strong_c_r{border-right:1px solid #267A12;} 
		
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
		request.setAttribute("orgCode",user.getCompany().getCode());
	%>
</head>
<body>
	<div class="mini-panel" title="用户信息" style="width:100%;" showCollapseButton="false">
    	<div id="uForm">
    	<input id="id" class="mini-hidden" name="id">
    	
    	<fieldset style="border:solid 1px #aaa;padding:3px;">
    		<legend >基本信息</legend>
    		<table>
    			<tr>
       				<td style="width:30%;">登录名：</td>
               		<td>    
                   		<input id="loginName" name="loginName" class="mini-textbox" required="true"/>
               		</td>
               		<td style="width:30%;">工号：</td>
               		<td>    
                   		<input id="no" name="no" class="mini-textbox" required="true"/>
               		</td>
       			</tr>
        		<tr>
        			<td>密码：</td>
                	<td>    
                    	<input id="password" name="password" class="mini-password" required="true" onKeyUp="checkIntensity"/>
                	</td>
               		<td></td>
               		<td>    
                   		<table border="0" cellpadding="0" cellspacing="0"> 
						  <tr align="center"> 
						    <td id="pwd_Weak" class="pwd pwd_c">&nbsp;</td> 
						    <td id="pwd_Medium" class="pwd pwd_c pwd_f">无</td> 
						    <td id="pwd_Strong" class="pwd pwd_c pwd_c_r">&nbsp;</td> 
						  </tr> 
						</table> 

               		</td>
        		</tr>
        		<tr>
               		<td>重复密码：</td>
               		<td>    
                   		<input id="password2" name="password2" class="mini-password" required="true"/>
               		</td>
               		<td>
               		</td>
               		<td>
               		</td>
        		</tr>
        			
       			<tr>
        			<td>姓名：</td>
                	<td>    
                   		<input id="name" name="name" class="mini-textbox" required="true"/>
               		</td>
               		<td>机构：</td>
               		<td>    
						<!--modify xf 2016,12.8 -->
						<!--demander 徐敏 -->
						<!--机构管理员默认未当前机构，且不可修改-->
						<!--区县管理员默认未当前区县的所有机构，可选，默认值为当前区县-->
						<c:if test="${fns:getUser().admin}"><!-- 如果是超级管理员-->
						<div id="orgCode" name="orgCode" class="mini-treeselect" required="true" resultAsTree="false" style="width:260px;" onbeforenodeselect="beforenodeselect" data="${fns:getOfficeTreeForAdmin('0')}" ></div>
						</c:if>
						<c:if test="${! fns:getUser().admin}"><!-- 如果不是超级管理员-->
							<c:if test="${manager == '1'}">	<!--系统管理员-->
								<div id="orgCode" name="orgCode" class="mini-treeselect" resultAsTree="false" style="width:260px;" onbeforenodeselect="beforenodeselect" data="${fns:getOfficeTreeForArea("310000000000")}" ></div>
							</c:if>
							<c:if test="${manager == '2'}">	<!--区县管理员-->
								<div id="orgCode" name="orgCode" class="mini-treeselect" value="${areaCode}" resultAsTree="false" style="width:260px;" onbeforenodeselect="beforenodeselect" data="${fns:getOfficeTreeForArea(areaCode)}" ></div>
							</c:if>
							<c:if test="${manager == '3'}">	<!--机构管理员-->
								<div id="orgCode" name="orgCode" class="mini-treeselect" value="${orgCode}" resultAsTree="false" style="width:260px;" onbeforenodeselect="beforenodeselect" data="${fns:getOfficeTreeForArea(orgCode)}" ></div>
							</c:if>
						</c:if>

               		</td>
        		</tr>
        		<tr>
       				<td>手机：</td>
               		<td>    
                   		<input id="mobile" name="mobile" class="mini-textbox"/>
               		</td>
               		<td>邮箱：</td>
               		<td>    
                   		<input id="email" name="email" class="mini-textbox"/>
               		</td>
       			</tr>
    		</table>
    	</fieldset>
    	</div>
    	<div class="mini-panel" title="角色" style="width:100%;height:60%;overflow:auto">
       		
       		<div id="roles" name="roles" class="mini-checkboxlist" repeatItems="4" style="width:100%;height:100%;overflow:auto"
				repeatLayout="table" r textField="text" valueField="value"  
					data="${fns:getCurrUserRoleList()}">
			</div>
       		
       </div>
       
       <div style="text-align:center;padding:10px;">               
            <a class="mini-button" onclick="saveUser" iconCls="icon-save" >保存</a>
              
        </div> 
        
    </div>
    
  <%--  <div showCollapseButton="false">
       <div class="mini-panel" title="资源权限" style="width:100%;height:100%;overflow:auto">

       		<ul id="resTree" class="mini-tree" url="${ctx}/sys/user/menuTree" checkRecursive="true"
       			style="width:500px;padding:5px;" showCheckBox="true" 
                    autoCheckParent="true" parentField="parentId" showTreeIcon="true" 
                    textField="name" idField="id" resultAsTree="false" onload="expandResTree()">        
            </ul>
       </div>
        
            
        
    </div>--%>
       

	<script type="text/javascript">
		mini.parse();
		/*var resTree = mini.get('resTree');*/
		var roles = mini.get('roles');
		
		/*
		 *校验密码强度
		 *1. 如果输入的密码位数少于5位，那么就判定为弱。
		 *2. 如果输入的密码只由数字、小写字母、大写字母或其它特殊符号当中的一种组成，则判定为弱。
		 *3. 如果密码由数字、小写字母、大写字母或其它特殊符号当中的两种组成，则判定为中。
		 *4. 如果密码由数字、小写字母、大写字母或其它特殊符号当中的三种以上组成，则判定为强。
		*/
		function checkIntensity(e) {
			var pwd = e.sender.getValue();
			var mColor,wColor,sColor,colorHtml; 
			var m=0; 
			var modes=0; 
			for(i=0; i<pwd.length; i++){ 
				var charType=0; 
				var t=pwd.charCodeAt(i); 
				if(t>=48 && t <=57){
					charType=1;
				} else if(t>=65 && t <=90){
					charType=2;
				} else if(t>=97 && t <=122){
					charType=4;
				} else {
					charType=4;
				} 
				modes |= charType; 
			} 
			
			for(i=0;i<4;i++){
				if(modes & 1){
					m++;
				} 
				modes>>>=1; 
			} 
			if(pwd.length<=4){
				m=1;
			} 
			if(pwd.length<=0){
				m=0;
			} 
			
			switch(m){ 
				case 1 : 
					wColor="pwd pwd_Weak_c"; 
					mColor="pwd pwd_c"; 
					sColor="pwd pwd_c pwd_c_r"; 
					colorHtml="弱"; 
					break; 
				case 2 : 
					wColor="pwd pwd_Medium_c"; 
					mColor="pwd pwd_Medium_c"; 
					sColor="pwd pwd_c pwd_c_r"; 
					colorHtml="中"; 
					break; 
				case 3 : 
					wColor="pwd pwd_Strong_c"; 
					mColor="pwd pwd_Strong_c"; 
					sColor="pwd pwd_Strong_c pwd_Strong_c_r"; 
					colorHtml="强"; 
					break; 
				default : 
					wColor="pwd pwd_c"; 
					mColor="pwd pwd_c pwd_f"; 
					sColor="pwd pwd_c pwd_c_r"; 
					colorHtml="无"; 
					break; 
			} 

			document.getElementById('pwd_Weak').className=wColor; 
			document.getElementById('pwd_Medium').className=mColor; 
			document.getElementById('pwd_Strong').className=sColor; 
			document.getElementById('pwd_Medium').innerHTML=colorHtml; 

		}
		
		/*function expandResTree() {
			resTree = mini.get('resTree');
			resTree.expandAll();
		}*/
		
		function saveUser() {
			var form = new mini.Form("#uForm");
			form.validate();
			if (form.isValid()) { 
				var pwd = mini.get('password').getValue();
				var pwd2 = mini.get('password2').getValue();
				if(pwd != pwd2) {
					mini.get('password').setValue('');
					mini.get('password2').setValue('');
					mini.get('password').focus();
					mini.alert("两次输入密码不同，请重新输入");
					return;
				}
				var orgCode = mini.get("orgCode").getValue();
				if(orgCode.length!=11){
					mini.alert("请选择正确的机构");
					return;
				}

				/*var resTreeIds = resTree.getValue(true);*/
				var roleIds = roles.getValue();
				var data = form.getData(false,false); 
				/*data["resTreeIds"] = resTreeIds;*/
				data["roleIds"] = roleIds;
				
				$.ajax({
	                url: "${ctx}/sys/user/save/",
	                type: "post",
	                data: data,
	                success: function (text) {
	                	var r = mini.decode(text);
	                	if(r.result == 0) {
	                		window.parent.reload('1');
		                	
	                	} else {
	                		if(r.result == 1) {
	                			mini.alert("保存 失败，登录名重复");
	                		}
	                		if(r.result == 2) {
	                			mini.alert("保存 失败，工号重复");
	                		}
	                	}
	                	
	                }
	            });
	           
	           
			} else {
				mini.alert("请填写必填项");
			}
			
		}

		//机构禁止选择父节点
		function beforenodeselect(e) {
			//禁止选中父节点
			if (e.isLeaf == false) e.cancel = true;
		}

    </script>
</body>
</html>