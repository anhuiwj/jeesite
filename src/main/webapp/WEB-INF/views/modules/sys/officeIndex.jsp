<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="miniui"/>
	<link rel="stylesheet" href="${ctxStatic}/css/my-mini.css" />
</head>
<body>
	<h4 class="h4-title"><span>机构管理</span></h4>
	<!--  
	<div style="width:100%;">
        <div class="mini-toolbar" style="border-bottom:0;padding:0px;">
            <table style="width:100%;">
                <tr>
                    <td style="width:100%;">
                        <a class="mini-button" iconCls="icon-add" onclick="addTop()">增加顶级机构</a>     
                    </td>
                </tr>
            </table>           
        </div>
    </div>
    -->
	<div id="ofGrid" class="mini-treegrid" fit="true"  url="${ctx}/sys/office/treeData" showTreeIcon="true" 
    	treeColumn="name" idField="id" allowResize="true" onbeforeload="onBeforeTreeLoad" onload="initLoad">
    	<div property="columns">
        	<div type="indexcolumn"></div>
        	<div name="id" field="id"  visible="false">id</div>
        	<div name="name" field="name" width="200" >名称</div>
        	<div name="code" field="code" width="160">编码</div>
        	<div name="type" field="type"  visible="false">type</div>
        	<div name="typeName" field="typeName" width="60" align="right">类型</div>
        	<div name="grade" field="grade"  visible="false">grade</div>
        	<div name="gradeName" field="gradeName" width="60" align="right">分级</div>
        	<div name="lv" field="lv"  visible="false">type</div>
        	<div name="lvName" field="lvName" width="60" align="right">分等</div>
        	<div name="opr" width="120" renderer="onOprRenderer">操作</div>                   
    	</div>
	</div>
	
	<div id="ofWin" class="mini-window" title="组织机构" style="width:50%;height:65%;" 
    	showMaxButton="true" showCollapseButton="true" showShadow="true" 
    	showToolbar="true" showModal="false" allowResize="true" allowDrag="true">
    	
    	
            
        <div id="ofForm" style="padding-left:11px;padding-bottom:5px;">
        	<input id="id" class="mini-hidden" name="id">
        	<input id="pid" class="mini-hidden" name="parent.id">
        	<fieldset style="border:solid 1px #aaa;padding:3px;">
        		<legend >基本信息</legend>
        		<table>
        			<tr>
        				<td style="width:70px;">上级机构：</td>
        				<td style="width:150px;"><span id="parentSpan"></span></td>
        				<td style="width:70px;">顺序：</td>
                		<td style="width:150px;">    
                    		<input id=sort name="sort" class="mini-spinner" minValue="0"  required="true"/>
                		</td>
        			</tr>
	                <tr>
	                    <td>机构名称：</td>
	                    <td style="width:150px;">    
	                        <input id="name" name="name" class="mini-textbox" required="true"/>
	                    </td>
	                    <td style="width:70px;">机构编码：</td>
	                    <td style="width:150px;">    
	                        <input id="code" name="code" class="mini-textbox" required="true" />
	                    </td>
	                </tr>
	                <tr>
	                	<td>联系电话：</td>
	                    <td>    
	                        <input id="phone" name="phone" class="mini-textbox"/>
	                    </td>
	                    <td>Email：</td>
	                    <td>    
	                        <input id="email" name="email" class="mini-textbox" />
	                    </td>
	                </tr>
	                <tr>
	                	<td>传真：</td>
	                    <td>    
	                        <input id="fax" name="fax" class="mini-textbox" />
	                    </td>
	                    <td>邮政编码：</td>
	                    <td>    
	                        <input id="zipCodee" name="zipCode" class="mini-textbox" />
	                    </td>
	                </tr>
	                <tr>
	                	<td>地址：</td>
	                    <td>    
	                        <input id="address" name="address" class="mini-textbox" />
	                    </td>
	                    <td>负责人：</td>
	                    <td>    
	                        <input id="master" name="master" class="mini-textbox" />
	                    </td>
	                </tr>          
            	</table>	
        	</fieldset>
        	
        	
        	<fieldset style="border:solid 1px #aaa;padding:3px;">
            	<legend >类型信息</legend>    
        		<table>
	            	<tr>
	                	<td style="width:70px;">类型</td>
	                	<td style="width:150px;">    
	                    	<input id="type" name="type" class="mini-combobox" data="[{ 'id': '1', 'text': '虚拟节点' },{ 'id': '2', 'text': '卫计委' },{ 'id': '3', 'text': '妇幼中心(所)' },{ 'id': '4', 'text': '医院' },{ 'id': '5', 'text': '社区' },{ 'id': '6', 'text': '部门' }]" required="true"/>
	                	</td>
	                	<td style="width:70px;">归属区域：</td>
	                	<td>                        
	                    	<input id="areaCode" name="area.code" required="true" class="mini-treeselect" data="[
									{id: '310000000000', text: '上海市',expanded:true,
									children:
									[{id: '310101000000', text: '黄浦区', pid: '310000000000'},
									{id: '310104000000', text: '徐汇区', pid: '310000000000'},
									{id: '310105000000', text: '长宁区', pid: '310000000000'},
									{id: '310106000000', text: '静安区', pid: '310000000000'},
									{id: '310107000000', text: '普陀区', pid: '310000000000'},
									{id: '310108000000', text: '闸北区', pid: '310000000000'},
									{id: '310109000000', text: '虹口区', pid: '310000000000'},
									{id: '310110000000', text: '杨浦区', pid: '310000000000'},
									{id: '310112000000', text: '闵行区', pid: '310000000000'},
									{id: '310113000000', text: '宝山区', pid: '310000000000'},
									{id: '310114000000', text: '嘉定区', pid: '310000000000'},
									{id: '310115000000', text: '浦东新区', pid: '310000000000'},
									{id: '310116000000', text: '金山区', pid: '310000000000'},
									{id: '310117000000', text: '松江区', pid: '310000000000'},
									{id: '310118000000', text: '青浦区', pid: '310000000000'},
									{id: '310120000000', text: '奉贤区', pid: '310000000000'},
									{id: '310230000000', text: '崇明县', pid: '310000000000'}]
									}
								]"
							/>
	                	</td>
	                
	            	</tr>
	            	<tr>
	                	<td style="width:70px;">分级</td>
	                	<td style="width:150px;">    
	                    	<input id="grade" name="grade" class="mini-combobox"  data="[{ 'id': '0', 'text': '无级别' },{ 'id': '1', 'text': '一级' },{ 'id': '2', 'text': '二级' },{ 'id': '3', 'text': '三级' }]" />
	                	</td>
	                	<td style="width:70px;">分等：</td>
	                	<td>                        
	                    	<input id="lv" name="lv" class="mini-combobox"  data="[{ 'id': '1', 'text': '甲等' },{ 'id': '2', 'text': '乙等' },{ 'id': '3', 'text': '丙等' }]" />
	                	</td>
	                
	            	</tr>
	            	<tr>
	                	<td >产筛中心：</td>
	                	<td>		  
	                    	<select id="cszx" name="cszx" class="mini-radiobuttonlist" required="true">
	                        	<option value="0">是</option>
	                        	<option value="1">否</option>
	                    	</select>
	                	</td>
	                	<td >诊断中心：</td>
	                	<td>    
	                    	<select id="zdzx" name="zdzx" class="mini-radiobuttonlist" required="true">
	                        	<option value="0">是</option>
	                        	<option value="1">否</option>
	                    	</select>
	                	</td>
	            	</tr>
	            	<tr>
	                	<td >助产机构：</td>
	                	<td>		  
	                    	<select id="zcjg" name="zcjg" class="mini-radiobuttonlist" required="true">
	                        	<option value="0">是</option>
	                        	<option value="1">否</option>
	                    	</select>
	                	</td>
	                	<td ></td>
	                	<td>    
	                    	
	                	</td>
	            	</tr>
            	             
        		</table>            
            
        	</fieldset>
        	          
        </div>
        <div style="text-align:center;padding:10px;">               
            <a class="mini-button" onclick="saveOffice" style="width:60px;margin-right:20px;">确定</a>       
            <a class="mini-button" onclick="onCancel" style="width:60px;">取消</a>       
        </div>  
       
    
	</div>
	
	<script type="text/javascript">
		mini.parse();
		var win = mini.get("ofWin");
		var form = new mini.Form("#ofForm");
		var grid = mini.get("ofGrid");
		var node;//当前节点
		var init = false;
		
		
    	function onBeforeTreeLoad(e) {
        	var tree = e.sender;    //树控件
        	var params = e.params;  //参数对象
        	if(e.node) {
        		//可以传递自定义的属性
            	params.id = e.node.id; //后台：request对象获取"id"
        	}
        	
    	}
    	
    	function initLoad() {
    		if(!init) {
    			grid = mini.get("ofGrid");
    			var root = grid.getRootNode();
    			grid.loadNode(grid.getChildNodes(root)[0]);
    			init = true;
    		}
    	}
    	
    	/*function addTop() {
    		form.clear();
    		$('#parentSpan').text('无');
    		mini.get('cszx').setValue('1');
    		mini.get('zdzx').setValue('1');
    		win.showAtPos('center', 'middle');
    	}*/
    	
    	function onCancel() {
    		win.hide();
    	}
    	
    	function addChild(pid,pName,nodeId) {
    		form.clear();
    		mini.get('pid').setValue(pid);
    		mini.get('cszx').setValue('1');
    		mini.get('zdzx').setValue('1');
    		mini.get('zcjg').setValue('1');
    		
    		$('#parentSpan').text(pName);
    		win.showAtPos('center', 'middle');
    		node = grid.getNode(nodeId);
    		
    	}
    	
    	function modify(id,nodeId) {
    		form.clear();
    		$.ajax({
                url: "${ctx}/sys/office/load/"+id,
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
    	                    url: "${ctx}/sys/office/remove/"+id,
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
    	
    	function saveOffice() {
            form.validate();
			if (form.isValid()) { 
				var data = form.getData(false,false); 
	            var json = mini.encode(data);   //序列化成JSON
	            $.ajax({
	                url: "${ctx}/sys/office/save/",
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
		                	
	                	} else {
	                		mini.alert("保存 失败，组织机构代码重复");
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

			returnStr += '<a href="javascript:modify(\''+record.id+'\',\''+e.node.id+'\')">修改</a>&nbsp;';
			returnStr += '<a href="javascript:remove(\''+record.id+'\',\''+e.node.id+'\')">删除</a>&nbsp;';
			if(record.type == '1' ) {
				returnStr += '<a href="javascript:addChild(\''+record.id+'\',\''+record.name+'\',\''+e.node.id+'\')">添加下级</a>&nbsp;';
			}

    		<%--<c:choose>
				<c:when test="${fns:getUser().admin}"><!-- 判断是否超级管理员-->
					returnStr += '<a href="javascript:modify(\''+record.id+'\',\''+e.node.id+'\')">修改</a>&nbsp;';
					returnStr += '<a href="javascript:remove(\''+record.id+'\',\''+e.node.id+'\')">删除</a>&nbsp;';
					if(record.type != '6') {
						returnStr += '<a href="javascript:addChild(\''+record.id+'\',\''+record.name+'\',\''+e.node.id+'\')">添加下级</a>&nbsp;';
					}
				</c:when>
    			<c:otherwise>
					var userOrgCode = '${fns:getUser().companyCode}';
					if(userOrgCode == record.code) {
						returnStr += '<a href="javascript:modify(\''+record.id+'\',\''+e.node.id+'\')">修改</a>&nbsp;';
						if(record.type != '6') {
							returnStr += '<a href="javascript:addChild(\''+record.id+'\',\''+record.name+'\',\''+e.node.id+'\')">添加下级</a>&nbsp;';
						}
					} else {
						if(record.type =='1' || record.type=='6') {
							var n = grid.getNode(e.node.id);
							var pn = grid.getParentNode(n);
							while(pn) {
								if(pn.type !='1' && pn.type!='6') {
									if(pn.code == userOrgCode) {
										returnStr += '<a href="javascript:modify(\''+record.id+'\',\''+e.node.id+'\')">修改</a>&nbsp;';
										if(record.type != '6') {
											returnStr += '<a href="javascript:addChild(\''+record.id+'\',\''+record.name+'\',\''+e.node.id+'\')">添加下级</a>&nbsp;';
										}
									}
									break;
								}
								pn = grid.getParentNode(pn);
							}
						}

					}
    			</c:otherwise>
    		</c:choose>--%>
    		
    		
    		return returnStr;
    	}
    	
    	

    


    </script>
</body>
</html>