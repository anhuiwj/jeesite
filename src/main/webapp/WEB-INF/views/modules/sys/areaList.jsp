<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>区域管理</title>
	<meta name="decorator" content="miniui"/>
	<link rel="stylesheet" href="${ctxStatic}/css/my-mini.css" />
</head>
<body>
	<h4 class="h4-title"><span>区域管理</span></h4>
	<div style="width:100%;">
        <div class="mini-toolbar" style="border-bottom:0;padding:0px;">
            <table style="width:100%;">
                <tr>
                    <td style="width:100%;">
                    	<c:if test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->
                        <a class="mini-button" iconCls="icon-add" onclick="addTop()">增加顶级区域</a> 
                        </c:if>    
                    </td>
                </tr>
            </table>           
        </div>
    </div>
	<div id="areaGrid" class="mini-treegrid" fit="true"  url="${ctx}/sys/area/treeData" showTreeIcon="true" 
    	treeColumn="name" idField="id" allowResize="true" onbeforeload="onBeforeTreeLoad">
    	<div property="columns">
        	<div type="indexcolumn"></div>
        	<div name="id" field="id"  visible="false">id</div>
        	<div name="name" field="name" width="140" >区域名称</div>
        	<div field="code" field="code" width="80">区域编码</div>
        	<div name="type" field="type"  visible="false">type</div>
        	<div field="typeName" field="typeName" width="60" align="right">类型</div>
        	<div name="opr" width="120" renderer="onOprRenderer">操作</div>                   
    	</div>
	</div>
	
	<div id="areaWin" class="mini-window" title="行政区域" style="width:38%;height:35%;" 
    	showMaxButton="true" showCollapseButton="true" showShadow="true" 
    	showToolbar="true" showModal="false" allowResize="true" allowDrag="true">
    	<div property="toolbar" style="padding:5px;">
        	<input type='button' value='保存' onclick="saveArea()" style='vertical-align:middle;'/>
    	</div>
    	
            
        <div id="areaForm" style="padding:5px;">
        	<input id="id" class="mini-hidden" name="id">
        	<input id="pid" class="mini-hidden" name="parent.id">
        	<table>
        		<tr>
                	<td >上级区域：</td>
                	<td colspan="3">    
                   		<span id="parentSpan"></span>
                	</td>
            	</tr> 
            	<tr>
                	<td style="width:30%;">名称：</td>
                	<td style="width:20%;">    
                   		<input id="name" name="name" class="mini-textbox" required="true"/>
                	</td>
                	<td style="width:20%;">代码：</td>
                	<td>                        
                    	<input id="code" name="code" class="mini-textbox" required="true"/>
                	</td>
                
            	</tr>
            	<tr>
            		<td>类型：</td>
                	<td>    
                    	<span id="typeSpan"></span>
                	</td>
                	<td>顺序：</td>
                	<td>    
                    	<input id=sort name="sort" class="mini-spinner" minValue="1" required="true"/>
                	</td>
                
            	</tr>
        	</table>            
        </div>
       
    
	</div>
	
	<script type="text/javascript">
		mini.parse();
		var win = mini.get("areaWin");
		var form = new mini.Form("#areaForm");
		var grid = mini.get("areaGrid");
		var node;//当前节点
		
    	function onBeforeTreeLoad(e) {
        	var tree = e.sender;    //树控件
        	var params = e.params;  //参数对象
        	if(e.node) {
        		//可以传递自定义的属性
            	params.code = e.node.code; //后台：request对象获取"code"
        	}
        	
    	}
    	
    	function addTop() {
    		form.clear();
    		$('#typeSpan').text('省份、直辖市');
    		$('#parentSpan').text('无');
    		win.showAtPos('center', 'middle');
    	}
    	
    	function addChild(pid,pType,pName,nodeId) {
    		form.clear();
    		mini.get('pid').setValue(pid);
    		if(pType == '2') {
    			$('#typeSpan').text('地市');
    		}
    		if(pType == '3') {
    			$('#typeSpan').text('区县');
    		}
    		if(pType == '4') {
    			$('#typeSpan').text('街道（镇）');
    		}
    		
    		if(pType == '5') {
    			$('#typeSpan').text('居委（村）');
    		}
    		
    		$('#parentSpan').text(pName);
    		win.showAtPos('center', 'middle');
    		node = grid.getNode(nodeId);
    		
    	}
    	
    	function modify(id,nodeId) {
    		form.clear();
    		$.ajax({
                url: "${ctx}/sys/area/load/"+id,
                type: "post",
                success: function (text) {
                    var data = mini.decode(text);   //反序列化成对象
                    $('#typeSpan').text(data.typeName);
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
    	                    url: "${ctx}/sys/area/remove/"+id,
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
    		                		grid.loadNode (node);
    		                	} else {
    		                		grid.reload();
    		                	}
    	                    }
    	                });
    	        	}        
    			}
    	    );
    		
    	}
    	
    	function saveArea() {
            form.validate();
			if (form.isValid()) { 
				var data = form.getData(false,false); 
	            var json = mini.encode(data);   //序列化成JSON
	            $.ajax({
	                url: "${ctx}/sys/area/save/",
	                type: "post",
	                data: data,
	                success: function (text) {
	                	var r = mini.decode(text);
	                	if(r.result == 0) {
	                		mini.alert("保存成功");
		                	win.hide();
		                	if(node) {
		                		grid.loadNode (node);
		                	} else {
		                		grid.reload();
		                	}
		                	
	                	} else {
	                		mini.alert("保存 失败，区域代码重复");
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
    		if(record.type != '6') {
    			returnStr += '<a href="javascript:addChild(\''+record.id+'\',\''+record.type+'\',\''+record.name+'\',\''+e.node.id+'\')">添加下级</a>&nbsp;';
    		}
    		</c:if>
    		return returnStr;
    	}
    	
    	

    


    </script>
</body>
</html>