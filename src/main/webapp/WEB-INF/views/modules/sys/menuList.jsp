<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>菜单管理</title>
	<meta name="decorator" content="miniui"/>
	<link rel="stylesheet" href="${ctxStatic}/css/my-mini.css" />
</head>
<body>
	<h4 class="h4-title"><span>菜单管理</span></h4>  
	<div style="width:100%;">
        <div class="mini-toolbar" style="border-bottom:0;padding:0px;">
            <table style="width:100%;">
                <tr>
                    <td style="width:100%;">
                    	<c:if test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->
                        <a class="mini-button" iconCls="icon-add" onclick="addTop()">增加顶级菜单</a> 
                        </c:if>    
                    </td>
                </tr>
            </table>           
        </div>
    </div>
    
	<div id="mGrid" class="mini-treegrid" fit="true"  url="${ctx}/sys/menu/treeData" showTreeIcon="true" 
    	treeColumn="name" idField="id" allowResize="true" onbeforeload="onBeforeTreeLoad">
    	<div property="columns">
        	<div type="indexcolumn"></div>
        	<div name="id" field="id"  visible="false">id</div>
        	<div name="name" field="name" width="160" >名称</div>
        	<div field="href" field="code" width="80">链接</div>
        	<div name="type" field="type"  visible="false">type</div>
        	<div field="typeName" field="typeName" width="60" align="right">类型</div>
        	<div name="sysflag" field="sysflag"  visible="false">sysflag</div>
        	<div field="sysflagName" field="sysflagName" width="60" align="right">级别</div>
        	<div field="permission" field="permission" width="120" align="right">权限标识</div>
        	<div name="opr" width="120" renderer="onOprRenderer">操作</div>                   
    	</div>
	</div>
	
	<div id="mWin" class="mini-window" title="菜单资源" style="width:50%;height:43%;" 
    	showMaxButton="true" showCollapseButton="true" showShadow="true" 
    	showToolbar="true" showModal="false" allowResize="true" allowDrag="true">
    	
    	
            
        <div id="mForm" style="padding-left:11px;padding-bottom:5px;">
        	<input id="id" class="mini-hidden" name="id">
        	<input id="pid" class="mini-hidden" name="parent.id">
        	<fieldset style="border:solid 1px #aaa;padding:3px;">
        		<table>
        			<tr>
        				<td style="width:20%;">上级菜单：</td>
        				<td style="width:40%;" ><span id="parentSpan"></span></td>
        				<td>顺序：</td>
        				<td>    
	                        <input id=sort name="sort" class="mini-spinner" minValue="1"  required="true" ;"/>
	                    </td>
        			</tr>
        			<tr>
        				<td style="width:70px;">名称：</td>
        				<td style="width:90%;" colspan="3">    
	                        <input id="name" name="name" class="mini-textbox" required="true" style="width:98%;"/>
	                    </td>
        			</tr>
	               	<tr>
        				<td>链接：</td>
        				<td colspan="3">    
	                        <input id="href" name="href" class="mini-textbox" style="width:98%;"/>
	                    </td>
        			</tr>
        			<tr>
        				<td>目标：</td>
        				<td colspan="3">    
	                        <input id="target" name="target" class="mini-textbox" style="width:98%;"/>
	                    </td>
        			</tr>
        			<tr>
        				<td>权限标记：</td>
        				<td colspan="3">    
	                        <input id="permission" name="permission" class="mini-textbox" style="width:98%;"/>
	                    </td>
        			</tr>
        			<tr>
        				<td>类型：</td>
        				<td>    
	                        <select id="type" name="type" class="mini-radiobuttonlist" required="true" >
	                        	<option value="1">菜单</option>
	                        	<option value="2">按钮</option>
	                    	</select>
	                    </td>
	                    <td>级别：</td>
        				<td>    
	                        <select id="sysflag" name="sysflag" class="mini-radiobuttonlist" required="true" >
	                        	<option value="0">系统级</option>
	                        	<option value="1">业务级</option>
	                    	</select>
	                    </td>
	                    
        			</tr>
        			
        			
	                      
            	</table>	
        	</fieldset>
        	          
        </div>
        <div style="text-align:center;padding:10px;">               
            <a class="mini-button" onclick="saveMenu" style="width:60px;margin-right:20px;">确定</a>       
            <a class="mini-button" onclick="onCancel" style="width:60px;">取消</a>       
        </div>  
       
    
	</div>
	
	<script type="text/javascript">
		mini.parse();
		var win = mini.get("mWin");
		var form = new mini.Form("#mForm");
		var grid = mini.get("mGrid");
		var node;//当前节点
		
		
		
    	function onBeforeTreeLoad(e) {
        	var tree = e.sender;    //树控件
        	var params = e.params;  //参数对象
        	if(e.node) {
        		//可以传递自定义的属性
            	params.id = e.node.id; //后台：request对象获取"id"
        	}
        	
    	}
    	
    	
    	
    	function addTop() {
    		form.clear();
    		$('#parentSpan').text('无');
    		mini.get('pid').setValue('1');
    		mini.get('type').setValue('1');
    		mini.get('sysflag').setValue('1');
    		win.showAtPos('center', 'middle');
    	}
    	
    	function onCancel() {
    		win.hide();
    	}
    	
    	function addChild(pid,pName,nodeId) {
    		form.clear();
    		mini.get('pid').setValue(pid);
    		mini.get('type').setValue('1');
    		mini.get('sysflag').setValue('1');
    		$('#parentSpan').text(pName);
    		win.showAtPos('center', 'middle');
    		node = grid.getNode(nodeId);
    		
    	}
    	
    	function modify(id,nodeId) {
    		form.clear();
    		$.ajax({
                url: "${ctx}/sys/menu/load/"+id,
                type: "post",
                success: function (text) {
                    var data = mini.decode(text);   //反序列化成对象
                    $('#parentSpan').text(data.parentName);
                    form.setData(data);
                }
            });
    		win.showAtPos('center', 'middle');
    		var n = grid.getNode(nodeId);
    		var pn = grid.getParentNode(n);
    		if(pn.code) {
    			node = pn;
    		} else {
    			var temp;
    			node = temp;
    		}
    	}
    	
    	function remove(id,nodeId) {
    		mini.confirm("确定删除记录？", "确定？",
    	    	function (action) {
    	        	if(action == "ok") {
    	        		$.ajax({
    	                    url: "${ctx}/sys/menu/remove/"+id,
    	                    type: "post",
    	                    success: function (text) {
    	                    	mini.alert("删除成功");
    	                    	var n = grid.getNode(nodeId);
    	                		var pn = grid.getParentNode(n);
    	                		if(pn.code) {
    	                			node = pn;
    	                		} else {
    	                			var temp;
    	                			node = temp;
    	                		}
    	                		if(node) {
    		                		grid.loadNode(node);
    		                	} else {
    		                		grid.reload();
    		                	}
    	                    }
    	                });
    	        	}        
    			}
    	    );
    		
    	}
    	
    	function saveMenu() {
            form.validate();
			if (form.isValid()) { 
				var data = form.getData(false,false); 
	            var json = mini.encode(data);   //序列化成JSON
	            $.ajax({
	                url: "${ctx}/sys/menu/save/",
	                type: "post",
	                data: data,
	                success: function (text) {
	                	var r = mini.decode(text);
	                	if(r.result == 0) {
	                		mini.alert("保存成功");
		                	win.hide();
		                	if(node) {
		                		grid.loadNode(node);
		                	} else {
		                		grid.reload();
		                	}
	                	} 
	                	
	                }
	            });
			} else {
				mini.alert("请填写必填项");
			}
            
    	}
    	
    	function onOprRenderer(e) {
    		var record = e.record;
    		var returnStr = "";
    		<c:if test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->
    		returnStr += '<a href="javascript:modify(\''+record.id+'\',\''+e.node.id+'\')">修改</a>&nbsp;';
    		returnStr += '<a href="javascript:remove(\''+record.id+'\',\''+e.node.id+'\')">删除</a>&nbsp;';
    		returnStr += '<a href="javascript:addChild(\''+record.id+'\',\''+record.name+'\',\''+e.node.id+'\')">添加下级</a>&nbsp;';
    		</c:if>
    		return returnStr;
    	}
    	
    	

    


    </script>
</body>
</html>