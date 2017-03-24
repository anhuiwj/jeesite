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
		<div style="text-align:left;padding:10px;">               
            <a class="mini-button" onclick="saveRole" iconCls="icon-save" >保存</a>       
              
        </div> 
    
    	<div id="rForm">
    	<fieldset style="border:solid 1px #aaa;padding:3px;">
    		<legend >基本信息</legend>
    		<table>
    			<tr>
       				<td style="width:10%;">名称：</td>
               		<td style="width:20%;">    
                   		<input id="name" name="name" class="mini-textbox" required="true"/>
               		</td>
               		<td style="width:10%;">英文名：</td>
               		<td style="width:30%;">    
                   		<input id="enname" name="enname" class="mini-textbox" required="true"/>
               		</td>
               		<c:if test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->
               		<td style="width:10%;">机构：</td>
               		<td style="width:30%;">    
                   		<div id="orgCode" name="orgCode" class="mini-treeselect" resultAsTree="false" style="width:260px;" data="${fns:getOfficeTreeForAdmin('1')}" ></div>
               		</td>
               		</c:if>
       			</tr>
        		
    		</table>
    	</fieldset>
    	</div>
    	
    
       <div class="mini-panel" title="资源权限" style="width:100%;height:80%;overflow:auto">

       		<ul id="resTree" class="mini-tree" url="${ctx}/sys/role/menuTree" checkRecursive="true"
       			style="width:500px;padding:5px;" showCheckBox="true" 
                    autoCheckParent="true" parentField="parentId" showTreeIcon="true" 
                    textField="name" idField="id" resultAsTree="false" onload="expandResTree()">        
            </ul>
       </div>
        
            
        
    
       

	
	
	
	<script type="text/javascript">
		mini.parse();
		var resTree = mini.get('resTree');
		
		function expandResTree() {
			resTree = mini.get('resTree');
			resTree.expandAll();
		}
		
		function saveRole() {
			var form = new mini.Form("#rForm");
			form.validate();
			if (form.isValid()) { 
				var resTreeIds = resTree.getValue(true);
				var data = form.getData(false,false); 
				data["resTreeIds"] = resTreeIds;
				
				$.ajax({
	                url: "${ctx}/sys/role/save/",
	                type: "post",
	                data: data,
	                success: function (text) {
	                	var r = mini.decode(text);
	                	if(r.result == 0) {
	                		window.parent.reload('1');
		                	
	                	} else {
	                		if(r.result == 1) {
	                			mini.alert("保存 失败，名称重复");
	                		}
	                		if(r.result == 2) {
	                			mini.alert("保存 失败，英文名重复");
	                		}
	                	}
	                	
	                }
	            }); 
			} else {
				mini.alert("请填写必填项");
			}
			
		}
    


    </script>
</body>
</html>