s
function getSer(){
	try{
		SafeEngineCtl.SEH_InitialSession(2,"","", 0,2,"", "");
	}catch (e) {
		alert("请安装数字证书！");
		return "";
	}
	if(SafeEngineCtl.ErrorCode!=0){
		alert(GetErrCode(SafeEngineCtl.ErrorCode));
		return "";
	}
	
	//生成随机数	
	strRandom = SafeEngineCtl.SEH_GenRandomBytes();
	if(SafeEngineCtl.ErrorCode!=0){
		alert(GetErrCode(SafeEngineCtl.ErrorCode));
		SafeEngineCtl.SEH_ClearSession();
		return "";
	}

	//获取证书内容
	strCert = SafeEngineCtl.SEH_GetSelfCertificate(27, "com1", "");	
	if(SafeEngineCtl.ErrorCode!=0){
		alert(GetErrCode(SafeEngineCtl.ErrorCode));
		return "";
	}
	
	//验证证书	
	SafeEngineCtl.SEH_VerifyCertificate(strCert);	
	if(SafeEngineCtl.ErrorCode!=0){
		SafeEngineCtl.SEH_ClearSession();
		return "";
	}

	//获取证书细目
	strItemVal = SafeEngineCtl.SEH_GetCertDetail(strCert, 2); 
	if(SafeEngineCtl.ErrorCode!=0){
		SafeEngineCtl.SEH_ClearSession();
		return "";
	}
	SafeEngineCtl.SEH_ClearSession();
	return strItemVal;
	
	
	function GetErrCode(errcode){
		var result = '';
		switch (errcode){
			case -2113667072 :
			    result = "装载动态库错误(-2113667072)";
				break;
			case -2113667071 :
			    result = "内存分配错误(-2113667071)";
				break;
			case -2113667070 :
			    result = "读私钥设备错误(-2113667070)";
				break;
			case -2113667069 :
			    result = "私钥密码错误(-2113667069)";
				break;
			case -2113667068 :
			    result = "读证书链设备错误(-2113667068)";
				break;
			case -2113667067 :
			    result = "证书链密码错误(-2113667067)";
				break;
			case -2113667066 :
			    result = "读证书设备错误(-2113667066)";
				break;
			case -2113667065 :
			    result = "证书密码错误(-2113667065)";
				break;
			case -2113667064 :
			    result = "私钥超时(-2113667064)";
				break;
			case -2113667063 :
			    result = "缓冲区太小(-2113667063)";
				break;
			case -2113667062 :
			    result = "初始化环境错误(-2113667062)";
				break;
			case -2113667061 :
			    result = "清除环境错误(-2113667061)";
				break;
			case -2113667060 :
			    result = "数字签名错误(-2113667060)";
				break;
			case -2113667059 :
			    result = "验证签名错误(-2113667059)";
				break;
			case -2113667058 :
			    result = "摘要错误(-2113667058)";
				break;
			case -2113667057 :
			    result = "证书格式错误(-2113667057)";
				break;
			case -2113667056 :
			    result = "数字信封错误(-2113667056)";
				break;
			case -2113667055 :
			    result = "从LDAP获取证书错误(-2113667055)";
				break;
			case -2113667054 :
			    result = "证书已过期(-2113667054)";
				break;
			case -2113667053 :
			    result = "获取证书链错误(-2113667053)";
				break;
			case -2113667052 :
			    result = "证书链格式错误(-2113667052)";
				break;
			case -2113667051 :
			    result = "验证证书链错误(-2113667051)";
				break;
			case -2113667050 :
			    result = "证书已废除(-2113667050)";
				break;
			case -2113667049 :
			    result = "CRL格式错误(-2113667049)";
				break;
			case -2113667048 :
			    result = "连接OCSP服务器错误(-2113667048)";
				break;
			case -2113667047 :
			    result = "OCSP请求编码错误(-2113667047)";
				break;
			case -2113667046 :
			    result = "OCSP回包错误(-2113667046)";
				break;
			case -2113667045 :
			    result = "OCSP回包格式错误(-2113667045)";
				break;
			case -2113667044 :
			    result = "OCSP回包过期(-2113667044)";
				break;
			case -2113667043 :
			    result = "OCSP回包验证签名错误(-2113667043)";
				break;
			case -2113667042 :
			    result = "证书状态未知(-2113667042)";
				break;
			case -2113667041 :
			    result = "对称加解密错误(-2113667041)";
				break;
			case -2113667040 :
			    result = "获取证书信息错误(-2113667040)";
				break;
			case -2113667039 :
			    result = "获取证书细目错误(-2113667039)";
				break;
			case -2113667038 :
			    result = "获取证书唯一标识错误(-2113667038)";
				break;
			case -2113667037 :
			    result = "获取证书扩展项错误(-2113667037)";
				break;
			case -2113667036 :
			    result = "PEM编码错误(-2113667036)";
				break;
			case -2113667035 :
			    result = "PEM解码错误(-2113667035)";
				break;
			case -2113667034 :
			    result = "产生随机数错误(-2113667034)";
				break;
			case -2113667033 :
			    result = "PKCS12参数错误(-2113667033)";
				break;m
			case -2113667032 :
			    result = "私钥格式错误(-2113667032)";
				break;
			case -2113667031 :
			    result = "公私钥不匹配(-2113667031)";
				break;
			case -2113667030 :
			    result = "PKCS12编码错误(-2113667030)";
				break;
			case -2113667029 :
			    result = "PKCS12格式错误(-2113667029)";
				break;
			case -2113667028 :
			    result = "PKCS12解码错误(-2113667028)";
				break;
			case -2113667027 :
			    result = "非对称加解密错误(-2113667027)";
				break;
			case -2113667026 :
			    result = "OID格式错误(-2113667026)";
				break;
			case -2113667025 :
			    result = "LDAP地址格式错误(-2113667025)";
				break;
			case -2113667024 :
			    result = "LDAP地址错误(-2113667024)";
				break;
			case -2113667023 :
			    result = "连接LDAP服务器错误(-2113667023)";
				break;m
			case -2113667022 :
			    result = "LDAP绑定错误(-2113667022)";
				break;
			case -2113667021 :
			    result = "没有OID对应的扩展项(-2113667021)";
				break;
			case -2113667020 :
			    result = "获取证书级别错误(-2113667020)";
				break;
			case -2113667019 :
			    result = "读取配置文件错误(-2113667019)";
				break;
			case -2113667018 :
			    result = "私钥未载入(-2113667018)";
				break;
		// 以下错误用于登录
			case -2113666824 :
			    result = "无效的登录凭证(-2113666824)";
				break;
			case -2113666823 :
			    result = "参数错误(-2113666823)";
				break;
			case -2113666822 :
			    result = "不是服务器证书(-2113666822)";
				break;
			case -2113666821 :
			    result = "登录错误(-2113666821)";
				break;
			case -2113666820 :
			    result = "证书验证方式错误(-2113666820)";
				break;
			case -2113666819 :
			    result = "随机数验证错误(-2113666819)";
				break;
			case -2113666818 :
			    result = "与单点登录客户端代理通信(-2113666818)";
			break;
			case -2113666992 :
			    result = "不能获取内容，请确认U盘已经插入接口(-2113666818)";
			break;
		}
	return result;
	}
}

function _doTest(strpassword)
{
	strpripath="com1";
	strcertpath="com1";
	strcertchainpath="com1";
	//初始化函数
	try{
		SafeEngineCtl.SEH_InitialSession(10,"com1",strpassword, 0,10,"com1", "");
	}catch (e) {
		alert("请安装数字证书！");
		return false;
	}
	if(SafeEngineCtl.ErrorCode!=0){
		alert(GetErrCode(SafeEngineCtl.ErrorCode));
		return false;
	}
	
	//生成随机数	
	strRandom = SafeEngineCtl.SEH_GenRandomBytes();
	if(SafeEngineCtl.ErrorCode!=0){
		alert(GetErrCode(SafeEngineCtl.ErrorCode));
		SafeEngineCtl.SEH_ClearSession();
		return false;
	}

	//获取证书内容
	strCert = SafeEngineCtl.SEH_GetSelfCertificate(27, "com1", "");	
	if(SafeEngineCtl.ErrorCode!=0){
		alert(GetErrCode(SafeEngineCtl.ErrorCode));
		return false;
	}
	
	//验证证书	
	SafeEngineCtl.SEH_VerifyCertificate(strCert);	
	if(SafeEngineCtl.ErrorCode!=0){
		SafeEngineCtl.SEH_ClearSession();
		return false;
	}

	//获取证书细目
	strItemVal = SafeEngineCtl.SEH_GetCertDetail(strCert, 2); 
	if(SafeEngineCtl.ErrorCode!=0){
		SafeEngineCtl.SEH_ClearSession();
		return false;
	}
	
	SafeEngineCtl.SEH_ClearSession();
	return true;
	
	
	function GetErrCode(errcode){
		var result = '';
		switch (errcode){
			case -2113667072 :
			    result = "装载动态库错误(-2113667072)";
				break;
			case -2113667071 :
			    result = "内存分配错误(-2113667071)";
				break;
			case -2113667070 :
			    result = "读私钥设备错误(-2113667070)";
				break;
			case -2113667069 :
			    result = "私钥密码错误(-2113667069)";
				break;
			case -2113667068 :
			    result = "读证书链设备错误(-2113667068)";
				break;
			case -2113667067 :
			    result = "证书链密码错误(-2113667067)";
				break;
			case -2113667066 :
			    result = "读证书设备错误(-2113667066)";
				break;
			case -2113667065 :
			    result = "证书密码错误(-2113667065)";
				break;
			case -2113667064 :
			    result = "私钥超时(-2113667064)";
				break;
			case -2113667063 :
			    result = "缓冲区太小(-2113667063)";
				break;
			case -2113667062 :
			    result = "初始化环境错误(-2113667062)";
				break;
			case -2113667061 :
			    result = "清除环境错误(-2113667061)";
				break;
			case -2113667060 :
			    result = "数字签名错误(-2113667060)";
				break;
			case -2113667059 :
			    result = "验证签名错误(-2113667059)";
				break;
			case -2113667058 :
			    result = "摘要错误(-2113667058)";
				break;
			case -2113667057 :
			    result = "证书格式错误(-2113667057)";
				break;
			case -2113667056 :
			    result = "数字信封错误(-2113667056)";
				break;
			case -2113667055 :
			    result = "从LDAP获取证书错误(-2113667055)";
				break;
			case -2113667054 :
			    result = "证书已过期(-2113667054)";
				break;
			case -2113667053 :
			    result = "获取证书链错误(-2113667053)";
				break;
			case -2113667052 :
			    result = "证书链格式错误(-2113667052)";
				break;
			case -2113667051 :
			    result = "验证证书链错误(-2113667051)";
				break;
			case -2113667050 :
			    result = "证书已废除(-2113667050)";
				break;
			case -2113667049 :
			    result = "CRL格式错误(-2113667049)";
				break;
			case -2113667048 :
			    result = "连接OCSP服务器错误(-2113667048)";
				break;
			case -2113667047 :
			    result = "OCSP请求编码错误(-2113667047)";
				break;
			case -2113667046 :
			    result = "OCSP回包错误(-2113667046)";
				break;
			case -2113667045 :
			    result = "OCSP回包格式错误(-2113667045)";
				break;
			case -2113667044 :
			    result = "OCSP回包过期(-2113667044)";
				break;
			case -2113667043 :
			    result = "OCSP回包验证签名错误(-2113667043)";
				break;
			case -2113667042 :
			    result = "证书状态未知(-2113667042)";
				break;
			case -2113667041 :
			    result = "对称加解密错误(-2113667041)";
				break;
			case -2113667040 :
			    result = "获取证书信息错误(-2113667040)";
				break;
			case -2113667039 :
			    result = "获取证书细目错误(-2113667039)";
				break;
			case -2113667038 :
			    result = "获取证书唯一标识错误(-2113667038)";
				break;
			case -2113667037 :
			    result = "获取证书扩展项错误(-2113667037)";
				break;
			case -2113667036 :
			    result = "PEM编码错误(-2113667036)";
				break;
			case -2113667035 :
			    result = "PEM解码错误(-2113667035)";
				break;
			case -2113667034 :
			    result = "产生随机数错误(-2113667034)";
				break;
			case -2113667033 :
			    result = "PKCS12参数错误(-2113667033)";
				break;m
			case -2113667032 :
			    result = "私钥格式错误(-2113667032)";
				break;
			case -2113667031 :
			    result = "公私钥不匹配(-2113667031)";
				break;
			case -2113667030 :
			    result = "PKCS12编码错误(-2113667030)";
				break;
			case -2113667029 :
			    result = "PKCS12格式错误(-2113667029)";
				break;
			case -2113667028 :
			    result = "PKCS12解码错误(-2113667028)";
				break;
			case -2113667027 :
			    result = "非对称加解密错误(-2113667027)";
				break;
			case -2113667026 :
			    result = "OID格式错误(-2113667026)";
				break;
			case -2113667025 :
			    result = "LDAP地址格式错误(-2113667025)";
				break;
			case -2113667024 :
			    result = "LDAP地址错误(-2113667024)";
				break;
			case -2113667023 :
			    result = "连接LDAP服务器错误(-2113667023)";
				break;m
			case -2113667022 :
			    result = "LDAP绑定错误(-2113667022)";
				break;
			case -2113667021 :
			    result = "没有OID对应的扩展项(-2113667021)";
				break;
			case -2113667020 :
			    result = "获取证书级别错误(-2113667020)";
				break;
			case -2113667019 :
			    result = "读取配置文件错误(-2113667019)";
				break;
			case -2113667018 :
			    result = "私钥未载入(-2113667018)";
				break;
		// 以下错误用于登录
			case -2113666824 :
			    result = "无效的登录凭证(-2113666824)";
				break;
			case -2113666823 :
			    result = "参数错误(-2113666823)";
				break;
			case -2113666822 :
			    result = "不是服务器证书(-2113666822)";
				break;
			case -2113666821 :
			    result = "登录错误(-2113666821)";
				break;
			case -2113666820 :
			    result = "证书验证方式错误(-2113666820)";
				break;
			case -2113666819 :
			    result = "随机数验证错误(-2113666819)";
				break;
			case -2113666818 :
			    result = "与单点登录客户端代理通信(-2113666818)";
			break;
			case -2113666992 :
			    result = "不能获取内容，请确认U盘已经插入接口(-2113666818)";
			break;
		}
	return result;
}

}