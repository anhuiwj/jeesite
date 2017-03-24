////////////////////////////////////////
function onEnglishVal(e) {
    if (e.isValid) {
        if (isEnglish(e.value) == false) {
            e.errorText = "请输入英文";
            e.isValid = false;
        }
    }
}
function onChineseAndEnglishVal(e) {
    if (e.value != null && "" != e.value) {
        if (e.isValid) {
            if (isChineseAndEnglish(e.value) == false) {
                e.errorText = "请输入英文或汉字";
                e.isValid = false;
            }
        }
    }
}
function onEnglishAndNumberVal(e) {
    if (e.value != null && "" != e.value) {
        if (e.isValid) {
            if (isEnglishAndNumber(e.value) == false) {
                e.errorText = "请输入英文+数字";
                e.isValid = false;
            }
        }
    }
}
function onChineseVal(e) {
    if (e.isValid) {
        if (isChinese(e.value) == false) {
            e.errorText = "请输入中文";
            e.isValid = false;
        }
    }
}
function onValidateIDCard(e) {
    if (e.isValid) {
        if (isIDCard(e.value) == false) {
            e.errorText = "请输入正确身份证号码";
            e.isValid = false;
        }
    }
}
function onIDCardsVal(e) {
    if (e.isValid) {
        var pattern = /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{4}$/;
        if (e.value.length < 15 || e.value.length > 18 || pattern.test(e.value) == false) {
            e.errorText = "请输入合法的身份证";
            e.isValid = false;
        }
    }
}
function onPhoneVal(e) {
    if (e.value != null && "" != e.value) {
        var patternss = /(13\d|14[57]|15[^4,\D]|17[678]|18\d)\d{8}|170[059]\d{7}/;
        var pattern = /^([0-9]{11})?$/;
        //console.log("{} "+pattern.test(e.value));
        if (pattern.test(e.value) == false) {
            e.errorText = "请输入合法的手机号码";
            e.isValid = false;
        }
    }
}

function onTelPhoneVal(e) {
    var lxdhqh = mini.get('lxdhqh').getValue();
    var hm = lxdhqh + e.value;
    if (hm != null && hm != "") {
        var pattern = /^\d{11}$/;
        if (pattern.test(hm) == false) {
            e.errorText = "请输入合法的电话号码";
            e.isValid = false;
        }
    }
}


function ondwTelPhoneVal(e) {
    var lxdhqh = mini.get('dwdhqh').getValue();
    var hm = lxdhqh + e.value;
    if (hm != null && hm != "") {
        var pattern = /^\d{11}$/;
        if (pattern.test(hm) == false) {
            e.errorText = "请输入合法的电话号码";
            e.isValid = false;
        }
    }
}
/**
 * 校验必须移动电话 备注：<input class="mini-textbox" vtype="chinese"/>
 */
mini.VTypes["mobileErrorText"] = "手机格式不正确";
mini.VTypes["mobile"] = function (value) {
    var length = value.length;
    return $.trim(value) == "" || (length == 11 && /^(1\d{10})$/.test(value));
}

/**
 * 校验必须移动电话 备注：<input class="mini-textbox" vtype="chinese"/>
 */
mini.VTypes["phoneErrorText"] = "电话格式不正确";
mini.VTypes["phone"] = function (value) {
    //var length = value.length;
    //return $.trim(value) == "" || (/^(0[\d]{2,3}-[\d]{7,8})$/.test(value));
    var regEx = /^[0-9][0-9-]+$/;
    if ($.trim(value) == "" || regEx.test(value))return true;
    return false;
}
/**
 * 群组中至少有一个是必填校验 备注：<input class="mini-textbox" vtype="group:fid,sid;"/>
 */
mini.VTypes["groupErrorText"] = "群组内组件至少有一个必填";
mini.VTypes["group"] = function (v, param) {
    if (param == undefined || param.length == 0) {
        mini.VTypes["groupErrorText"] = "id配置参数错误";
        return false;
    }
    var rs = false;
    for (var i = 0; i < param.length; i++) {
        if (param[0] == "sjhm" && param[1] == "lxdh") {
            mini.VTypes["groupErrorText"] = "手机号和固定电话至少填写一个";
        }

        var value = "";
        var pi = mini.get(param[i]);
        if (pi == undefined || pi == null) {
            value = $("#" + param[i]).val();
        } else {
            value = pi.getValue();
        }
        //mini.VTypes["remoteErrorText"] = "id配置参数错误";
        //return false;
        if ($.trim(value) != "") {
            rs = true;
        }
    }
    if (rs) {
        for (var i = 0, len = param.length; i < len; i++) {
            mini.get(param[i]).setIsValid(true);
        }
    }
    return rs;
}

function onAddressVal(e) {
    if (e.isValid) {
        if (e.value.charAt(5) == '0') {
            e.errorText = "请选择区县或者街道/居委会";
            e.isValid = false;
        }
    }
}

function onFloatPoint2Val(e) {
    var rgExp = /^-?\d+\.?\d{0,2}$/;
    if (e.value != '' && !e.value.match(rgExp)) {
        e.errorText = "最多保留两位小数点";
        e.isValid = false;
    }
}


//身份证号码校验
function isIdCard(card) {
    var pattern = /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{4}$/;
    if (card.length < 15 || card.length > 18 || pattern.test(card) == false) {
        mini.alert("请输入合法的身份证");
        return false;
    }
    return true;
}

////////////////////////////////////////////////////////////////////
/*
 * 省市县路四级联动校验
 * lx:1本市 2非本市  tag: 1户籍地址 2居住地址 3休养地址 
 * qu：区(直辖市相当于街道) jln：路
 */
function isAdrVal(lx, tag, qx, jln) {
    if (lx == '1' || lx == '2') { //本市户籍
        if (qx == null || qx == '') {  //街道不选，必须填写路
            mini.alert("本市户籍必须选择到街道");
            return false;
        } else {
            if (tag == '1') {
                if (qx.substring(0, 3) != '310') {
                    mini.alert("本市户籍必须选择上海市地址.");
                    return false;
                } else {
                    if (qx.substring(6, 9) == '000' && jln == '') {
                        mini.alert("本市户籍 '街道'与'街路弄'必须录入一项.");
                        return false;
                    }
                }
            }
        }
    }

    if (lx == '3' || lx == '4') { //外地户籍
        if (tag == '1') {
            if (qx.substring(0, 3) == '310') {
                mini.alert("非本市户籍不能选择上海市地址.");
                return false;
            }
        }
        if (tag == '2') {
            if (qx.substring(6, 9) == '000' && jln == '') {
                mini.alert("非本市户籍居住地址的 '街道'与'街路弄'必须录入一项.");
                return false;
            }
        }
    }
    return true;
}
//详细地址中的路、弄、号、室组装(miniui元素的Value)
//XX街XX路XX弄 # XX号XX室
function getAdrCge(jln, hs) {
    jln = $.trim(jln);
    hs = $.trim(hs);

    var str = '';

    if (jln != null && jln != '') {
        str += jln + "#";
    } else {
        str += "#";
    }

    if (hs != null && hs != '') {
        str += hs + "#";
    } else {
        str += "#";
    }

    if (str != '') {
        str = str.substring(0, str.length - 1);
    }

    return str;
}
//设置路、弄、号、市的组装数据解析到各个元素(miniui元素的ID，以及组装值)
function setAdrCge(jln, hs, val) {

    var adr = val.split("#");
    if (adr[0] != null && adr[0] != '') {
        mini.get(jln).setValue($.trim(adr[0]));
    }
    if (adr[0] != null && adr[0] != '') {
        mini.get(hs).setValue($.trim(adr[1]));
    }
}
////////////////////////////////////////////////////////////////////


//联动地址校验
function isAddressVal(str, qx, jln) {
    if (qx.charAt(5) == '0') {
        mini.alert(str + "必须选择到区县或者街道");
        return false;
    }
    //不填写路
    if (jln == null || jln == '') {
        if (qx.charAt(8) == '0') {
            mini.alert(str + "街/路/弄为空，必须选择到街道");
            return false;
        }
    }
    return true;
}

/*
 * 联动地址校验
 * param flag 地址标记 1=户籍地址  2=居住地址
 * param areaCode 行政区划代码
 * param lu 路
 */
function isAddressVal2(flag, areaCode, jln) {
    var str;
    if (flag == 1) {
        str = "户籍地址";
    }
    if (flag == 2) {
        str = "居住地址";
    }
    if (areaCode.charAt(5) == '0') {
        mini.alert(str + "必须选择到区县或者街道");
        return false;
    }
    //不填写路
    if (jln == null || jln == '') {
        if (qx.charAt(8) == '0') {
            prefix = areaCode.substring(0, 3);
            if (prefix == '310') {
                mini.alert(str + "街/路/弄为空，必须选择到街道");
                return false;
            }
        }
    }
    return true;
}
//地址的路弄号转换
function addressCge(jln, hs) {
    var str = '';
    if (jln && jln != '') {
        str += jln + "#";
    } else {
        str += "#";
    }
    if (hs && hs != '') {
        str += hs + "#";
    } else {
        str += "#";
    }

    if (str != '') {
        str = str.substring(0, str.length - 1);
    }
    return str;
}


//电话号码二选一校验
function isPhones(sj, dh) {
    var psj = /(13\d|14[57]|15[^4,\D]|17[678]|18\d)\d{8}|170[059]\d{7}/;
    var pdh = /^\d{3}-\d{8}|\d{4}-\d{7}$/;
    if ((sj == null || sj == '') && (dh == null || dh == '')) {
        mini.alert("手机号码和电话号码请二选一!");
        return false;
    }
    if (sj != null && sj != '' && dh != null && dh != '') {
        if (psj.test(sj) == false) {
            mini.alert("请填写正确格式的手机号码!");
            return false;
        }
        if (pdh.test(dh) == false) {
            mini.alert("请填写正确格式的电话号码!");
            return false;
        }
    }
    if (sj != null && sj != '' && (dh == null || dh == '')) {
        if (psj.test(sj) == false) {
            mini.alert("请填写正确格式的手机号码!");
            return false;
        }
    }
    if ((sj == null || sj == '') && dh != null && dh != '') {
        if (pdh.test(dh) == false) {
            mini.alert("请填写正确格式的电话号码!");
            return false;
        }
    }
    return true;
}

////////////////////////////////////
/* 是否英文 */
function isEnglish(v) {
    var re = new RegExp("^[a-zA-Z\_]+$");
    if (re.test(v)) return true;
    return false;
}
/* 是否英文+数字 */
function isEnglishAndNumber(v) {
    var re = new RegExp("^[0-9a-zA-Z\_]+$");
    if (re.test(v)) return true;
    return false;
}
/* 是否汉字 */
function isChinese(v) {
    var re = new RegExp("^[\u4e00-\u9fa5]+$");
    if (re.test(v)) return true;
    return false;
}
/* 是否汉字或英文*/
function isChineseAndEnglish(v) {
    var reg = new RegExp("^[\u4E00-\u9FA5a-zA-Z.·\_]+$");
    if (!reg.test(v)) return false;
    return true;
}
/* 身份证严格验证*/
function isIDCard(v) {
    //18位身份证号码的正则表达式
    var regIdCard = /^(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;
    // 如果通过该验证，说明身份证格式正确，但准确性还需计算
    if (regIdCard.test(v)) {
        // 将前17位加权因子保存在数组里
        var idCardWi = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
        // 这是除以11后，可能产生的11位余数、验证码，也保存成数组
        var idCardY = new Array(1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2);
        // 用来保存前17位各自乖以加权因子后的总和
        var idCardWiSum = 0;
        for (var i = 0; i < 17; i++) {
            idCardWiSum += v.substring(i, i + 1) * idCardWi[i];
        }
        // 计算出校验码所在数组的位置
        var idCardMod = idCardWiSum % 11;
        // 得到最后一位身份证号码
        var idCardLast = v.substring(17);
        // 如果等于2，则说明校验码是10，身份证号码最后一位应该是X
        if (idCardMod == 2) {
            if (idCardLast == "X" || idCardLast == "x")    return true;
            return false;
        } else {
            // 用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
            if (idCardLast == idCardY[idCardMod]) return true;
            return false;
        }
    }
    return false;
}

/*自定义vtype*/
mini.VTypes["englishErrorText"] = "请输入英文";
mini.VTypes["english"] = function (v) {
    var re = new RegExp("^[a-zA-Z\_]+$");
    if (re.test(v)) return true;
    return false;
}


//姓名的色标改变（fl颜色分类 zs是否紫色 rs不宜继续妊娠 xm姓名 path资源路径地址）
/**function chanageXm(fl,zs,rs,xm,path){
	var flDiv = "";//颜色div
	var zsflDiv = "";//紫色div
	var burenshen="";
	  if (fl == 2){
	    	flDiv ='<img class="img" align="left" src="'+path+'/images/ls.png">';
	    } else if(fl == 3) {
	    	flDiv = '<img class="img" align="left" src="'+path+'/images/ys.png">';
	    }else if(fl == 4) {
	    	flDiv = '<img class="img" align="left" src="'+path+'/images/cs.png">';
	  	}else if(fl == 5) {
	  		flDiv = '<img class="img" align="left" src="'+path+'/images/hs.png">';
	  	}
	  
	  if(zs==1){
			zsflDiv = '<img class="img" align="left" src="'+path+'/images/zs.png">';
		}
   if(rs==1){
	   burenshen='<img class="img " align="left" src="'+path+'/images/u231.png">';
   }

   return "<div style='width: 53px;float: left;'>"+flDiv + zsflDiv +burenshen+"</div><div style='width:auto;float: right;'>"+xm+"</div>";	
}**/


/**
 * 根据预警分类显示图标
 * @param e
 */
function onLoadColor(e, contentPath) {
    var record = e.record;
    var greenCodeCount = record.greenCodeCount;
    var yellowCodeCount = record.yellowCodeCount;
    var orangeCodeCount = record.orangeCodeCount;
    var redCodeCount = record.redCodeCount;
    var purpleCodeCount = record.purpleCodeCount;
    var byjxrs = record.byjxrs;

    //紫色
    var purpleImgHtml = "";
    //不含紫色图标
    var singleIllImgHtml = "";
    //不宜继续妊娠
    var unGestationImgHtml = "";

    //最终返回颜色
    var returnHtml = "";

    //处理紫色
    if (purpleCodeCount) {
        var purpleNum = parseInt(purpleCodeCount);
        if (purpleNum === 1) {
            //紫色图标
            purpleImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/zs.png'>";
        } else {
            //紫色带星星图标
            purpleImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/zs_xx.png'>";
        }
    }

    //处理不含紫色--黄色
    if (yellowCodeCount) {
        var yellowNum = parseInt(yellowCodeCount);
        if (yellowNum === 1) {
            //红色图标
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/ys.png'>";
        } else {
            //红色带星星
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/ys_xx.png'>";
        }
    }

    //处理不含紫色--橙色
    if (orangeCodeCount) {
        var orgageNum = parseInt(orangeCodeCount);
        if (orgageNum === 1) {
            //红色图标
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/cs.png'>";
        } else {
            //红色带星星
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/cs_xx.png'>";
        }
    }

    //处理不含紫色--红色
    if (redCodeCount) {
        var redNum = parseInt(redCodeCount);
        if (redNum === 1) {
            //红色图标
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/hs.png'>";
        } else {
            //红色带星星
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/hs_xx.png'>";
        }
    }

    //处理不含紫色--绿色(如果是绿色，那么紫色可以清空掉)
    if (greenCodeCount) {
        if (greenCodeCount == "1") {
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/ls.png'>";
            purpleImgHtml = "";
        }
    }

    //处理不宜继续妊娠
    if (byjxrs) {
        if (byjxrs == "1") {
            unGestationImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/u231.png'>";
        }
    }

    returnHtml = unGestationImgHtml + purpleImgHtml + singleIllImgHtml;

    return returnHtml;
}

//姓名和颜色标示分开两列处理 @Maggie 20151110 10:09
function chanageXm(fl, zs, rs, xm, path) {

    var flDiv = "";//颜色div
    var zsflDiv = "";//紫色div
    var burenshen = "";
    if (fl == 2) {
        flDiv = '<img class="img" align="center" src="' + path + '/images/ls.png">';
    } else if (fl == 3) {
        flDiv = '<img class="img" align="center" src="' + path + '/images/ys.png">';
    } else if (fl == 4) {
        flDiv = '<img class="img" align="center" src="' + path + '/images/cs.png">';
    } else if (fl == 5) {
        flDiv = '<img class="img" align="center" src="' + path + '/images/hs.png">';
    }

    if (zs == 1) {
        zsflDiv = '<img class="img" align="center" src="' + path + '/images/zs.png">';
    }
    if (rs == 1) {
        burenshen = '<img class="img " align="center" src="' + path + '/images/u231.png">';
    }
    if (fl == 2 && (zs == 1 || rs == 1)) {
        return "<div style='width: 40px;float: center;'>" + zsflDiv + burenshen + "</div>";
    }
    var str = '<img class="img" align="center" src="' + path + '/images/yj.png">';

    return "<div style='width: 40px;float: center;'>" + flDiv + zsflDiv + burenshen + str + "</div>";
}

//姓名和颜色标示分开两列处理 @Maggie 20151110 10:09
function chanageXm(fl, zs, rs, xm, path, yjqk, yid, duoji) {

    var flDiv = "";//颜色div
    var zsflDiv = "";//紫色div
    var burenshen = "";
    var divBegin = "<div style='width: 40px;float: cnerter;' ";
    var divEnd = "</div>";

    if (fl == 2) {
        flDiv = '<img class="img" align="center" src="' + path + '/images/ls.png">';
    } else if (fl == 3) {
        flDiv = '<img class="img" align="center" src="' + path + '/images/ys.png">';
    } else if (fl == 4) {
        flDiv = '<img class="img" align="center" src="' + path + '/images/cs.png">';
    } else if (fl == 5) {
        flDiv = '<img class="img" align="center" src="' + path + '/images/hs.png">';
    }

    if (zs == 1) {
        zsflDiv = '<img class="img" align="center" src="' + path + '/images/zs.png">';
    }
    if (rs == 1) {
        burenshen = '<img class="img" title="不宜继续妊娠" align="center" src="' + path + '/images/u231.png">';
    }

    var yjts = '';//预警提示
    if (yjqk == 1) {
        yjts = '<img class="img" title="预警颜色有变化" align="center" src="' + path + '/images/yjup.png">';
        divBegin += ' onClick=viewYjysLog("' + yid + '") >';
    } else if (yjqk == 2) {
        yjts = '<img class="img" title="预警颜色有变化" align="center" src="' + path + '/images/yjdown.png">';
        divBegin += ' onClick=viewYjysLog("' + yid + '") >';
    } else {
        divBegin += ">";
    }

    //预警分类，是否存在多个疾病
    if (duoji == 1) {
        burenshen += '<img class="img" title="存在多个疾病" align="center" src="' + path + '/images/duoji.png">';
    }

    if (fl == 2 && (zs == 1 || rs == 1)) {
        return divBegin + zsflDiv + burenshen + yjts + divEnd;
    }
    return divBegin + flDiv + zsflDiv + burenshen + yjts + divEnd;
}
//ajax获取预警分类，同一颜色中是否存在多个疾病
function gtyjfl(tag, usid) {
    var duoji = 0;
    $.ajax({
        url: "/fysite/a/yunbao/chanfu/gtDuoji/" + tag + "/" + usid,
        type: "get",
        async: false,
        success: function (data) {
            duoji = data;
        }
    });
    return duoji;
}
//中转方法，用于在原有预警状态的字段渲染的基础上新增 多疾病图标，兼任多种类型，并不影响到其他功能。
function cgeXm(fl, zs, rs, xm, path, yjqk, yid, usid, tag) {
    //预警分类，是否存在多个疾病
    var duoji = 0;
    if (tag == 0) {//默认不启用
        return chanageXm(fl, zs, rs, xm, path, yjqk, yid, duoji);
    }
    if (tag == 1) {//默认usid
        duoji = gtyjfl("usid", usid);
        return chanageXm(fl, zs, rs, xm, path, yjqk, yid, duoji);
    }
    if (tag == 2) { //默认yid
        duoji = gtyjfl("yid", yid);
        return chanageXm(fl, zs, rs, xm, path, yjqk, yid, duoji);
    }
}


/*按身份证号设置年龄
 * param ageId 年龄控件id
 * param zjhm 身份证号
 */

function calAge(ageId, zjhm) {
    var year = zjhm.substring(6, 10);
    var month = zjhm.substring(10, 12);
    var day = zjhm.substring(12, 14);
    var str = year + "-" + month + "-" + day;
    var d1 = mini.parseDate(str);
    var d2 = new Date();
    var days = (d2.getTime() - d1.getTime()) / (1000 * 3600 * 24);
    var age = Math.floor(days / 365);
    mini.get(ageId).setValue(age);

}

/**
 * 根据证件号码，计算年龄，并设置age
 * @param ageId
 * @param zjhm
 * @param todayStr
 */
function calAgeNew(ageId, zjhm, todayStr) {
    var year = parseInt(zjhm.substring(6, 10));
    var month = parseInt(zjhm.substring(10, 12));
    var day = parseInt(zjhm.substring(12, 14));


    var nowYear = parseInt(todayStr.substring(0, 4));
    var nowMonth = parseInt(todayStr.substring(5, 7));
    var nowDay = parseInt(todayStr.substring(8, 10));

    var age = nowYear - year;
    if(nowMonth < month){
        age--;
    }else if(nowMonth = month && nowDay < day) {
        age--;
    }
    mini.get(ageId).setValue(age);
}

/**
 * 变更出生日期时，返回年龄
 * @param birthday
 * @param todayStr
 * @returns {number}
 */
function changeBirthday( birthday, todayStr) {
    var year = parseInt(birthday.substring(0, 4));
    var month = parseInt(birthday.substring(5, 7));
    var day = parseInt(birthday.substring(8, 10));

    var nowYear = parseInt(todayStr.substring(0, 4));
    var nowMonth = parseInt(todayStr.substring(5, 7));
    var nowDay = parseInt(todayStr.substring(8, 10));

    var age = nowYear - year;
    if(age < 0){
        return 0;
    }
    if(nowMonth < month){
        age--;
    }else if(nowMonth = month && nowDay < day) {
        age--;
    }
  return age;
}

var readerIsInit = false;
/*
 * 读卡
 * param zjlxId 证件类型控件id
 * param zjhmId 证件号码控件id
 * param xmId 姓名控件id
 * param ageId 年龄控件id
 */
function readCard(zjlxId, zjhmId, xmId, ageId) {
    try {
        var obj = document.getElementById('CardReader');
        if (false == readerIsInit) {
            //设置端口号，1表示串口1，2表示串口2，依此类推；1001表示USB。0表示自动选择
            obj.setPortNum(0);
            readerIsInit = true;
        }
        //使用重复读卡功能
        obj.Flag = 0;
        //读卡
        var rst = obj.ReadCard();
        //获取各项信息
        if (0x90 == rst) {
            var sex = obj.CardNo().charAt(16);
            if (sex % 2 != 0) {
                mini.alert("您输入的身份证为男性身份证！");
                return false;
            }

            mini.get(zjlxId).setValue('01');
            mini.get(zjhmId).setValue(obj.CardNo());
            mini.get(xmId).setValue(obj.NameL());
            //计算年龄
            calAge(ageId, obj.CardNo());
            return true;
        }
        return false;
    } catch (e) {
        mini.alert("请安装驱动，并接上规定的读卡器！");
        return false;
    }
}

//查询孕妇是否已结案       true:已结案,false:未结案 @cuixuezhi 20151116
//"${ctx}/yunbao/chanfu/ifJieAn/"+yid
function ifJieAn(path, yid) {
    var result = true;
    $.ajax({
        url: path + "/yunbao/chanfu/ifJieAn/" + yid,
        type: "post",
        async: false,
        data: "",
        success: function (text) {
            if (text == "success") {
                result = true;//已结案
            } else {
                result = false;//未结案
            }
        }, error: function () {
            mini.alert('查询孕妇是否结案发生异常！', '提示！');
            form.unmask();
            return;
        }
    });
    return result;
}

//查询孕妇是否已分娩       true:已分娩,false:未分娩 @cuixuezhi 20151116
//"${ctx}/yunbao/chanfu/ifFenMian/"+yid
function ifFenMian(path, yid) {
    var result = true;
    $.ajax({
        url: path + "/yunbao/chanfu/ifFenMian/" + yid,
        type: "post",
        async: false,
        data: "",
        success: function (text) {
            if (text == "success") {
                result = true;//已结案
            } else {
                result = false;//未结案
            }
        }, error: function () {
            mini.alert('查询孕妇是否分娩发生异常！', '提示！');
            form.unmask();
            return;
        }
    });
    return result;
}

/*
 * @cuixuezhi 2015-11-26
 *
 * TAB动态显示
 *
 * tabsName 总tab名称
 * name 弹出tab的名字
 * title 弹出tab显示title内容
 * url 弹出tab跳转的请求或页面
 * showCloseFlag 是否显示关闭按钮
 */
function showTab(tabsName, name, title, url, showCloseFlag) {
    var tabs = mini.get(tabsName);
    //打开tab
    var addTab = tabs.getTab(name);
    if (!addTab) {
        addTab = {
            name: name,
            title: title,
            url: url,
            showCloseButton: showCloseFlag
        };
        tabs.addTab(addTab);
        tabs.activeTab(addTab);
    } else {
        tabs.activeTab(addTab);
    }
}


function queryCard(zjhmId, xmId, grid) {
    try {
        var obj = document.getElementById('CardReader');
        if (false == readerIsInit) {
            //设置端口号，1表示串口1，2表示串口2，依此类推；1001表示USB。0表示自动选择
            obj.setPortNum(0);
            readerIsInit = true;
        }
        //使用重复读卡功能
        obj.Flag = 0;
        //读卡
        var rst = obj.ReadCard();
        //获取各项信息
        if (0x90 == rst) {
            var sex = obj.CardNo().charAt(16);
            /*if(sex%2!=0){
             mini.alert("您输入的身份证为男性身份证！");
             return false;
             }*/
            mini.get(zjhmId).setValue(obj.CardNo());
            mini.get(xmId).setValue(obj.NameL());
            var param = {
                xm: obj.NameL(),
                zjhm: obj.CardNo(),
                zjlx: '01'
            }
            mini.get(grid).load(param);
        }
    }
    catch (e) {
        mini.alert("请安装驱动，并接上规定的读卡器！");
    }
}

function cgeUrls(obj) {
    var pars = '';
    for (i in obj) {
        pars += '&' + i + "=" + encodeURI(encodeURI(obj[i]));
    }
    return pars.substr(1).replace(' ', '');
}

function ifShangHai(st) {
    var str = st.substring(0, 2);
    if (str == '31') {
        return true;
    } else {
        return false;
    }
}

//获取当前系统时间字符串yyyy-MM-dd 23:59:59
function getNowFormatDate(date) {
    var day = date;
    var Year = 0;
    var Month = 0;
    var Day = 0;
    var CurrentDate = "";
    Year = day.getFullYear();//支持IE和火狐浏览器.
    Month = day.getMonth() + 1;
    Day = day.getDate();
    CurrentDate += Year;
    if (Month >= 10) {
        CurrentDate += "-" + Month;
    }
    else {
        CurrentDate += "-" + "0" + Month;
    }
    if (Day >= 10) {
        CurrentDate += "-" + Day;
    }
    else {
        CurrentDate += "-" + "0" + Day;
    }
    return CurrentDate + " 23:59:59";
}

//获取当前日期最大时间
function getMaxNewDate() {
    var dateStr = getNowFormatDate();
    return Date.parse(dateStr.replace(/-/g, "/"));
}

//计算天数差的函数，通用
function DateDiff(sDate1, sDate2) {    //sDate1和sDate2是2006-12-18格式
    var aDate, oDate1, oDate2, iDays
    aDate = sDate1.split("-")
    oDate1 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0])    //转换为12-18-2006格式
    aDate = sDate2.split("-")
    oDate2 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0])
    iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24)    //把相差的毫秒数转换为天数
    return iDays
}

//验证血色素小于90红色  大于160红色提醒
function xssCheck(name) {
    var xss = mini.get(name).getValue();
    if (Number(xss) < 90) {
        mini.get(name).addCls("red-span");
    } else if (Number(xss) > 160) {
        mini.get(name).addCls("red-span");
    } else {
        mini.get(name).removeCls("red-span");
    }
}

function getYYYYMMDDHHFFMM(time) {
    if (time == null || time == '') {
        return "";
    }
    var year = time.getFullYear();
    var month = time.getMonth() + 1;
    var date = time.getDate();
    var hours = time.getHours();
    var minutes = time.getMinutes();
    var second = time.getSeconds();

    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }
    if (hours < 10) {
        hours = "0" + hours;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (second < 10) {
        second = "0" + second;
    }

    var timeStr = time.getFullYear() + "-" + (time.getMonth() + 1) + "-" + time.getDate() + " " + time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds();
    return timeStr;
}

function getYYYYMMDD(time) {
    if (time == null || time == '') {
        return "";
    }
    var year = time.getFullYear();
    var month = time.getMonth() + 1;
    var date = time.getDate();

    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }

    var timeStr = year + "-" + month + "-" + date;
    return timeStr;
}

/*
 *
 * 获取UTC时间
 * 通过字符串替换实现
 */
function getNowDate(time) {
    var date = time;

    var index = time.indexOf('CST');//找到cst位置
    var cstTime = date.substr((index - 9), 8);
    var cstHour = date.substr((index - 9), 2);//截取时间
    var utcHour = parseInt(cstHour) + 8;//时间+8小时区时

    var t = date.substr(0, (index - 9)) + utcHour + date.substr(index - 7);
    date = t.replace('CST', 'UTC+0800');
    return date;
}

//设置颜色
function setXYcolor(ssy, szy) {
    var ssyVal = mini.get(ssy).getValue();//收缩压
    var szyVal = mini.get(szy).getValue();//舒张压
    if (Number(ssyVal) > 140) {
        mini.get(ssy).addCls("red-span");
    } else {
        mini.get(ssy).removeCls("red-span");
    }
    if (Number(szyVal) > 0 && (Number(szyVal) < 60 || Number(szyVal) > 90)) {
        mini.get(szy).addCls("red-span");
    } else {
        mini.get(szy).removeCls("red-span");
    }
}

//打开tab关闭后grid自动刷新
function showTab(tabsName, name, title, url, showCloseFlag, grid) {
    var tabs = parent.mini.get(tabsName);
    //打开tab
    var addTab = tabs.getTab(name);
    if (!addTab) {
        addTab = {
            name: name,
            title: title,
            url: url,
            //refreshOnClick : true,
            showCloseButton: showCloseFlag,
            ondestroy: function (action) {
                grid.reload();//tab销毁后刷新主tab的grid数据
            }
        };
        tabs.addTab(addTab);
        tabs.activeTab(addTab);
    } else {
        tabs.activeTab(addTab);
    }
}

//打开窗口，弹出页面，关闭后自动刷新列表grid
function showWin(win, url, title, grid, width, height) {
    if (width == '' || width == undefined) {
        width = 800;
    }
    if (height == '' || height == undefined) {
        height = 500;
    }

    win = mini.open({
        url: url,
        title: title,
        width: width,
        height: height,
        onload: function () {
        },
        ondestroy: function (action) {//win关闭时执行
            grid.load();//刷新主页面
        }
    });
}

/**
 * 判断不为空
 * @param char
 * @returns {boolean}
 */
function isNotBlank(char) {
    char = $.trim(char);
    if (char != null && char != '' && char.length != 0) {
        return true;
    }
    return false;
}

/**
 * 判断为空
 * @param char
 * @returns {boolean}
 */
function isBlank(char) {
    char = $.trim(char);
    if (char == null || char == '' || char.length == 0) {
        return true;
    }
    return false;
}


function contansKey(keys, key) {
    if (isNotBlank(keys)) {
        var ks = keys.split(",");
        if (ks.length > 0) {
            for (var i = 0; i < keys.length; i++) {
                if (ks[i] == key) {
                    return true;
                }
            }
        }
    }

    return false;
}


/**
 * 根据预警分类显示图标(新的，添加上升还是下降，和点击事件展示历史诊断)
 * @param e
 */
function onLoadColorNew(e, contentPath, ctx) {
    var record = e.record;
    var greenCodeCount = record.greenCodeCount;
    var yellowCodeCount = record.yellowCodeCount;
    var orangeCodeCount = record.orangeCodeCount;
    var redCodeCount = record.redCodeCount;
    var purpleCodeCount = record.purpleCodeCount;
    var byjxrs = record.byjxrs;
    var status = record.bgStatus;
    //紫色
    var purpleImgHtml = "";
    //不含紫色图标
    var singleIllImgHtml = "";
    //不宜继续妊娠
    var unGestationImgHtml = "";

    //最终返回颜色
    var returnHtml = "";
    //预警变动	0无升降 1升 2降
    var downorup = "";

    //处理预警变动	0无升降 1升 2降

    if (isNotBlank(status)) {
        if ("1" == status) {
            downorup = "<img class='img' align='center' src='" + contentPath + "/images/yjup.png'>";
        }
        if ("2" == status) {
            downorup = "<img class='img' align='center' src='" + contentPath + "/images/yjdown.png'>";
        }
    }


    //处理紫色
    if (purpleCodeCount) {
        var purpleNum = parseInt(purpleCodeCount);
        if (purpleNum === 1) {
            //紫色图标
            purpleImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/zs.png'>";
        } else {
            //紫色带星星图标
            purpleImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/zs_xx.png'>";
        }
    }

    //处理不含紫色--黄色
    if (yellowCodeCount) {
        var yellowNum = parseInt(yellowCodeCount);
        if (yellowNum === 1) {
            //红色图标
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/ys.png'>";
        } else {
            //红色带星星
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/ys_xx.png'>";
        }
    }

    //处理不含紫色--橙色
    if (orangeCodeCount) {
        var orgageNum = parseInt(orangeCodeCount);
        if (orgageNum === 1) {
            //红色图标
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/cs.png'>";
        } else {
            //红色带星星
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/cs_xx.png'>";
        }
    }

    //处理不含紫色--红色
    if (redCodeCount) {
        var redNum = parseInt(redCodeCount);
        if (redNum === 1) {
            //红色图标
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/hs.png'>";
        } else {
            //红色带星星
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/hs_xx.png'>";
        }
    }

    //处理不含紫色--绿色(如果是绿色，那么紫色可以清空掉)
    if (greenCodeCount) {
        if (greenCodeCount == "1") {
            singleIllImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/ls.png'>";
            purpleImgHtml = "";
        }
    }

    //处理不宜继续妊娠
    if (byjxrs) {
        if (byjxrs == "1") {
            unGestationImgHtml = "<img class='img' align='center' src='" + contentPath + "/images/u231.png'>";
        }
    }

    returnHtml = downorup + unGestationImgHtml + purpleImgHtml + singleIllImgHtml;
    returnHtml = '<span onclick="yjhistory(\'' + record.yid + '\', \'' + ctx + '\')">' + returnHtml + '</span>';
    return returnHtml;
}


//打开历次诊断
function yjhistory(yid, ctx) {

    var url = ctx + "/yunbao/chanfu/viewYjysLog/" + yid;

    var win = mini.open({
        url: url,
        title: "孕产妇预警记录",
        width: 800,
        height: 500,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

//判断显示预警颜色，和 显示 是否有★
function getYjcolor(greencode, yellowcode, cscode, redcode, zscode, path) {

    var zshtml = "";	//紫色图表

    //绿色
    if (greencode) {
        return "<img class='img' align='center' src='" + path + "/images/ls.png'>";
    }


    //紫色
    if (zscode) {
        var zsNum = parseInt(pgnrNum(zscode));

        if (zsNum == 1) {
            zshtml = "<img class='img' align='center' src='" + path + "/images/zs.png'>";
        }
        if (zsNum > 1) {//紫色带星星图标
            zshtml = "<img class='img' align='center' src='" + path + "/images/zs_xx.png'>";
        }
    }

    if (redcode) {	//红色原点
        var redNum = parseInt(pgnrNum(redcode));
        if (redNum == 1) {
            return "<img class='img' align='center' src='" + path + "/images/hs.png'>" + zshtml;
        }
        if (redNum > 1) {//红色带星星图标
            return "<img class='img' align='center' src='" + path + "/images/hs_xx.png'>" + zshtml;
        }
    }
    //橙色
    if (cscode) {

        var csdNum = parseInt(pgnrNum(cscode));
        if (csdNum == 1) {	//橙色原点
            return "<img class='img' align='center' src='" + path + "/images/cs.png'>" + zshtml;
        }
        if (csdNum > 1) {//橙色带星星图标
            return "<img class='img' align='center' src='" + path + "/images/cs_xx.png'>" + zshtml;
        }
    }


    if (yellowcode) {		//黄色
        var yellowdNum = parseInt(pgnrNum(yellowcode));
        if (yellowdNum == 1) {	//黄色原点

            return "<img class='img' align='center' src='" + path + "/images/ys.png'>" + zshtml;
        }
        if (yellowdNum > 1) {//黄色带星星图标

            return "<img class='img' align='center' src='" + path + "/images/ys_xx.png'>" + zshtml;
        }
    }

    return zshtml;
}

/**
 * 计算评估内容，同一颜色预警选择个数
 * */
function pgnrNum(code) {
    var num = parseInt(0);

    var codes = code.split(",");
    for (var i = 0; i < codes.length; i++) {
        if (isNotBlank(codes[i])) {

            num++;
        }
    }
    return num;
}

/**
 * 获取预警分类
 * @param greencode
 * @param yellowcode
 * @param cscode
 * @param redcode
 * @returns {*}
 */
function getyjfl(greencode, yellowcode, cscode, redcode) {
    if (greencode) {
        return "1";
    }

    if (redcode) {
        return "4";
    }


    if (cscode) {
        return "3";
    }

    if (yellowcode) {
        return "2";
    }
    //如都没有，则默认为绿色，正常
    return "1";


}


function  getAgeByBirthOfAjax(ctx, birthday, ageId){
    if(isBlank(birthday)){
        return mini.alert("出生日期不能为空！");
    }
    if(isBlank(ageId)){
        return mini.alert("年龄ID不能为空！");
    }
    var  url = ctx + "/yunbao/chanfu/getAge";
    var param = {"birthday" : birthday };
    $.ajax({
        url : url,
        type : "post",
        data : param,
        success : function(age){
            mini.get(ageId).setValue(age);
        },
        error : function(){
            mini.alert("计算年龄发生异常！");
            mini.get(ageId).setValue("");
        }
    });

}

