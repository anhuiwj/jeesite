(function($) {
    //六月份测试数据 18号和15号有数据  *!这里没有加月份*

    var testData={
            day_18:'  黄娟娟预约了 剖腹产,定在b产区，8:30开始。需要预先了解病人档案...',
            day_15:'  黄娟娟预约了 剖腹产,定在A产区，14:30开始。需要预先了解病人档案...'
    }

	function calendarWidget(el, params) {

		var now   = new Date();
        var nowday=now.getDate();
		var thismonth = now.getMonth();
		var thisyear  = now.getFullYear();
        //alert(thisyear);
		//console.log(nowday);
		var opts = {
			month: thismonth,
			year: thisyear
		};

		$.extend(opts, params);

		var monthNames = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'];
		var dayNames = ['日', '一', '二', '三', '四', '五', '六'];
		month = i = parseInt(opts.month);
		year = parseInt(opts.year);
		var m = 0;
		var table = '';

			// next month
			if (month == 11) {
				var next_month = '<a date-day="'+0+','+(year+1)+'" href="?month=' + 1 + '&amp;year=' + (year + 1) + '" title="' + monthNames[0] + ' ' + (year + 1) + '">' + monthNames[0] + ' ' + (year + 1) + '</a>';
			} else {
				var next_month = '<a date-day="'+(month+1)+','+year+'" href="?month=' + (month + 2) + '&amp;year=' + (year) + '" title="' + monthNames[month + 1] + ' ' + (year) + '">' + monthNames[month + 1] + ' ' + (year) + '</a>';
			}

			// previous month
			if (month == 0) {
				var prev_month = '<a date-day="'+11+','+(year-1)+'" href="?month=' + 12 + '&amp;year=' + (year - 1) + '" title="' + monthNames[11] + ' ' + (year - 1) + '">' + monthNames[11] + ' ' + (year - 1) + '</a>';
			} else {
				var prev_month = '<a  date-day="'+(month-1)+','+year+'" href="?month=' + (month) + '&amp;year=' + (year) + '" title="' + monthNames[month - 1] + ' ' + (year) + '">' + monthNames[month - 1] + ' ' + (year) + '</a>';
			}

			table += ('<h3 class="calendar_title" id="current-month"><a class="calendar_left_btn" title="上一月" href="javascript:void(0);" ></a><span>'+monthNames[month]+' '+year+'</span><a class="calendar_right_btn" title="下一月" href="javascript:void(0);" ></a></h3>');
			// uncomment the following lines if you'd like to display calendar month based on 'month' and 'view' paramaters from the URL
			table += ('<div style="display: none" class="nav-prev">'+ prev_month +'</div>');
			table += ('<div style="display: none" class="nav-next">'+ next_month +'</div>');
			table += ('<table class="calendar_table tbcol" ' +'id="calendar-month'+i+' " cellspacing="0">');

			table += '<tr>';

			for (d=0; d<7; d++) {
				table += '<th class="weekday">' + dayNames[d] + '</th>';
			}

			table += '</tr>';

			var days = getDaysInMonth(month,year);
            var firstDayDate=new Date(year,month,1);
            var firstDay=firstDayDate.getDay();

			var prev_days = getDaysInMonth(month,year);
            var firstDayDate=new Date(year,month,1);
            var firstDay=firstDayDate.getDay();

			var prev_m = month == 0 ? 11 : month-1;
			var prev_y = prev_m == 11 ? year - 1 : year;
			var prev_days = getDaysInMonth(prev_m, prev_y);
			firstDay = (firstDay == 0 && firstDayDate) ? 7 : firstDay;

			var i = 0;
            for (j=0;j<42;j++){

              if ((j<firstDay)){
                table += ('<td class="other-month"><div class="day_box">'+ (prev_days-firstDay+j+1) +'</div></td>');
			  } else if ((j>=firstDay+getDaysInMonth(month,year))) {
				i = i+1;
                table += ('<td class="other-month"><div class="day_box">'+ i +'</div></td>');
              }else{

                if(nowday>(j-firstDay+1)){
                    //过期数据
                    if(testData['day_'+(j-firstDay+1)]){
                        table += ('<td class="current-month day'+(j-firstDay+1)+'">' +
                                    '<div class="day_box">'+(j-firstDay+1)+
                                        '<em></em><div class="day_box_sub">' +
                                                '<p class="p1">'+
                                                  testData['day_'+(j-firstDay+1)]+
                                                 '</p>'+
                                                '<div class="calendar_more"><a class="look_more" href="">详情</a>' +
                                                '</div>' +
                                        '</div>' +
                                    '</div>' +
                                   '</td>')
                    }else{
                        table += ('<td class="current-month day'+(j-firstDay+1)+'"><div class="day_box">'+(j-firstDay+1)+'</div></td>')
                    }

                }else{
                    //未过期数据
                    if(testData['day_'+(j-firstDay+1)]){
                        table += ('<td class="current-month day'+(j-firstDay+1)+'">' +
                                        '<div class="day_box">'+(j-firstDay+1)+
                                            '<em class="c_green"></em><div class="day_box_sub">' +
                                                '<p class="p1">'+
                                                testData['day_'+(j-firstDay+1)]+
                                                '</p>'+
                                                '<div class="calendar_more"><a class="look_more" href="">详情</a>' +
                                                '</div>' +
                                            '</div>' +
                                        '</div>' +
                                    '</td>')
                    }else{
                        table += ('<td class="current-month day'+(j-firstDay+1)+'"><div class="day_box">'+(j-firstDay+1)+'</div></td>')
                    }

                }
              }
              if (j%7==6)  table += ('</tr>');
            }

            table += ('</table>');

		el.html(table);
        el.attr("year",year);
        el.attr("month",month);
        if(thismonth==opts.month){
            $(".day"+nowday).find('.day_box').addClass("red_bg");
        }

	}

	function getDaysInMonth(month,year)  {
		var daysInMonth=[31,28,31,30,31,30,31,31,30,31,30,31];
		if ((month==1)&&(year%4==0)&&((year%100!=0)||(year%400==0))){
		  return 29;
		}else{
		  return daysInMonth[month];
		}
	}


	// jQuery plugin initialisation
	$.fn.calendarWidget = function(params) {
        var that=this;
		calendarWidget(this, params);

        //上一个月
        this.delegate(".calendar_left_btn","click",function(){
            var arr=$('.nav-prev>a').attr('date-day').split(',');
            var params={
                month:parseInt(arr[0]),
                year:parseInt(arr[1])
            }
            calendarWidget(that, params);
        });

        //下一个月
        this.delegate(".calendar_right_btn","click",function(){

            var arr=$('.nav-next>a').attr('date-day').split(',');
            var params={
                month:parseInt(arr[0]),
                year:parseInt(arr[1])
            }
            calendarWidget(that, params);
        });

        //鼠标悬停日期展示数据
        this.delegate('.day_box','hover',function(event){
            //console.log(event.type);
            if (event.type == 'mouseenter') {
                $(this).addClass('orange_bg');
                $(this).css('z-index',100)
                $(this).find('.day_box_sub').show();
            } else {
                $(this).find('.day_box_sub').hide();
                $(this).css('z-index','')
                $(this).removeClass('orange_bg');
            }
        });
		return this;
	};

})(jQuery);
