<%@page import="com.thinkgem.jeesite.common.config.Global"%>
<%@ page import="com.thinkgem.jeesite.modules.sys.entity.User" %>
<%@ page import="com.thinkgem.jeesite.modules.sys.utils.UserUtils" %>
<%@ page import="com.thinkgem.jeesite.modules.sys.entity.Role" %>
<%@ page import="java.util.List" %>
<%@ page import="com.thinkgem.jeesite.common.utils.StringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')}</title>
	<meta name="decorator" content="blank"/>
	<c:set var="tabmode" value="${empty cookie.tabmode.value ? '0' : cookie.tabmode.value}"/>
    <c:if test="${tabmode eq '1'}"><link rel="Stylesheet" href="${ctxStatic}/jerichotab/css/jquery.jerichotab.css" />
    <script type="text/javascript" src="${ctxStatic}/jerichotab/js/jquery.jerichotab.js"></script></c:if>
	<link rel="stylesheet" href="${ctxStatic}/css/new-index.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/miniui/themes/default/miniui.css"/>
	<script type="text/javascript" src="${ctxStatic}/miniui/miniui.js" ></script>
	<%--<%
		User user = UserUtils.getUser();
		List<Role> roleList = user.getRoleList();
		boolean showSetup = false;
		if(roleList != null && roleList.size()>0){
			for (Role role : roleList){
				//如果是机构管理员和超级管理员角色，那么需要显示主界面中设置菜单按钮
				if(role != null && StringUtils.isNotBlank(role.getEnname()) && (role.getEnname().endsWith("_MANAGE") || role.getEnname().equals("dept") )){
					showSetup = true;
				}
			}
		}
		request.setAttribute("showSetup",showSetup);
	%>--%>
	<script type="text/javascript">
		$(document).ready(function() {
			// <c:if test="${tabmode eq '1'}"> 初始化页签
			$.fn.initJerichoTab({
                renderTo: '#right', uniqueId: 'jerichotab',
                contentCss: { 'height': $('#right').height() - tabTitleHeight },
                tabs: [], loadOnce: false, tabWidth: 110, titleHeight: tabTitleHeight
            });//</c:if> 
			// 绑定菜单单击事件
			$("#menu a.menu").click(function(){
				// 一级菜单焦点
				$("#menu li.menu").removeClass("active");
				$(this).parent().addClass("active");
				// 左侧区域隐藏
				if ($(this).attr("target") == "mainFrame"){
					$("#left,#openClose").hide();
					wSizeWidth();
					// <c:if test="${tabmode eq '1'}"> 隐藏页签
					$(".jericho_tab").hide();
					$("#mainFrame").show();//</c:if>
					return true;
				}
				
				// 左侧区域显示
				$("#left,#openClose").show();
				if(!$("#openClose").hasClass("close")){
					$("#openClose").click();
				}
				// 显示二级菜单
				var menuId = "#menu-" + $(this).attr("data-id");
				if ($(menuId).length > 0){
					$("#left .accordion").hide();
					$(menuId).show();
					// 初始化点击第一个二级菜单
					if (!$(menuId + " .accordion-body:first").hasClass('in')){
						$(menuId + " .accordion-heading:first a").click();
					}
					if (!$(menuId + " .accordion-body li:first ul:first").is(":visible")){
						$(menuId + " .accordion-body a:first i").click();
					}
					// 初始化点击第一个三级菜单
					$(menuId + " .accordion-body li:first li:first a:first i").click();
				}else{
					// 获取二级菜单数据
					$.get($(this).attr("data-href"), function(data){
						if (data.indexOf("id=\"loginForm\"") != -1){
							alert('未登录或登录超时。请重新登录，谢谢！');
							top.location = "${ctx}";
							return false;
						}
						$("#left .accordion").hide();
						$("#left").append(data);
						// 链接去掉虚框
						$(menuId + " a").bind("focus",function() {
							if(this.blur) {this.blur()};
						});
						// 二级标题
						$(menuId + " .accordion-heading a").click(function(){
							$(menuId + " .accordion-toggle i").removeClass('icon-chevron-down').addClass('icon-chevron-right');
							if(!$($(this).attr('data-href')).hasClass('in')){
								$(this).children("i").removeClass('icon-chevron-right').addClass('icon-chevron-down');
							}
						});
						// 二级内容
						$(menuId + " .accordion-body a").click(function(){
							$(menuId + " li").removeClass("active");
							$(menuId + " li i").removeClass("icon-white");
							$(this).parent().addClass("active");
							$(this).children("i").addClass("icon-white");
						});
						// 展现三级
						$(menuId + " .accordion-inner a").click(function(){
							var href = $(this).attr("data-href");
							if($(href).length > 0){
								$(href).toggle().parent().toggle();
								return false;
							}
							// <c:if test="${tabmode eq '1'}"> 打开显示页签
							return addTab($(this)); // </c:if>
						});
						// 默认选中第一个菜单
						//$(menuId + " .accordion-body a:first i").click();
						$(menuId + " .accordion-body li:first li:first a:first i").click();
					});
				}
				// 大小宽度调整
				wSizeWidth();
				return false;
			});
			// 初始化点击第一个一级菜单
			//$("#menu a.menu:first span").click();
			// <c:if test="${tabmode eq '1'}"> 下拉菜单以选项卡方式打开
			$("#userInfo .dropdown-menu a").mouseup(function(){
				return addTab($(this), true);
			});// </c:if>
			// 鼠标移动到边界自动弹出左侧菜单
			$("#openClose").mouseover(function(){
				if($(this).hasClass("open")){
					$(this).click();
				}
			});
			// 获取通知数目  <c:set var="oaNotifyRemindInterval" value="${fns:getConfig('oa.notify.remind.interval')}"/>
			 function getNotifyNum(){
				$.get("${ctx}/oa/oaNotify/self/count?updateSession=0&t="+new Date().getTime(),function(data){
					var num = parseFloat(data);
					if (num > 0){
						$("#notifyNum,#notifyNum2").show().html("("+num+")");
					}else{
						$("#notifyNum,#notifyNum2").hide()
					}
				});
			}
			/* getNotifyNum(); //<c:if test="${oaNotifyRemindInterval ne '' && oaNotifyRemindInterval ne '0'}">
			setInterval(getNotifyNum, ${oaNotifyRemindInterval}); //</c:if> */
		});
		// <c:if test="${tabmode eq '1'}"> 添加一个页签
		function addTab($this, refresh){
			$(".jericho_tab").show();
			$("#mainFrame").hide();
			$.fn.jerichoTab.addTab({
                tabFirer: $this,
                title: $this.text(),
                closeable: true,
                data: {
                    dataType: 'iframe',
                    dataLink: $this.attr('href')
                }
            }).loadData(refresh);
			return false;
		}// </c:if>
	</script>
	<style>
		.newVersion{width: 7px; height: 7px; background: #FFFFFF; position: relative; left: 20px; top: -3px; border-radius: 10px; display: none;}
	</style>
</head>
<body>
    <div class="div-bg"><img src="${ctxStatic}/images/body-bg.png" /></div>
	<div id="main">
		<div id="header" class="navbar navbar-fixed-top">
			<div class="navbar-inner">
				<div class="brand"><span id="productName">${fns:getConfig('productName')}</span></div>
				<ul id="userControl" class="nav pull-right">
<!-- 					<li> -->
<!-- 						<input type="button" title="查看孕妇所有信息" onclose="showViewYF()"> -->
<%-- 						<a href="${ctx}" title="查看孕妇所有信息"> --%>
<!-- 						</a> -->
<!-- 					</li> -->
					<li><a href="${ctx}" title="主页"><i class="icon-home"></i></a></li>



					<%--<c:if test="${showSetup}">
						<li><a href="javascript:" title="系统设置" onclick="opens('2')"><i class="icon-cog"></i></a></li>
					</c:if>--%>

					<shiro:hasPermission name="sys:btn:set">
						<li><a href="javascript:" title="系统设置" onclick="opens('2')"><i class="icon-cog"></i></a></li>
					</shiro:hasPermission>



					<%--<li><a href="javascript:" title="邮件" onclick="openMessageWindow()"><i class="icon-envelope"></i></a></li>--%>
					<li id="tip" style="display:none"><a href="${ctx}/yunbao/chanfu/main" title="今日提醒" target="mainFrame" onclick="opens('3')"><i class="icon-bell"></i></a></li>
					<li id="userInfo" class="dropdown">
						<a id="userMsg" class="dropdown-toggle" data-toggle="dropdown" href="#" title="个人信息">${fns:getUser().name}&nbsp;<span id="notifyNum" class="label label-info hide"></span></a>
						<ul class="dropdown-menu">
							<li><a href="${ctx}/sys/user/info" <%--onclick=updatePwd('${ctx}/sys/user/info')--%> target="mainFrame">&nbsp; 个人信息</a></li>
							<li><a href="${ctx}/sys/user/modifyPwd" <%--onclick="updatePwd('${ctx}/sys/user/modifyPwd')"--%> id="up_pwd" target="mainFrame"><i class="icon-lock"></i>&nbsp;  修改密码</a></li>
							<%--<li><a href="${ctx}/oa/oaNotify/self" target="mainFrame"><i class="icon-bell"></i>&nbsp;  我的通知 <span id="notifyNum2" class="label label-info hide"></span></a></li>--%>
						</ul>
					</li>
					
					<li id="version" style="display:none">
						<a href="javascript:showVersion();" title="版本更新">
							版本
							<span id="remand">
								<img style="top:5px;right: 0px;" src="${ctxStatic}/images/new.gif">
							</span>
						</a>
						<div class="newVersion"></div>
					</li>
<%-- 					<li><a href="${ctx}/yunbao/chanfu/showHelp" title="帮助?">帮助</a></li>
 --%>				
					<li><a href="${ctx}/yunbao/chanfu/showHelp1" target="_Blank" title="帮助?">帮助</a></li>
					<li>
						<%-- <c:if test="${fns:getConfig('casFlag') eq 'true'}">
						 <a href="${fns:getConfig('cas.server.url')}/logout" title="退出登录">退出</a>
						 </c:if>
						 <c:if test="${fns:getConfig('casFlag') eq 'false'}">
						 <a href="${ctx}/logout" title="退出登录">退出</a>
						 </c:if> --%>
						 <a href="${ctx}/logout" title="退出登录">退出</a>
					</li>	
					<li>&nbsp;</li>
				</ul>
				<div class="nav-collapse" style="display: none;">
					<ul id="menu" class="nav" style="*white-space:nowrap;float:none;">
						<c:set var="firstMenu" value="true"/>
						<c:forEach items="${fns:getMenuList()}" var="menu" varStatus="idxStatus">
							<c:if test="${menu.parent.id eq '1'&&menu.type eq '1'}">
								<li class="menu ${not empty firstMenu && firstMenu ? ' active' : ''}">
									<c:if test="${empty menu.href}">
										<a id="${menu.id}" class="menu" href="javascript:" data-href="${ctx}/sys/menu/tree?parentId=${menu.id}" data-id="${menu.id}"><span>${menu.name}</span></a>
									</c:if>
									<c:if test="${not empty menu.href}">
										<c:if test="${empty menu.target}">
											<a class="menu" href="${fn:indexOf(menu.href, '://') eq -1 ? ctx : ''}${menu.href}" data-id="${menu.id}" target="mainFrame"><span>${menu.name}</span></a>
										</c:if>
										<c:if test="${not empty menu.target}">
											<a  href="${fn:indexOf(menu.href, '://') eq -1 ? ctx : ''}${menu.href}"  target="${menu.target}"><span>${menu.name}</span></a>
										</c:if>
									</c:if>
								</li>
								<c:if test="${firstMenu}">
									<c:set var="firstMenuId" value="${menu.id}"/>
								</c:if>
								<c:set var="firstMenu" value="false"/>
							</c:if>
						</c:forEach>
					</ul>
				</div><!--/.nav-collapse -->
			</div>
	    </div>
	    <div class="container-fluid">
	    	<div id="navs"> 
		    	<div class="header-title"><i class="logo"></i>上海市妇幼保健信息系统</div> 
			    <table class="table-nav">
				<tr>
					<td class="tds"><div class="sys-nav"><img src="${ctxStatic}/images/1.png" /></div></td>
					<td class="tds"><div class="sys-nav"><img src="${ctxStatic}/images/2.png" /></div></td>
					<td class="tds" onclick="opens('b7c435c3fdfc47b7982e2662e9221c91')"><div class="sys-nav"><img src="${ctxStatic}/images/3.png" /></div></td>
					<td class="tds"><div class="sys-nav"><img src="${ctxStatic}/images/4.png" /></div></td>
					<td class="tds"><div class="sys-nav"><img src="${ctxStatic}/images/5.png" /></div></td>
				</tr>
				<tr>
					<td class="tds"><div class="sys-nav"><img src="${ctxStatic}/images/6.png" /></div></td>
					<td class="tds" onclick="opensWidow()"><div class="sys-nav"><img src="${ctxStatic}/images/7.png" /></div></td>
					<td class="tds"><div class="sys-nav"><img src="${ctxStatic}/images/8.png" /></div></td>
					<td class="tds"><div class="sys-nav"><img src="${ctxStatic}/images/9.png" /></div></td>
					<td class="tds"><div class="sys-nav"><img src="${ctxStatic}/images/10.png" /></div></td>
				</tr>
			</table>
				<%-- <c:if test="${sessionScope.eb != null && sessionScope.eb == '0'}"> --%>
				<div class="other-link-btn"><a  id="eb" href="javascript:void(0)" onclick="opener()" target="_Blank">儿保系统</a></div>
				<%-- </c:if> --%>
	    	</div>
			<div id="content" class="row-fluid" style="display: none; background: #fff;">
				<div id="left"><%-- 
					<iframe id="menuFrame" name="menuFrame" src="" style="overflow:visible;" scrolling="yes" frameborder="no" width="100%" height="650"></iframe> --%>
				</div>
				<div id="openClose" class="close">&nbsp;</div>
				<div id="right">
					<iframe id="mainFrame" name="mainFrame" src="" style="overflow:visible;" scrolling="yes" frameborder="no" width="100%" height="650"></iframe>
				</div>
			</div>
		</div>
	</div>
	
	<form id="loginForm" class="form-signin" target="_blank" action="<%=Global.getConfig("cszmurl")%>/cszm/main/weblogin" method="post">
      <input id="LoginName"  name="username" value="${sessionScope.fylg}" type="hidden" />
      <input type="password" id="LoginPassword" value="${sessionScope.fymm}" name="password" type="hidden"/>
     </form>
	
	<!-- 信息发送窗口 -->
	<div id="message-window" class="mini-window" title="网内信息" url="${ctx}/yunbao/emails/main" showMaxButton="true" showShadow="true" showModal="true" allowResize="true" allowDrag="true"></div>
	
	<!-- 浮动按钮 -->
	<div id="wFloatBtn" style="display:none; border:2px solid #FFFFFF; background:#FA5461; width: 50px; height: 50px; position:fixed; right: 10px; top: 55px; z-index:9999; border-radius: 50px; color: #FFFFFF; text-align: center; line-height: 50px;cursor: pointer;">
		详情
	</div>
	
	<script type="text/javascript">
		var leftWidth = 160; // 左侧窗口大小
		var tabTitleHeight = 33; // 页签的高度
		var htmlObj = $("html"), mainObj = $("#main");
		var headerObj = $("#header"), footerObj = $("#footer");
		var frameObj = $("#left, #openClose, #right, #right iframe");
		var xtType = "";
		function wSize(){
			var minHeight = 500, minWidth = 980;
			var strs = getWindowSize().toString().split(",");
			htmlObj.css({"overflow-x":strs[1] < minWidth ? "auto" : "hidden", "overflow-y":strs[0] < minHeight ? "auto" : "hidden"});
			mainObj.css("width",strs[1] < minWidth ? minWidth - 10 : "auto");
			frameObj.css({'height':$(window).height()-52});
			/* frameObj.height((strs[0] < minHeight ? minHeight : strs[0]) - headerObj.height() - footerObj.height() - (strs[1] < minWidth ? 42 : 28)); */
			$("#openClose").height($("#openClose").height() - 5);// <c:if test="${tabmode eq '1'}"> 
			$(".jericho_tab iframe").height($("#right").height() - tabTitleHeight); // </c:if>
			wSizeWidth();
		}
		function wSizeWidth(){
			if (!$("#openClose").is(":hidden")){
				var leftWidth = ($("#left").width() < 0 ? 0 : $("#left").width());
				$("#right").width($("#content").width()- leftWidth - $("#openClose").width() -5);
			}else{
				$("#right").width("100%");
			}
		}// <c:if test="${tabmode eq '1'}"> 
		function openCloseClickCallBack(b){
			$.fn.jerichoTab.resize();
		} // </c:if>
		
		function opens(ids){
			if(ids==3 || ids == 'b7c435c3fdfc47b7982e2662e9221c91'){
				$("#menu a.menu[id='b7c435c3fdfc47b7982e2662e9221c91'] span").click();
				$("#mainFrame").attr("src","${ctx}/yunbao/chanfu/main");
				//改变框架显示的系统名称
				$("#productName").text("上海市孕产妇保健信息管理系统");
				//获取是否具有新版本
				getHaveNewVersion("3");
				xtType = "3";
			}else{
				$("#menu a.menu[id='"+ids+"'] span").click();
                $("#userMsg").attr("data-toggle","dropdown");
			}
			$("#content").show();
			wSizeWidth(); // 大小宽度调整
			$("#navs").remove();
			
		}
		
		//历史版本
		function showVersion(){
			window.open("${ctx}/sys/system/version/history?type="+xtType, "newwindow", "height=600, width=1000, menubar=no, scrollbars=yes, toolbar=no, status=no, resizable=no");
		}
		
		function showHelp(){
			window.location.href="";
		}
		
		function openMessageWindow(){
			var sw = $(window).width() * 0.7;
			var sh = $(window).height() * 0.9;
			var messageWindow = mini.get("message-window");
			messageWindow.setStyle("width:"+sw+"px;height:"+sh+"px;min-width:951px;");
			messageWindow.show();
		}
		
		//查询孕妇详细信息
		function watchDetail(_id){
			window.open("${ctx}/yunbao/chanfu/yunChanFuMain?id="+_id, "newwindow", "height=600; width=1000; menubar=no; scrollbars=yes; titlebar=yes; resizable=yes");
		}
		
		//显示浮动按钮
		function showFloatBtn(){
			$("#wFloatBtn").css("display","block");
		}
		
		//隐藏浮动按钮
		function hideFloatBtn(){
			$("#wFloatBtn").css("display","none");
		}
        
		function opener(){
 			window.open("<%= Global.getConfig("eburl")%>uid=${user.loginName}&psw=${user.password}");
 		}
		
		//显示或隐藏今日提醒和版本提示
		function toggleTipAndVersion(val){
			switch(val){
				case "show":
                    //进入提醒
					$("#tip").show();
                    //版本信息
					$("#version").show();
                    //用户是否具有下拉
                    $("#userMsg").attr("data-toggle","dropdown");
					break;
				case "hide":
					$("#tip").hide();
					$("#version").hide();
                    $("#userMsg").removeAttr("data-toggle","dropdown");
					break;
				default:
					$("#tip").hide();
					$("#version").hide();
                    $("#userMsg").removeAttr("data-toggle","dropdown");
			}
		}
		
		//获取是否具有新版本
		function getHaveNewVersion(sysType){
			$.ajax({
				url: "${ctx}/sys/system/version/isNewVersion",
				type: "get",
				data: {mType: sysType},
				dataType: "text",
				success: function(result){
					if(result == "true"){
						$("#remand").show();
						//$(".newVersion").css("display","block");
					}else{
						$("#remand").hide();
						//$(".newVersion").css("display","none");
					}
				},
				error: function(){
					
				}
			});
			
			//显示消息提醒和版本提示
			toggleTipAndVersion("show");
		}
		
		$(function(){
			//隐藏信息提醒和版本提醒
			toggleTipAndVersion("hide");

		});
		
		function opensWidow(){
			$("#loginForm").submit();
		}

		//修改密码和查看个人信息查看
        function updatePwd(src){
			opens("2");
			$("#mainFrame").attr("src",src);
        }
		
	</script>
	<script src="${ctxStatic}/common/wsize.min.js" type="text/javascript"></script>
</body>
</html>