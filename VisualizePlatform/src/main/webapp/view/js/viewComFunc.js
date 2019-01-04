/* 标签组 */
function vpCreateTags (getData, pair, domId, str) {
  console.log('显示', getData, pair, domId, str)
  var target = $('#' + domId)
  var dataArr = []
  getData.forEach(item => {
    var obj = {}
    pair.tagsData.forEach((e, i) => {
      obj[e] = item[pair.realData[i]]
    })
    dataArr.push(obj)
  })
  var param = {
    title: str,
    arr: []
  }
  var html = '<div class=\'vp-tags\'><label>' + str + '：</label>'
  dataArr.forEach(item => {
    html += '<span data-value=\'' + item.value + '\'>' + item.label + '</span>'
  })
  html += '</div>'
  target.append(html)
  target.attr('val', JSON.stringify(param))

  target.on('click', 'span', function () {
    $(this).toggleClass('active')
    var tartetVal = $(this).attr('data-value')
    var obj = JSON.parse(target.attr('val'))
    console.log(obj)
    var arr = obj.arr
    if (arr.includes(tartetVal)) {
      arr.splice(arr.indexOf(tartetVal), 1)
    } else {
      arr.push(tartetVal)
    }
    target.attr('val', JSON.stringify(obj))
  })
}
/* 地图 */
function vpMapInitFunc (domId, option) {
  window.myMaps[domId] = new window.IcJsMap()
  window.myMaps[domId].init(domId, option)
}
function vpMapShowFunc (getData, pair, domId, overlayType) {
  if (!getData) {
    window.myMaps[domId].$_clearAllOverlays()
    alert('没有符合条件的查询结果！')
    return
  }
  console.log('显示', getData, pair, domId, overlayType)
  var data = {
    overlayType: overlayType,
    data: []
  }
  if (overlayType === 'heatMap') {
    data.max = 100
  }
  getData.forEach(item => {
    var obj = {}
    pair.mapData.forEach((e, i) => {
      var val = item[pair.realData[i]]
      if (e === 'lng' || e === 'lat') {
        obj[e] = parseFloat(val)
      } else {
        obj[e] = val
      }
    })
    if (overlayType === 'heatMap') {
      obj.count = item.count
    }
    if (overlayType === 'clusterLabel') {
      obj.text = obj.title + '：' + item.count
    }
    data.data.push(obj)
  })
  if (overlayType === 'clusterLabel') {
    data.overlayType = 'label'
  }
  console.log('data=', data)
  window.myMaps[domId].$_clearAllOverlays()
  window.myMaps[domId].$_showOverlays(data, true)
}
/* 创建列表 */
function pvCreatList (domId, dataArr) {
  var target = $('#' + domId)
  var html = '<ul class="vp-list">'
  dataArr.forEach(item => {
    html += '<li><label>' + item.title + '</label>' + item.content + '</li>'
  })
  html += '</ul>'
  target.append(html)
}
/* url取值 */
function vpGetQueryString (name) {
  var href = window.location.href
  var search = href.slice(href.indexOf('?'), href.length)
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)')
  var r = search.substr(1).match(reg)
  if (r != null) {
    return decodeURIComponent(r[2])
  } else {
    return null
  }
}
/* 创建单选按钮 */
function vpCreateRadio (domId, dataArr) {
  var target = $('#' + domId)
  var html = ''
  var selectVal = ''
  for (var i = 0; i < dataArr.length; i++) {
    var select = ''
    if (dataArr[i].selected == true) {
      select = ' select'
      selectVal = dataArr[i].value
    }
    var radioHtml = '<span val="' + dataArr[i].value + '" class="vp-radio-item' + select + '"><span class="vp-radio-icon"><span class="vp-radio-fill"></span> </span><span class="vp-radio-label">' + dataArr[i].label + '</span></span>'
    html += radioHtml
  }
  target.empty()
  target.append(html)
  target.attr('val', selectVal)
  var selector = '#' + domId + ' .vp-radio-item'
  $(selector).bind('click', function (e) {
    var val = $(this).attr('val')
    target.attr('val', val)
    $(selector).removeClass('select')
    $(this).addClass('select')
    vpEventTarget.fire({type: 'change', id: domId, source: 'raidio', data: val})
  })
}
/* 创建多选按钮 */
function vpCreateCheckBox (domId, dataArr) {
  var target = $('#' + domId)
  var html = ''
  var selectVal = ''
  var valArr = []
  for (var i = 0; i < dataArr.length; i++) {
    var select = ''
    if (dataArr[i].selected == true) {
      select = ' select'
      valArr.push(dataArr[i].value)
      if (selectVal == '') {
        selectVal += dataArr[i].value
      } else {
        selectVal += ',' + dataArr[i].value
      }
    }
    var boxHtml = '<span val="' + dataArr[i].value + '" class="vp-checkbox-item' + select + '"><span class="vp-checkbox-icon"><span class="vp-checkbox-fill"></span> </span><span class="vp-checkbox-label">' + dataArr[i].label + '</span></span>'
    html += boxHtml
  }
  target.empty()
  target.append(html)
  target.attr('val', selectVal)
  var selector = '#' + domId + ' .vp-checkbox-item'
  $(selector).bind('click', function (e) {
    var val = $(this).attr('val')
    valArr = target.attr('val').split(',')
    if ($(this).hasClass('select')) {
      $(this).removeClass('select')
      for (var j = 0; j < valArr.length; j++) {
        if (valArr[j] == val) {
          valArr.splice(j, 1)
          break
        }
      }
    } else {
      $(this).addClass('select')
      valArr.push(val)
    }
    var selectStr = ''
    for (var k = 0; k < valArr.length; k++) {
      if (selectStr == '') {
        selectStr += valArr[k]
      } else {
        selectStr += ',' + valArr[k]
      }
    }
    target.attr('val', selectStr)
    vpEventTarget.fire({type: 'change', id: domId, source: 'checkbox', data: selectStr})
  })
}
/* 创建下拉菜单 */
function vpCreateSelect (domId, dataArr) {
  var target = $('#' + domId)
  var html = ''
  var headerHtml = '<div class="vp-select-container"><div class="vp-select-header"><input placeholder="" autocomplete="off" readonly="readonly" class="vp-select-input"><span class="vp-select-image">' +
        '<i class="vp-select-icon iconfont icon-triangle-bottom"></i> </span></div>'
  var bodyHtml = '<div class="vp-select-body"><div class="vp-select-body-content"> '
  var selectObj = undefined
  for (var i = 0; i < dataArr.length; i++) {
    var htmlVal = dataArr[i].value
    if (dataArr[i].selected == true) {
      selectObj = {index: i, val: dataArr[i].value, label: dataArr[i].label}
    }
    var itemHtml = '<div class="vp-select-item" val="' + dataArr[i].value + '">' + dataArr[i].label + '</div>'
    bodyHtml += itemHtml
  }
  bodyHtml += '</div>'
  html = headerHtml + bodyHtml + '</div>'
  target.append(html)
  if (selectObj != undefined) {
    target.find('.vp-select-item').eq(selectObj.index).addClass('select')
    target.attr('val', selectObj.val)
    target.find('.vp-select-input').eq(0).val(selectObj.label)
  }
  target.attr('state', 'close')
  var bodyTarget = target.find('.vp-select-body').eq(0)
  target.find('.vp-select-header').bind('click', function (e) {
    if (target.attr('state') == 'close') {
      bodyTarget.css({'display': 'block'})
      setTimeout(function () {
        bodyTarget.css({'opacity': 1, 'transform-origin': 'center top 0px', 'transform': 'scaleY(1)'})
      }, 100)
      target.attr('state', 'open')
    } else {
      bodyTarget.css({'opacity': 0, 'transform-origin': 'center top 0px', 'transform': 'scaleY(0)'})
      setTimeout(function () {
        bodyTarget.css({'display': 'none'})
        target.attr('state', 'close')
      }, 300)
    }
  })
  var items = bodyTarget.find('.vp-select-item')
  items.bind('click', function (e) {
    items.removeClass('select')
    $(this).addClass('select')
    target.attr('val', $(this).attr('val'))
    target.find('.vp-select-input').eq(0).val($(this).text())
    bodyTarget.css({'opacity': 0, 'transform-origin': 'center top 0px', 'transform': 'scaleY(0)'})
    vpEventTarget.fire({type: 'change', id: domId, source: 'select', data: $(this).attr('val')})
    setTimeout(function () {
      bodyTarget.css({'display': 'none'})
      target.attr('state', 'close')
    }, 300)
  })
}
/* 创建表格 */
function pvCreatTable (domId, tableObj, selectType, mulSelect) {
  var target = $('#' + domId)
  // console.info('target=', target)
  var tableStyle = {}
  if (target.attr('instyle') != 'undefined' && target.attr('instyle') != undefined) {
    tableStyle = JSON.parse(target.attr('instyle'))
  }
  // console.info('yyyyy=', tableStyle)
  var html = '<table><thead><tr>'
  // operateArr=[{className:'icon-kejian',eventType:'detail'},{className:'icon-shanchu',eventType:'delete'},{className:'icon-shezhi1',eventType:'set'}];
  if (selectType == true) { tableObj.th.push('选中') }
  tableObj.th.forEach(e => {
    var styleStr = 'background-color:' + tableStyle.thbg + ';color:' + tableStyle.thcolor + ';font-size:' + tableStyle.thsize + 'px;'
    html += '<th style="' + styleStr + '">' + e + '</th>'
  })
  html += '</tr></thead>'
  if (tableObj.td) {
    for (var i = 0; i < tableObj.td.length; i++) {
      html += '<tr data=\'' + JSON.stringify(tableObj.td[i]) + '\'>'
      // var attr=
      for (var j = 0; j < tableObj.param.length; j++) {
        if (i % 2 == 0) {
          var oddStyeStr = 'background-color:' + tableStyle.tdoddbg + ';color:' + tableStyle.tdoddcolor + ';font-size:' + tableStyle.tdoddsize + 'px;'
          // console.info('ttttttttttttt=', tableObj.td[i][tableObj.param[j]])
          html += '<td style="' + oddStyeStr + '">' + tableObj.td[i][tableObj.param[j]] + '</td>'
        } else {
          var evenStyeStr = 'background-color:' + tableStyle.tdevenbg + ';color:' + tableStyle.tdevencolor + ';font-size:' + tableStyle.tdevensize + 'px;'
          // console.info('ttttttttttttt=', tableObj.td[i][tableObj.param[j]])
          html += '<td style="' + evenStyeStr + '">' + tableObj.td[i][tableObj.param[j]] + '</td>'
        }
      }
      if (selectType == true && !mulSelect) {
        if (i % 2 == 0) { html += '<td style="' + oddStyeStr + '"><span class="vp-radio-icon"><span class="vp-radio-fill"></span> </span> </td>' } else { html += '<td style="' + evenStyeStr + '"><span class="vp-radio-icon"><span class="vp-radio-fill"></span> </span> </td>' }
      } else if (selectType == true && mulSelect) {
        if (i % 2 == 0) { html += '<td style="' + oddStyeStr + '"><span class="ic-checkbox-icon"><span class="ic-checkbox-fill"></span> </span> </td>' } else { html += '<td style="' + evenStyeStr + '"><span class="ic-checkbox-icon"><span class="ic-checkbox-fill"></span> </span> </td>' }
      }
    }

    html += '</tr>'
  }
  html += '</table>'
  if (target.children().length > 0) {
    target.empty()
  }
  target.append(html)
  if (selectType == true) {
    target.on('click', 'tbody tr', function (e) {
      var val = $(e.currentTarget).attr('data')
      if (!mulSelect) {
        target.find('tbody tr').removeClass('select')
        $(e.currentTarget).addClass('select')
        val = $(e.currentTarget).attr('data')
        target.attr('val', val)
      } else {
        if ($(e.currentTarget).hasClass('select')) {
          $(e.currentTarget).removeClass('select')
        } else {
          $(e.currentTarget).addClass('select')
        }
        var trs = target.find('tbody').find('tr')
        var dataArray = new Array()
        for (var i = 0; i < trs.length; i++) {
          if (trs.eq(i).hasClass('select')) {
            dataArray.push(trs.eq(i).attr('data'))
          }
        }
        val = dataArray
        target.attr('val', JSON.stringify(val))
      }
      vpEventTarget.fire({type: 'change', id: domId, source: 'table', data: val})
    })
  }
}
/* 创建日期组件 */
function vpCreateDatePicker (domId) {
  new VpDatePicker({id: domId})
}
/* 创建tree组件 */
function vpCreateTree (domId, data) {
  if (data != undefined) { new VpTree({id: domId, data: data}) }
}
function vpGetTableData (tableData, value) {
  var returnValue
  if (value != undefined) { value = value.replace(/^\./, '') }
  console.info('dddddddddd=', value)
  if (tableData != undefined) {
    if (tableData.constructor === Array) {
      returnValue = new Array()
      for (var i = 0; i < tableData.length; i++) {
        returnValue.push(JSON.parse(tableData[i])[value])
      }
      // returnValue = returnValue)
    } else {
      returnValue = tableData[value]
    }
  }
  return returnValue
}
/* 创建表单组件 */
function vpCreateForm (idArr, typeArr, fieldArr, data) {

  // console.info('type=', typeArr)
  // console.info('data=', data)
    var newData;
    if(Array.isArray(data))
      newData=data;
    else
    {
      newData=new Array();
      newData.push(data);
    }
  for (var i = 0; i < idArr.length; i++) {
    var type = typeArr[i]
    var id = idArr[i]
    var field = fieldArr[i]
    var dataValue = newData[0][field]
    if (type == 'inputtext')// input组件
    {
      $('#' + id).val(dataValue)
    } else if (type == 'select')// 下拉菜单组件
    {
      var selectContainer = $('#' + id)
      var selectLabel = ''
      var selectIndex
      var selectItem = selectContainer.find('.vp-select-item')
      selectItem.each(function (index, item) {
        if (dataValue == item.getAttribute('val')) {
          selectLabel = item.innerText
          selectIndex = index
        }
      })
      if (selectIndex != undefined) {
        selectItem.removeClass('select')
        selectItem.eq(selectIndex).addClass('select')
        selectContainer.find('.vp-select-input').val(selectLabel)
      }
    } else if (type == 'checkbox')// 多选组件
    {
      var checkboxContainer = $('#' + id)
      var dataVlaueArr = dataValue.split(',')
      var checkboxItem = checkboxContainer.find('.vp-checkbox-item')
      checkboxItem.each(function (index, item) {
        var jqItem = $(item)
        jqItem.removeClass('select')
        for (var i = 0; i < dataVlaueArr.length; i++) {
          if (dataVlaueArr[i] == item.getAttribute('val')) {
            jqItem.addClass('select')
          }
        }
      })
      checkboxContainer.attr('val', dataValue)
    } else if (type == 'radio')// 单选组件
    {
      var radioContainer = $('#' + id)
      var radioItem = radioContainer.find('.vp-radio-item')
      radioItem.removeClass('select')
      radioItem.each(function (index, item) {
        if (dataValue == item.getAttribute('val')) {
          $(item).addClass('select')
        }
      })
      radioContainer.attr('val', dataValue)
    } else if (type == 'timer')// 日期组件
    {
      new VpDatePicker({id: id, selectDate: dataValue})
    }
  }
}
/* 创建分页器 */
function vpCreatePagination (domId, totalPage, currentPage, pageSize) {
  /* var target = $('#' + domId)
  var html = '<div class="vp-pagination-pre"><span class="iconfont icon-arrow-left"></span><span>上一页</span></div> <div class="vp-pagination-next"><span>下一页</span><span class="iconfont icon-right"></span> </div>'
  target.empty()
  target.append(html) */
  var target = $('#' + domId)
  if (target.children().length == 0) {
    var html = '<div class="vp-pagination-pre iconfont icon-arrow-left disable"></div><div class="vp-pagination-page"></div><div class="vp-pagination-next iconfont icon-right"></div> '
    target.append(html)
  } else {
    target.find('.vp-pagination-item').unbind('click')
    target.find('.vp-pagination-page').empty()
  }
  var pages = Math.ceil(totalPage / pageSize)
  currentPage = currentPage - 1
  if (currentPage == undefined) { currentPage = 0 }
  var pageList = createPageList()
  for (var i = 0; i < pageList.length; i++) {
    var itemClass = 'vp-pagination-item'
    if (pageList[i].active) { itemClass += ' active' }
    var pageItem = '<div class="' + itemClass + '">' + pageList[i].name + '</div>'
    target.find('.vp-pagination-page').append(pageItem)
  }
  if(target.attr("instyle")){
      var instyle=JSON.parse(target.attr("instyle"));
      var btnCss={"backgroundColor":instyle.pagebg,"font-size":instyle.pagesize+"px","color":instyle.pagecolor};
      var activeBtnCss={"backgroundColor":instyle.pagedbg,"font-size":instyle.pagesize+"px","color":instyle.pagedcolor};
      target.find(".vp-pagination-pre").css(btnCss);
      target.find(".vp-pagination-next").css(btnCss);
      target.find(".vp-pagination-item").css(btnCss);
      target.find(".vp-pagination-item.active").css(activeBtnCss);
  }
  target.find('.vp-pagination-pre').unbind('click').bind('click', function (e) {
    if (currentPage > 0) {
      currentPage--
      if (currentPage == 0) {
        target.find('.vp-pagination-pre').addClass('disable')
      }
      target.find('.vp-pagination-next').removeClass('disable')
      target.find('.vp-pagination-item.active').removeClass('active');
      target.find('.vp-pagination-item').eq(currentPage).addClass('active');
      target.attr('val', currentPage + 1)
      vpEventTarget.fire({type: 'change', id: domId, source: 'pagination', data: currentPage + 1})
    }
  })
  target.find('.vp-pagination-next').unbind('click').bind('click', function (e) {
    if (currentPage < pages - 1) {
      currentPage++
      if (currentPage == pages - 1) {
        target.find('.vp-pagination-next').addClass('disable')
      }
      target.find('.vp-pagination-pre').removeClass('disable')
      target.find('.vp-pagination-item.active').removeClass('active');
      target.find('.vp-pagination-item').eq(currentPage).addClass('active');
      target.attr('val', currentPage + 1)
      vpEventTarget.fire({type: 'change', id: domId, source: 'pagination', data: currentPage + 1})
    }
  })
  target.find('.vp-pagination-item').bind('click', function (e) {
    var vpItem = e.target
    var vpNum = parseInt(vpItem.innerText)
    if (vpNum - 1 != currentPage) {
      target.find('.vp-pagination-item.active').removeClass('active');
      $(vpItem).addClass('active');
      currentPage = vpNum - 1
      target.attr('val', currentPage + 1)
      target.find('.vp-pagination-next').removeClass('disable')
      target.find('.vp-pagination-pre').removeClass('disable')
      if (currentPage == 0) {
        target.find('.vp-pagination-pre').addClass('disable')
      } else if (currentPage == pages - 1) {
        target.find('.vp-pagination-next').addClass('disable')
      }
      vpEventTarget.fire({type: 'change', id: domId, source: 'pagination', data: currentPage + 1})
    }
  })
  function createPageList () {
    var list = new Array()
    var maxPage = 5
    var minPage = currentPage - 3
    if (minPage < 0) { minPage = currentPage - 2 }
    if (minPage < 0) { minPage = currentPage - 1 }
    if (minPage < 0) { minPage = currentPage }
    if (pages < 5) {
      maxPage = pages
    } else {
      maxPage = minPage + 5
      if (maxPage > pages) {
        maxPage = pages
        minPage = pages - 5
      }
    }
    for (var i = minPage; i < maxPage; i++) {
      var obj = {name: i + 1, active: false}
      if (obj.name == currentPage + 1) { obj.active = true }
      list.push(obj)
    }
    return list
  }
}
/* 和后台通信get方法 */
function pvHttpGet (url, param, callback) {
  var xhr
  var paramStr = ''
  var tokenId = window.sessionStorage.getItem('token')
  for (var key in param) {
    if (paramStr.length == 0) { paramStr = key + '=' + param[key] } else { paramStr = paramStr + '&' + key + '=' + param[key] }
  }
  try {
    xhr = new XMLHttpRequest()
  } catch (e) {
    xhr = new ActiveXObject('Microsoft.XMLHTTP')
  }
  xhr.open('GET', url + '?' + paramStr, true)
  xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8')
  xhr.setRequestHeader('X-Auth-Token', tokenId)
  xhr.send()
  xhr.onreadystatechange = function () {
    if (xhr.readyState == 4 && xhr.status == 200) {
      callback(JSON.parse(xhr.responseText))
    }
  }
}
/* 和后台通信post方法 */
function pvHttpPost (url, param, callback) {
  var xhr
  var tokenId = window.sessionStorage.getItem('token')
  try {
    xhr = new XMLHttpRequest()
  } catch (e) {
    xhr = new ActiveXObject('Microsoft.XMLHTTP')
  }
  xhr.open('POST', url, true)
  xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded;charset=UTF-8')
  xhr.setRequestHeader('X-Auth-Token', tokenId)
  var valStr = ''
  for (var key in param) {
    if (valStr.length == 0) { valStr = key + '=' + param[key] } else {
      valStr = valStr + '&' + key + '=' + param[key]
    }
  }
  xhr.send(valStr)
  xhr.onreadystatechange = function () {
    if (xhr.readyState == 4 && xhr.status == 200) {
      callback(JSON.parse(xhr.responseText))
    }
  }
}
/* 创建图表方法 */
function pvCreatChart (id, type, dataZoomX, dataZoomY, data) {
  var option = null
  var container = document.getElementById(id)
  console.log(container)
  if (type === 'bar' || type === 'line') {
    option = {
      xAxis: {
        type: 'category',
        data: data.xAxisList
      },
      yAxis: {
        type: 'value'
      },
      tooltip: {
        show: true
      },
      series: [{
        type: type,
        data: data.yAxisList
      }]
    }
    if (dataZoomX) {
      if (!Array.isArray(option.dataZoom)) option.dataZoom = []
      option.dataZoom.push({
        type: 'slider',
        show: true,
        xAxisIndex: 0,
        height: 20
      })
    }
    if (dataZoomY) {
      if (!Array.isArray(option.dataZoom)) option.dataZoom = []
      option.dataZoom.push({
        type: 'slider',
        show: true,
        yAxisIndex: 0,
        width: 20
      })
    }
    // chart.setOption(option);
  } else if (type === 'pie') {
    var seriesData = []
    for (var i = 0; i < data.xAxisList.length; i++) {
      const element = data.xAxisList[i]
      seriesData.push({
        name: element,
        value: data.yAxisList[i]
      })
    }
    option = {
      legend: {
        data: data.xAxisList
      },
      tooltip: {
        show: true
      },
      series: [{
        type: type,
        radius: ['50%', '70%'],
        data: seriesData
      }]
    }
  } else if (type === 'graph') {
    option = {
      color: ['#EA8282', '#F2C551', '#80A9E1', '#F3A364'],
      tooltip: {
        show: true,
        triggerOn: 'click'
      },
      legend: {
        top: 0,
        left: 0
      },
      animation: false,
      series: [{
        type: 'graph',
        layout: 'force',
        symbolSize: 30,
        force: {
          repulsion: 500, // 斥力大小
          edgeLength: 150, // 节点连线长度
          // repulsion: 200,//斥力大小
          // edgeLength:80, //节点连线长度
          initLayout: 'circular'
        },
        label: {
          normal: {
            show: true,
            position: 'bottom',
            color: '#222222'
          }
        },
        lineStyle: {
          normal: {
            color: 'source'
          }
        },
        edgeLabel: { // 线上的 字
          normal: {
            show: true,
            color: '#999999',
            position: 'middle',
            formatter: function (e) {
              return e.data.relationType
            }
          }
        },
        data: data.nodes,
        links: data.links,
        roam: true
      }]
    }
  }
  var customClass = {
    width: container.offsetWidth > 10 ? container.offsetWidth + 'px' : '400px',
    height: container.offsetHeight > 10 ? container.offsetHeight + 'px' : '300px'
  }
  var chart = new Ischart(id, option, customClass)
  chart.on('click', function (e) {
     console.log('eeee1',e);
    var obj = {}
    obj.title = e.name
    obj.val = e.value
    container.setAttribute('val', JSON.stringify(obj))
    vpEventTarget.fire({type: 'change', id: id, source: e.componentSubType, data: obj})
  })
}
/*创建时间轴的方法*/
function vpCreateTimeline(data,tags,keys,domId) {
  var timeKey;
  var titleKey;
  var contentKey;
  var totalHtml='';
  var target = $('#' + domId)
  if(target.children().length>0){
    target.empty()
  }
  console.log('target===',target)
  for(var i=0;i<tags.length;i++){
    if(tags[i]=="time")
      timeKey=keys[i];
    else if(tags[i]=="title")
      titleKey=keys[i];
    else if(tags[i]=="content")
      contentKey=keys[i];
  }
  for(var j=0;j<data.length;j++){
      var timeVal=data[j][timeKey];
      var titleVal=data[j][titleKey];
      var contentVal=data[j][contentKey];
      var html='<div val='+j+' class="vp-timeline-conter"><div class="vp-timeline-icon"></div><span class="vp-timeline-time">'+timeVal+'</span><div class="vp-timeline-main"><span class="vp-timeline-span-jiao"></span><h3 class="vp-timeline-main-title">'+titleVal+'</h3><p class="vp-timeline-main-center">'+contentVal+'</p></div></div>';
      totalHtml+=html;
  }
target.append(totalHtml);
$('.vp-timeline-conter:even').addClass('vp-timeline-conter-left');
$('.vp-timeline-conter:odd').addClass('vp-timeline-conter-right')
var timeStyle = {}
if (target.attr('instyle') != 'undefined' && target.attr('instyle') != undefined) {
  timeStyle = JSON.parse(target.attr('instyle'));
  var timeBg={"backgroundColor":timeStyle.tlbg};//轴底色背景
  var leftBefore={"border-left-color":timeStyle.tlbg};
  var rightBefore={"border-right-color":timeStyle.tlbg};
  var timetitle={"font-size":timeStyle.tltsize,"color":timeStyle.tltcolor};//标题字体大小和颜色
  var timeCenter={"font-size":timeStyle.tlcsize,"color":timeStyle.tlccolor};//内容字体大小和颜色
  var timePoint={"background":timeStyle.tlzdbg};//轴点颜色
   target.find(".vp-timeline-main").css(timeBg);
   target.find(".vp-timeline-conter-left .vp-timeline-span-jiao").css(leftBefore);
   target.find(".vp-timeline-conter-right .vp-timeline-span-jiao").css(rightBefore);
   target.find(".vp-timeline-main-title").css(timetitle);
   target.find(".vp-timeline-main-center").css(timeCenter);
   target.find(".vp-timeline-icon").css(timePoint);
}
$(".vp-timeline-conter").on('click',function(e){
  var obj = data[parseInt($(e.currentTarget).attr("val"))];
  console.log('obj',obj)
  target.attr("val",JSON.stringify(obj));
  vpEventTarget.fire({type: 'change', id:domId, source: 'timeline', data: obj})
});


}
window.pvCreatTable = pvCreatTable
window.pvHttpGet = pvHttpGet
window.pvHttpPost = pvHttpPost
window.pvCreatList = pvCreatList
window.vpCreateRadio = vpCreateRadio
window.vpCreateCheckBox = vpCreateCheckBox
window.vpCreateSelect = vpCreateSelect
window.vpCreateDatePicker = vpCreateDatePicker
window.vpGetQueryString = vpGetQueryString
window.vpCreateTree = vpCreateTree
window.pvCreatChart = pvCreatChart
window.vpCreatePagination = vpCreatePagination
window.vpMapInitFunc = vpMapInitFunc
window.vpMapShowFunc = vpMapShowFunc
window.vpCreateForm = vpCreateForm
window.vpCreateTags = vpCreateTags
window.vpGetTableData = vpGetTableData
window.vpCreateTimeline=vpCreateTimeline
