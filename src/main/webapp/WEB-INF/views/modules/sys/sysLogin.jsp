<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>${fns:getConfig('productName')} 登录</title> 
	<meta name="decorator" content="blank"/>
    <script src="${ctxStatic}/js/jquery-1.7.2.min.js" type="text/javascript" ></script>
    <script src="${ctxStatic}/js/jquery.placehold.min.js" type="text/javascript" ></script>
   
    <script type="text/javascript" src="${ctxStatic}/miniui/miniui.js" ></script>
	<script type="text/javascript" src="${ctxStatic}/miniui/locale/zh_CN.js"></script>
    <link rel="stylesheet" href="${ctxStatic}/css/communal.css" type="text/css" />
    <link rel="stylesheet" href="${ctxStatic}/css/login.css"  type="text/css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/miniui/themes/default/miniui.css"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/miniui/themes/icons.css"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/miniui/themes/bootstrap/skin.css"/>
    <!--[if ie 6]>
    <script src="js/iepng.js" type="text/javascript"></script>
    <![endif]-->
    <script type="text/javascript">
    
 // 如果在框架或在对话框中，则弹出提示并跳转到首页
	if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.jbox').length > 0){
		alert('未登录或登录超时。请重新登录，谢谢！');
		top.location = "${ctx}";
	}
    
    function checkLogin(){
		name = $("#LoginName").val();
		pwd = $("#LoginPassword").val();
		if(!name || name==''){
			 mini.showTips({
                 content: "<b>信息</b> <br/>请输入您的用户名",
                 state: 'info',
                 x: 'center',
                 y: 'center',
                 timeout: 3000
             });
			 return;
		}
		if(!pwd || pwd==''){
			 mini.showTips({
                content: "<b>信息</b> <br/>请输入您的密码",
                state: 'info',
                x: 'center',
                y: 'center',
                timeout: 3000
            });
			 return;
		}
		document.forms[0].submit();
	}
    
    function pressLogin(e){
    	e = e || event;
    	if (e.keyCode == 13) {  //判断是否单击的enter按键(回车键)
    		checkLogin();
        }
    }
    
    </script>
</head>
<body>
    <div id="Login_body" class="login_main">
        <!--背景 start-->
        <div class="bg">
            <img src="${ctxStatic}/images/login_bg_02.png">
        </div>
        <!--背景 end>
        <!--左侧图片 start-->
        <div class="login_left_img">
            <img src="${ctxStatic}/images/login_img.png">
        </div>
        <!--左侧图片 end-->
        <!--内容 start-->
        <form id="loginForm" class="form-signin" action="${ctx}/login" method="post">
        <div class="login_content"><br>
            <!--title start -->
            <h1 class="login_title_h1"><span style="font-weight:bold;"><i class="logo"></i>上海市妇幼保健信息系统</span></h1>
            <!--title end-->
            
            
            <div class="login_content_box clearfix">
                <!--登录框 start-->
                
                <div class="login_box right">
                    <h3 class="login_h3_tit">用户登录</h3>
                      	<div class="header">
							<div id="messageBox" class="alert alert-error ${empty message ? 'hide' : ''}"><button data-dismiss="alert" class="close">×</button>
								<label id="loginError" class="error">${message}</label>
							</div>
						</div>
                    <div class="login_box_content">
                        <ul class="login_box_ul">
                            <li><i class="icon1"></i><input id="LoginName"  name="username" placeholder="请输入您的用户名" type="text" /></li>
                            <li><i class="icon2"></i><input type="password" id="LoginPassword" name="password" placeholder="请填写您的密码" type="text" onkeydown="pressLogin();"/></li>
                        </ul>
                      
                        <div class="login_box_tool clearfix">
                            <a class="btns pink_btn left" onclick="checkLogin()" style="cursor:pointer;">登录</a>
                            <a class="btns gray_btn right" href="">重填</a>
                        </div>
                    </div>
                </div>
                <!--登录框 end-->
            </div>
        </div>
        </form>
        <!--内容 end-->
          <div class="login_footer">
        	2015  上海市卫计委版权所有 
    		</div>
    </div>
  
    
</body>
</html>