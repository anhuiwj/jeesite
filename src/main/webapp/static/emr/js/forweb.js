var g = {
    TabShow: function (tabNavID, $pageTabBox, Event) {/*简单的tab切换效果*/
        var $pageTabBox = $($pageTabBox);
        var $pageChild = $pageTabBox.children('div');
        $(tabNavID).children('li').not(".more").bind(Event, function () {
            var $this = $(this);
            var index = $this.index();
           // alert(fn);
            $this.addClass('active').siblings().removeClass('active')
            $pageChild.removeClass('active').hide();
            $pageChild.eq(index).addClass('active').show();
            //return false;
        });
    },
    AlertBox: function (elem, alertBox, obj, callbacksure, callbackclose) { //可以增加关闭和其他按钮的回调函数
        if ($(alertBox).length == 0) {
            return
        }
        if($.ui.Dialog){
            var opt = {
                elem: alertBox,
                overlay: false
            }
            if (typeof obj == 'object') {
                $.extend(opt, obj)
            }
            var Dialog = new $.ui.Dialog(elem, opt);
            $(alertBox).find(".close").click(function () {
                Dialog.close();
                if (typeof callbackclose == 'function') {
                    callbackclose();
                }
            });
            $(alertBox).find(".save_btn").click(function () {
                Dialog.close();
                if (typeof callbacksure == 'function') {
                    callbacksure();
                }
            });
        }
    },
    AlertText: function (elem, obj) {//具体配置请参考Dialog库
        if($.ui.Dialog){
            var Dialog = new $.ui.Dialog(elem, obj);
        }
    }
}

$(function () {
    //登录页面的最小高设置
    function resizeMinHeight() {
        var $_Login_body = $('#Login_body');
        var height = $(window).height();
        var minHeight = height - $('.login_footer').height() - 2;
        //alert(minHeight)
        $_Login_body.css('min-height', minHeight);
        $_Login_body.css('_height', minHeight);
    }

    resizeMinHeight();
    $(window).bind('resize', resizeMinHeight);

    /*IE9以下支持placehold*/
    $('input, textarea').placehold();

    /*电子病历头部下拉菜单*/
    $('.header_system_tool>li').hover(function () {
        $(this).addClass('active');
        $('.header_system_sub').stop(true,true).slideDown()
    }, function () {
        $(this).removeClass('active');
        $('.header_system_sub').stop(true,true).slideUp();
    });

    /*病人列表鼠标悬停高亮效果*/
    $('.see_people_table').delegate('tr','mouseover',function(e){
       // console.log(e);
        $(this).addClass('hover');
    });
    $('.see_people_table').delegate('tr','mouseout',function(){
        $(this).removeClass('hover');
    });

    /*个人信息图表弹出框*/
    (function(){
        var tabnav=$("#PrenatalInfoChartAlert").find('.prenatal_tab_nav');
        var tabbox=$("#PrenatalInfoChartAlert").find('.prenatal_tab_box');
        g.AlertBox('#PrenatalInfoChartMore','#PrenatalInfoChartAlert',{overlay: true});
        g.TabShow(tabnav,tabbox,'click');
    })();
    /*日历信息*/
    $("#calendarInfo").calendarWidget();

    /*个人信息显示更多*/
    $('#prenatalInfoMore').click(function () {
        var $hideTR = $("#prenatalInfoTable").find('tr.hide');
        if ($hideTR.is(":hidden")) {
            $hideTR.show();
            $(this).html('收起<i class="more_up_icon"></i>').attr('title', '收起')
        } else {
            $hideTR.hide();
            $(this).html('更多<i class="more_down_icon"></i>').attr('title', '更多')
        }
    });

    /*产前检查tab*/
    g.TabShow('#pageTabNav', '#pageTabBox', 'click');

    /*个人信息图表 tab*/
    g.TabShow('#InfoChartNavTab', '#InfoChartTabBox', 'click');

    /*已就诊未就诊 tab*/
    g.TabShow('#SeePeolpleNav', '#SeePeolpleBox', 'click');

    /*诊疗弹出框*/
    function RecordAlert() {
        var $elem = $('.tables').find('.green');
        $elem.each(function () {
            var alertbox = $(this).attr('data-alert');
            var tab = parseInt($(this).attr('tab'));
            var tabNav = $(alertbox).find('.prenatal_tab_nav');
            var tabBox = $(alertbox).find('.prenatal_tab_box');
            var topHeight = '100px';
            var Dialog = new $.ui.Dialog(this, {
                elem: alertbox,
                // topWindow  :   true, chrom下报错
                fixed: false,
                overlayClose: false,
                top: topHeight      // String        设置对话框的top位置值(须含单位);
            });
            $(alertbox).find('.close').click(function () {
                Dialog.close();
            });
            //添加tab事件标识防止重复绑定
            if (!$(alertbox).attr('data-tab')) {
                g.TabShow(tabNav, tabBox, 'click');
                $(alertbox).attr('data-tab', 'true');
            }
            $(this).bind('click', function () {
                tabNav.children('li').removeClass('active').eq(tab).addClass('active');

                tabBox.children('div').removeClass('active').hide().eq(tab).addClass('active').show();
            });
        });
    }
    RecordAlert();

    //添加新药用记录
    g.AlertBox('.add_new_medicine', '#MedicineNew');
    //查看药用详情
    g.AlertBox('.detail_btn', '#MedicineDetail');

    //添加高危危险评估
    g.AlertBox('#addHighRisk', '#addHighRiskBox', {overlay: true});
    //高危评估结果查看
    g.AlertBox('#HighRiskResultLook', '#HighRiskResult', {overlay: true});

    /*孕妇建档修改信息弹出框*/
    g.AlertBox('#BaseInformationChange', '#BaseInformationChangeAlert', {overlay: true, top: '100px', fixed: false});

    /*医生首页添加日历备忘录*/
    g.AlertBox('.add_canlendar_btn', '#addCanlendarAlert', {overlay: true}, function () {
        g.AlertText('body',
            {
                trigger: 'saveCanlendar',
                autoClose: 4000,
                title: '提示',
                content: '备忘录已保存成功',
                effects: 'slide'
            }
        );
        $('body').trigger('saveCanlendar');
    });

    /*孕期建档保存按钮*/
    g.AlertText('#SureFiling',{
        autoClose: 5000,
        title: '提示',
        content: '王欢 建档成功！',
        yesText    :   '确定',
        yesFn:function(){},
        effects: 'slide'
    });

    if ($.fn.perfectScrollbar) {//有些页面可能没有加载自定义滚动条插件  以防报错
        //医生首页病人列表
        $('#seePeopleTableBox01').perfectScrollbar({suppressScrollX: true});
        $('#seePeopleTableBox02').perfectScrollbar({suppressScrollX: true});
        $('#medicineScrool01').perfectScrollbar();
        $('#medicineScrool02').perfectScrollbar();
        $('#medicineScrool03').perfectScrollbar();
        $('#medicineScrool04').perfectScrollbar();

        $('.prenatal_scroll_div').perfectScrollbar({suppressScrollY: true});
        //病历信息table最小宽设置
        function TableSmallWidth(id) {
            var width = 0;
            var $table = $(id);
            var time;
            $table.find('tr').eq(0).find('th').each(function () {
                width += parseInt($(this).attr('width'));
            });
            $table.css({'width': '100%'});
            //console.log($table.width());
            if ($table.width() < width) {
                $table.css({'width': width});
            }
            $(window).one('resize', function () {
                clearTimeout(time);
                time = setTimeout(function () {
                    TableSmallWidth($table);
                    $('.prenatal_scroll_div').perfectScrollbar('update');
                }, 300)
            })
        }

        TableSmallWidth('#PreviousPrenatalTable');
        TableSmallWidth('#TestResultsTable');
    }
    if ($.fn.vselect) { //select美化
        $('select').vselect({styleString: "width:100%;", direction: 'bottom'});
        $("input[type='text']").addClass('ipt_style');
        $('textarea').addClass('textarea_style');
        $('.record_alert,.alert_box').hide();
    }
    //解决弹出框中的select长度默认为100,史select与input 齐平
    $('.record_alert').find('.f_w_145').each(function () {
        //alert();
        var select = $(this).find('.vselect'),
            id;
        if (select.length != 0) {
            select.css('width', '145px');
            /* id='#'+select.attr('id')+'panel'
             console.log(id);
             $(id).css('width','145px');*/
        }
    });

});
