$(function () {

    //保持页面满屏
    function heightClinet() {
        var heiht = $(window).height();
        var otherheight = heiht - 47;
        //console.log( heiht);
        //console.log( otherheight);
        $(".height_auto").css('height', otherheight);
    }

    heightClinet();
    $(window).bind('resize', heightClinet);

    //左侧菜单满屏
    function seePeopleHeight() {
        var height = $('.see_people').height();
        //alert(height);
        $('.see_people_table_box').css('height', height - 180);
    }
    seePeopleHeight();

    $(window).bind('resize', seePeopleHeight);

    //病人列表显示隐藏
    $(".patient_btn").click(function () {
        //alert();
        if ($(this).hasClass('patient_btn_show')) {
            $(this).removeClass('patient_btn_show');
            $('.patient_view').animate({'left': '-230px'}, 200);
//            $('.iframe_left').css('width','158px');
//            $('.iframe_right').css('margin-left','158px');
        } else {
            $(this).addClass('patient_btn_show');
           /* $('.iframe_left').css('width','230px');
            $('.iframe_right').css('margin-left','230px');*/
            $('.patient_view').animate({'left': '0px'}, 200);
        }
    });
     /*iframe链接地址改变*/
    $('.menu_left_ul').children('li').children('a').bind('click',function(){
        var $this=$(this);
        var src=$(this).attr('href');
        $('#mainframe').attr('src',src);
        $this.parent().addClass('active').siblings().removeClass('active');
        return false;
    })
})
