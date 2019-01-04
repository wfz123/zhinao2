/**
 * Created by dell on 2018/9/21.
 */
function VpDatePicker(option) {
    var id=option.id;
    var container;
    var input;
    var _selectMonthData=[[{text:"一月",value:0},{text:"二月",value:1},{text:"三月",value:2},{text:"四月",value:3}], [{text:"五月",value:4},{text:"六月",value:5},{text:"七月",value:6},{text:"八月",value:7}],
        [{text:"九月",value:8},{text:"十月",value:9},{text:"十一月",value:10},{text:"十二月",value:11}]];
    var _selectYearObj=null;
    var _type=1;
    var _year;
    var _month;
    var _day;
    var _date;
    var _selectObj=null;
    var _dateObj;
    init();
    function init() {
        var containerHtml='<div class="vp-date-picker-container"></div>';
        var target = $('#' + id);
        target.empty();
        target.attr('state','close');
        container=target.append(containerHtml).children().eq(0);
        if(option.selectDate!=undefined){
            if(isDate(option.selectDate)){
                var myDate=new Date(option.selectDate);
                _year=myDate.getFullYear();
                _month=myDate.getMonth();
                _selectObj={year:_year,month:_month,date:myDate.getDate()};
                target.attr("val",option.selectDate);
            }
        }
        else{
            var dateObj=getCurrentDate()
            _year=dateObj.year;
            _month=dateObj.month;
            _date=dateObj.date;
            _day=dateObj.day;
            _dateObj=dateObj;
        }

        initHeader();
        initBody();

    }
    function initHeader() {
        var that=this;
        var html='<div class="vp-date-picker-header"><div class="vp-input-container vp-input-prefix">' +
            '<input class="vp-input-content" /><span class="vp-input-prefix-container"><i class="vp-input-icon iconfont icon-rili"></i> </span>' +
            '<span class="vp-input-suffix-container"><i class="vp-input-icon iconfont icon-shanchu2"></i></span> </div> </div>';
        container.append(html);
        input=$('#'+id+' .vp-input-content');
        $('#'+id+' .vp-input-content').unbind("click").bind("click",onOpen)
        //$('#'+id+' .vp-input-content').bind("blur",onClose)
        $('#'+id+' .vp-input-suffix-container').unbind("click").bind("click",{that:that},onClear)
        if(_selectObj!=null){
            input.val(option.selectDate);
        }
    }
    function initBody() {
        var that=this;
        var body=$('#'+id+' .vp-date-picker-body');
        if(body.length>0){

            /*var testes=$('#'+id+' .vp-date-picker-header-text1>span');
            testes.eq(0).html(this._year+'年');
            testes.eq(1).html((this._month+1)+'月');*/
            $('#'+id+' .vp-date-picker-select-year').text(_year+'年');
            $('#'+id+' .vp-date-picker-select-month').text((_month+1)+'月');
        }
        else{
            var html='<div class="vp-date-picker-body">' +
                '<div class="vp-date-picker-wrap-header"><div class="vp-date-picker-header-container">' +
                '<span class="vp-date-picker-header-year-btn left iconfont icon-shuangxianyoujiantou"></span>' +
                '<span class="vp-date-picker-header-month-btn iconfont icon-arrow-left"></span><span class="vp-date-picker-header-text">' +
                '<span class="vp-date-picker-header-text1"><span class="vp-date-picker-select-year">'+_year+'年</span><span class="vp-date-picker-select-month">'+(_month+1)+'月</span></span>' +
                '<span class="vp-date-picker-header-text2"></span></span> '+
                '<span class="vp-date-picker-header-month-btn iconfont icon-right"></span> <span class="vp-date-picker-header-year-btn right iconfont icon-shuangxianzuojiantou"></span> </div> </div>' +
                '<div class="vp-date-picker-warp-content"><div class="vp-date-picker-body-container"><div class="vp-date-picker-table-container"><table class="vp-date-picker-table1"></table>' +
                '<table class="vp-date-picker-table2 selectYear"></table></div> </div> </div> ' +
                '</div>';
            container.append(html);
            console.info("that=",that);
            $('#'+id+' .vp-date-picker-header-month-btn').off("click").on("click",{that:that}, onChangeMonth);
            $('#'+id+' .vp-date-picker-header-year-btn').off("click").on("click",{that:that}, onChangeYear);
            $('#'+id+' .vp-date-picker-header-text').off("click").on("click",{that:that},onSelectYearAndMonth)
        }

        initTable();
    }
    function initTable() {
        var that=this;
        var target=$('#'+id+' .vp-date-picker-table-container>.vp-date-picker-table1');
        var target2=$('#'+id+' .vp-date-picker-table-container>.vp-date-picker-table2');
        if(target.children().length>0)
            target.empty();
        var weekHtml='<thead><tr><th>日</th><th>一</th><th>二</th><th>三</th><th>四</th><th>五</th><th>六</th></tr></thead>';
        console.info("selectObj=",_selectObj);
        var dateArr=getDateTableData(_year,_month,_dateObj,_selectObj,null);
        var bodyHtml='<tbody cellspacing="0" cellpadding="0">';
        for(var i=0;i<dateArr.length;i++){
            bodyHtml+='<tr>';
            for(var j=0;j<dateArr[i].length;j++){

                var tdHtml='';
                var tdClass=''
                if(dateArr[i][j].currentMonth){
                    tdClass='current';
                }
                if(dateArr[i][j].currentDate){
                    tdClass+=' active';
                }
                if(dateArr[i][j].select){
                    tdClass+=' select';
                }
                tdHtml='<td month="'+dateArr[i][j].month+'" year="'+dateArr[i][j].year+'" class="'+ tdClass+'">'+dateArr[i][j].date+'</td>';
                bodyHtml+=tdHtml;
            }
            bodyHtml+='</tr>';
        }
        bodyHtml+='</tbody>'
        //console.info("dateArr=",dateArr);
        target.append(weekHtml);
        target.append(bodyHtml);
        target.off("click").on("click",{that:that},onSelectDate);
        target2.off("click").on("click",{that:that},onSelectYear)
    }
    /*事件*/
    function onOpen(e) {
        var domContainer=$('#'+id);
        var target= $('#'+id+' .vp-date-picker-body');
        if(domContainer.attr('state') == 'close'){
            target.css({'display': 'block'});
            setTimeout(function () {
                target.css({'opacity': 1, 'transform-origin': 'center top 0px', 'transform': 'scaleY(1)'})
                domContainer.attr('state', 'open')
            }, 100)
        }
        else{
            target.css({'opacity': 0, 'transform-origin': 'center top 0px', 'transform': 'scaleY(0)'})
            setTimeout(function () {
                target.css({'display': 'none'})
                domContainer.attr('state', 'close')
            }, 300)
        }
    }
    function onClose(e) {
        /*var domContainer=$('#'+id);
        var target= $('#'+id+' .vp-date-picker-body');
        if(domContainer.attr('state') == 'open'){
            target.css({'opacity': 0, 'transform-origin': 'center top 0px', 'transform': 'scaleY(0)'})
            setTimeout(function () {
                target.css({'display': 'none'})
                domContainer.attr('state', 'close')
            }, 300)
        }*/
    }
    function onClear(e) {
        var that=e.data.that;
        $('#'+id+' .vp-input-content').val('');
        $('#'+id).attr("val","");
        vpEventTarget.fire({type:'change',id:id,source:'timer',data:''});
        var dateObj=getCurrentDate()
        _year=dateObj.year;
        _month=dateObj.month;
        _date=dateObj.date;
        _day=dateObj.day;
        _dateObj=dateObj;
        _selectObj=null;
        initBody();
    }
    function onChangeMonth(e) {
        var target=$(e.currentTarget);
        //console.info("月份:",e.data.that._month);
        console.info("ggggggggggg=",e.data.that);
        var month=_month;
        var year=_year;
        if(target.hasClass("icon-arrow-left")){
            month=parseInt(month)-1;
            if(month<0){
                month=11;
                year=year-1;
            }
        }
        else{
            month=parseInt(month)+1;
            if(month>11){
                month=0;
                year=year+1;
            }
        }
        _year=year;
        _month=month;
        initBody()
    }
    function onChangeYear(e) {
        var target=$(e.currentTarget);
        var year=_year;
        var table1=$('#'+id+' .vp-date-picker-table1');
        if(_type==2){
            if(target.hasClass("left")){
                _selectYearObj.minYear=parseInt(_selectYearObj.minYear)-10;
                _selectYearObj.maxYear=parseInt(_selectYearObj.maxYear)-10;
            }
            else{
                _selectYearObj.minYear=parseInt(_selectYearObj.minYear)+10;
                _selectYearObj.maxYear=parseInt(_selectYearObj.maxYear)+10;
            }
            showSelectYearTable(_selectYearObj.minYear,_selectYearObj.maxYear)
        }
        else if(_type==3){
            if(target.hasClass("left")){
                year=year-1;
            }
            else{
                year=year+1;
            }
            _year=year;
            $('#'+id+' .vp-date-picker-header-text2').text(_year+'年');
        }
        else{
            if(target.hasClass("left")){
                year=year-1;
            }
            else{
                year=year+1;
            }
            _year=year;
            initBody();
        }
    }
    function onSelectYearAndMonth(e) {
        var target=$(e.target);
        var month=_month;
        var year=_year;
        $('#'+id+' .vp-date-picker-header-month-btn').hide();
        $('#'+id+' .vp-date-picker-header-text1').hide();
        $('#'+id+' .vp-date-picker-table1').hide();
        $('#'+id+' .vp-date-picker-table2').show();
        if(target.hasClass("vp-date-picker-select-year")){
            _selectYearObj=getMaxAndMinYear(year);
            _type=2;
            showSelectYearTable();
        }
        else{
            _type=3;
            showSelectYearTable();
        }
    }
    function onSelectYear(e) {
        var target=$(e.target);
        if(_type==2){
            var year=target.text();
            _year=year
            _type=3;
            showSelectYearTable();
        }
        else if(_type==3){
            _month=parseInt(target.attr("val"));
            _type=1;
            $('#'+id+' .vp-date-picker-header-month-btn').show();
            $('#'+id+' .vp-date-picker-header-text1').show();
            $('#'+id+' .vp-date-picker-table1').show();
            $('#'+id+' .vp-date-picker-table2').hide();
            $('#'+id+' .vp-date-picker-header-text2').hide();
            initBody();
        }
    }
    function showSelectYearTable() {
        var target=$('#'+id+' .vp-date-picker-table2');
        var html='<tbody>';
        if(_type==2){
            var selectYearText=_selectYearObj.minYear+"年 - "+_selectYearObj.maxYear+"年";
            $('#'+id+' .vp-date-picker-header-text2').show().text(selectYearText);
            var dataArr=getSelectYearData(_selectYearObj.minYear,_selectYearObj.maxYear);
            console.info("aaaaaaaaa=",dataArr);
            for(var i=0;i<dataArr.length;i++){
                html+='<tr>';
                for(var j=0;j<dataArr[i].length;j++){
                    html+='<td>'+dataArr[i][j]+'</td>';
                }
                html+='</tr>';
            }
            html+='</tbody>';

        }
        else if(_type==3){
            var selectText=_year+'年';
            $('#'+id+' .vp-date-picker-header-text2').show().text(selectText);
            for(var i=0;i<_selectMonthData.length;i++){
                html+='<tr>';
                for(var j=0;j<_selectMonthData[i].length;j++){
                    html+='<td val="'+_selectMonthData[i][j].value+'">'+_selectMonthData[i][j].text+'</td>';
                }
                html+='</tr>';
            }
            html+='</tbody>';
        }
        target.empty();
        target.append(html);
    }
    function onSelectDate(e) {
        var target=$(e.target);
        var body= $('#'+id+' .vp-date-picker-body');
        var that=e.data.that;
        var domContainer=$('#'+id);
        if(e.target.tagName=="TD"){
           var year=target.attr("year");
           var month=target.attr("month");
            _year=parseInt(year);
            _month=parseInt(month);
            _selectObj={year:_year,month:_month,date:parseInt(e.target.innerText)}
            initBody();
            body.css({'opacity': 0, 'transform-origin': 'center top 0px', 'transform': 'scaleY(0)'})
            setTimeout(function () {
                body.css({'display': 'none'});
                domContainer.attr('state', 'close');
                var myDate=new Date(_year,_month,parseInt(e.target.innerText));
                var dateStr=formatDate(myDate,'yyyy-mm-dd');
                domContainer.attr('val',dateStr);
                input.val(dateStr);
                vpEventTarget.fire({type:'change',id:id,source:'timer',data:dateStr});
            }, 300)
        }
    }

    function getCurrentDate() {
        var date=new Date();
        var obj=new Object();
        obj.year=date.getFullYear();
        obj.month=date.getMonth();
        obj.day=date.getDay();
        obj.date=date.getDate();
        return obj;
    }
    function getDateTableData(year,month,dateObj,selectDateObj,disabledDate) {
        var firstDay=getMonthFirstDay(year,month);
        var prevMonth=month-1;
        var prevMonthYear=year;
        if(prevMonth<0){
            prevMonth=11;
            prevMonthYear=year-1;
        }
        var nextMonth=month+1;
        var nextMonthYear=year;
        if(nextMonth>11){
            nextMonth=0;
            nextMonthYear=year+1;
        }
        var prevMonthDate=getMonthDateNumber(prevMonthYear,prevMonth);
        var currentMonthDate=getMonthDateNumber(year,month);
        var startDate=0;
        if(firstDay==0){
            startDate=prevMonthDate-6;
        }
        else{
            startDate=prevMonthDate-(firstDay-1);
        }
        var dateArr=new Array;
        var currentMonth=0;
        for(var i=0;i<6;i++){
            var rowArr=new Array();
            for(var j=0;j<7;j++){
                var obj=new Object();
                obj.date=startDate;
                obj.currentDate=false;
                obj.currentMonth=false;
                obj.select=false;
                rowArr.push(obj);
                if(currentMonth==0){
                    obj.month=prevMonth;
                    obj.year=prevMonthYear;
                }
                else if(currentMonth==1){
                    obj.month=month;
                    obj.year=year;
                    obj.currentMonth=true;
                }
                else if(currentMonth==2){
                    obj.month=nextMonth;
                    obj.year=nextMonthYear;
                }

                if( dateObj!=undefined && startDate==dateObj.date && year==dateObj.year && month==dateObj.month){
                    obj.currentDate=true;
                }
                if(selectDateObj!=null && startDate==selectDateObj.date && year==selectDateObj.year && month==selectDateObj.month && currentMonth==1){
                    obj.select=true;
                }
                if(disabledDate!=null && isFunction(disabledDate)){
                    obj.disable=disabledDate(new Date(obj.year,obj.month,obj.date));
                }
                startDate++;
                if(startDate>prevMonthDate && currentMonth==0)
                {
                    startDate=1;
                    currentMonth=1;
                }
                if(startDate>currentMonthDate && currentMonth==1){
                    startDate=1;
                    currentMonth=2;
                }
            }
            dateArr.push(rowArr);
        }
        return dateArr;
    }
    function getMonthFirstDay(year,month) {
        var date=new Date(year,month,1);
        var day=date.getDay();
        return day;
    }
    function getMonthDateNumber(year,month) {
        var num=31;
        if (month === 3 || month === 5 || month === 8 || month === 10) {
            num=30;
        }
        else if (month === 1) {
            if (year % 4 === 0 && year % 100 !== 0 || year % 400 === 0) {
                num=29;
            } else {
                num=28;
            }
        }
        return num;
    }
    function formatDate(date,format){
        var v = "";
        if (typeof date == "string" || typeof date != "object" || date=="Invalid Date") {

        }
        else{
            var year  = date.getFullYear();
            var month  = date.getMonth()+1;
            var day   = date.getDate();
            var hour  = date.getHours();
            var minute = date.getMinutes();
            var second = date.getSeconds();
            var weekDay = date.getDay();
            var ms   = date.getMilliseconds();
            var weekDayString = "";

            if (weekDay == 1) {
                weekDayString = "星期一";
            } else if (weekDay == 2) {
                weekDayString = "星期二";
            } else if (weekDay == 3) {
                weekDayString = "星期三";
            } else if (weekDay == 4) {
                weekDayString = "星期四";
            } else if (weekDay == 5) {
                weekDayString = "星期五";
            } else if (weekDay == 6) {
                weekDayString = "星期六";
            } else if (weekDay == 7) {
                weekDayString = "星期日";
            }

            v = format;
            //Year
            v = v.replace(/yyyy/g, year);
            v = v.replace(/YYYY/g, year);
            v = v.replace(/yy/g, (year+"").substring(2,4));
            v = v.replace(/YY/g, (year+"").substring(2,4));

            //Month
            var monthStr = ("0"+month);
            var monthStr2=String(month);
            v = v.replace(/MM/g, monthStr.substring(monthStr.length-2));
            v = v.replace(/mm/g, monthStr.substring(monthStr.length-2));
            v=v.replace(/m/g,monthStr2);
            v=v.replace(/M/g,monthStr2);

            //Day
            var dayStr = ("0"+day);
            var dayStr2=String(day);
            v = v.replace(/DD/g, dayStr.substring(dayStr.length-2));
            v = v.replace(/dd/g, dayStr.substring(dayStr.length-2));
            v=v.replace(/d/g,dayStr2);
            v=v.replace(/D/g,dayStr2);

            //hour
            var hourStr = ("0"+hour);
            v = v.replace(/HH/g, hourStr.substring(hourStr.length-2));
            v = v.replace(/hh/g, hourStr.substring(hourStr.length-2));

            //minute
            var minuteStr = ("0"+minute);
            v = v.replace(/mm/g, minuteStr.substring(minuteStr.length-2));

            //Millisecond
            v = v.replace(/sss/g, ms);
            v = v.replace(/SSS/g, ms);

            //second
            var secondStr = ("0"+second);
            v = v.replace(/ss/g, secondStr.substring(secondStr.length-2));
            v = v.replace(/SS/g, secondStr.substring(secondStr.length-2));

            //weekDay
            v = v.replace(/E/g, weekDayString);
        }
        return v;
    }
    function getMaxAndMinYear(year) {
        year=String(year);
        var minYear='0';
        var maxYear='9';
        if(year.length>2){
            minYear=year.replace(/[0-9]$/,'0');
            maxYear=year.replace(/[0-9]$/,'9');
        }
        return {minYear:minYear,maxYear:maxYear};
    }
    function getSelectYearData(minYear,maxYear) {
        var startYear=Number(minYear);
        var endYear=Number(maxYear)
        var array=new Array();
        for(var i=0;i<3;i++){
            var newArray=new Array;
            array.push(newArray);
            for(var j=0;j<4;j++){
                newArray.push(startYear);
                startYear++;
                if(startYear>endYear)
                    break;
            }
        }
        return array;
    }
    function isDate(date) {
        if (date === null || date === undefined) return false;
        if (isNaN(new Date(date).getTime())) return false;
        return true;
    }
}
window.VpDatePicker=VpDatePicker;