function keydwon13(p) {
	mini.get(p).focus();
    var $inp = jQuery('input:text');//所有文本框
    $inp.bind('keydown', function (e) {
        var key = e.which;
        if (key == 13) {
            e.preventDefault();
            var nxtIdx = $inp.index(this) + 1;
            jQuery(":input:text:eq(" + nxtIdx + ")").focus();
        }
    });
   
};