// 引入 文件   修改
// 合并对象
const mergeObject = function (defaultObj, originalObj) {
  for (let d in defaultObj) {
    var value = defaultObj[d]
    if (originalObj[d] === undefined) {
      originalObj[d] = value
    }
  }
  return originalObj
}
const isString = function (val) {
  return Object.prototype.toString.call(val) === '[object String]'
}
// --------------------------------------------------------------style
let tooltipTextStyle = {
  color: '#fff'
}
// --------------------------------------------------------------defaultOption
let feature = {
  dataView: { // 数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
    show: true, // 是否显示该工具。
    title: '数据视图',
    readOnly: false, // 是否不可编辑（只读）
    lang: ['数据视图', '关闭', '刷新'], // 数据视图上有三个话术，默认是['数据视图', '关闭', '刷新']
    backgroundColor: '#fff', // 数据视图浮层背景色。
    textareaColor: '#fff', // 数据视图浮层文本输入区背景色
    textareaBorderColor: '#333', // 数据视图浮层文本输入区边框颜色
    textColor: '#000', // 文本颜色。
    buttonColor: '#c23531', // 按钮颜色。
    buttonTextColor: '#fff' // 按钮文本颜色。
  },
  magicType: { // 动态类型切换
    show: true,
    title: '切换', // 各个类型的标题文本，可以分别配置。
    type: ['line', 'bar'] // 启用的动态类型，包括'line'（切换为折线图）, 'bar'（切换为柱状图）, 'stack'（切换为堆叠模式）, 'tiled'（切换为平铺模式）
  },
  restore: { // 配置项还原。
    show: true, // 是否显示该工具。
    title: '还原'
  },
  saveAsImage: { // 保存为图片。
    show: true, // 是否显示该工具。
    type: 'png', // 保存的图片格式。支持 'png' 和 'jpeg'。
    name: 'pic1', // 保存的文件名称，默认使用 title.text 作为名称
    backgroundColor: '#ffffff', // 保存的图片背景色，默认使用 backgroundColor，如果backgroundColor不存在的话会取白色
    title: '保存为图片',
    pixelRatio: 1 // 保存图片的分辨率比例，默认跟容器相同大小，如果需要保存更高分辨率的，可以设置为大于 1 的值，例如 2
  },
  dataZoom: { // 数据区域缩放。目前只支持直角坐标系的缩放
    show: true, // 是否显示该工具。
    title: '缩放', // 缩放和还原的标题文本
    xAxisIndex: 0, // 指定哪些 xAxis 被控制。如果缺省则控制所有的x轴。如果设置为 false 则不控制任何x轴。如果设置成 3 则控制 axisIndex 为 3 的x轴。如果设置为 [0, 3] 则控制 axisIndex 为 0 和 3 的x轴
    yAxisIndex: false // 指定哪些 yAxis 被控制。如果缺省则控制所有的y轴。如果设置为 false 则不控制任何y轴。如果设置成 3 则控制 axisIndex 为 3 的y轴。如果设置为 [0, 3] 则控制 axisIndex 为 0 和 3 的y轴
  }
}
let defaultOption = {
  title: {
    show: false,
    text: '',
    subtext: '',
    textBaseline: 'center',
    top: '10',
    right: '',
    bottom: '',
    target: 'blank',
    subtarget: 'blank',
    textStyle: {},
    subtextStyle: {},
    left: 'left'
  },
  tooltip: {
    show: false,
    trigger: 'axis',
    showDelay: 0,
    hideDelay: 100, // 浮层隐藏的延迟，单位为 ms
    enterable: false, // 鼠标是否可进入提示框浮层中
    transitionDuration: 1, // 提示框浮层的移动动画过渡时间，单位是 s
    confine: true,
    formatter: '',
    backgroundColor: 'rgb(72, 84, 101,0.9)',
    borderColor: 'rgb(72, 84, 101,0.9)',
    borderWidth: 0,
    padding: 5,
    textStyle: tooltipTextStyle
  },
  legend: {
    show: true,
    left: 'center',
    top: 'top',
    itemWidth: 10,
    itemHeight: 10,
    selectedMode: false, // 是否单选
    formatter: function (name) { // 用来格式化图例文本，支持字符串模板和回调函数两种形式。模板变量为图例名称 {name}
      return name
    },
    // textStyle: mytextStyle, // 文本样式
    data: [] // series中根据名称区分
  },
  dataZoom: [{
    type: 'slider',
    show: true,
    xAxisIndex: [0],
    handleSize: 10, // 滑动条的 左右2个滑动条的大小
    height: 5, // 组件高度
    bottom: 10, // 右边的距离
    handleColor: '#F9CA77', // h滑动图标的颜色
    start: 0,
    end: 10,
    handleStyle: {
      borderColor: '#cacaca',
      borderWidth: '1',
      shadowBlur: 2,
      background: '#F9CA77',
      shadowColor: '#ddd'
    },
    fillerColor: {
      'x': 0,
      'y': 0,
      'x2': 0,
      'y2': 1,
      'type': 'linear',
      'global': false,
      'colorStops': [{
        'offset': 0,
        'color': '#F9CA77'
      }, {
        'offset': 0.5,
        'color': '#FB7F38'
      }, {
        'offset': 1,
        'color': '#FB7F38'
      }]
    },
    backgroundColor: '#ddd', // 两边未选中的滑动条区域的颜色
    showDataShadow: false, // 是否显示数据阴影 默认auto
    showDetail: true, // 即拖拽时候是否显示详细数值信息 默认true
    handleIcon: 'M-292,322.2c-3.2,0-6.4-0.6-9.3-1.9c-2.9-1.2-5.4-2.9-7.6-5.1s-3.9-4.8-5.1-7.6c-1.3-3-1.9-6.1-1.9-9.3c0-3.2,0.6-6.4,1.9-9.3c1.2-2.9,2.9-5.4,5.1-7.6s4.8-3.9,7.6-5.1c3-1.3,6.1-1.9,9.3-1.9c3.2,0,6.4,0.6,9.3,1.9c2.9,1.2,5.4,2.9,7.6,5.1s3.9,4.8,5.1,7.6c1.3,3,1.9,6.1,1.9,9.3c0,3.2-0.6,6.4-1.9,9.3c-1.2,2.9-2.9,5.4-5.1,7.6s-4.8,3.9-7.6,5.1C-285.6,321.5-288.8,322.2-292,322.2z',
    filterMode: 'filter'
  },
    // 下面这个属性是里面拖到
  {
    type: 'inside',
    show: true,
    xAxisIndex: [0]
    // start: 1,
    // end: 100
  }
  ],
  toolbox: {
    show: true, // 是否显示工具栏组件
    // orient: 'horizontal', // 工具栏 icon 的布局朝向'horizontal' 'vertical'
    itemSize: 15, // 工具栏 icon 的大小
    itemGap: 10, // 工具栏 icon 每项之间的间隔
    showTitle: true, // 是否在鼠标 hover 的时候显示每个工具 icon 的标题

    zlevel: 0, // 所属图形的Canvas分层，zlevel 大的 Canvas 会放在 zlevel 小的 Canvas 的上面
    z: 2, // 所属组件的z分层，z值小的图形会被z值大的图形覆盖
    left: 'right', // 组件离容器左侧的距离,'left', 'center', 'right','20%'
    top: 'top', // 组件离容器上侧的距离,'top', 'middle', 'bottom','20%'
    right: 'auto', // 组件离容器右侧的距离,'20%'
    bottom: 'auto', // 组件离容器下侧的距离,'20%'
    width: 'auto', // 图例宽度
    height: 'auto' // 图例高度
  },
  radar: {
    shape: 'polygon',
    name: {
      color: '#000',
      fontSize: '12',
      fontFamily: 'PingFangSC-Regular',
      opacity: 0.87
    },
    center: ['50%', '50%'],
    radius: '75%',
    nameGap: 8,
    splitArea: {
      show: false,
      areaStyle: { // 分隔区域的样式设置。  color为数组 循环取值

      }
    }
  }
}

let coordinateDefaultOption = {
  grid: {
    show: false,
    left: '10%',
    top: 60,
    right: 'auto',
    bottom: 'auto',
    width: 'auto',
    height: 'auto',
    containLabel: true,
    backgroundColor: 'transparent',
    borderColor: '#ccc',
    borderWidth: 0
  },
  xAxis: {
    show: false,
    // gridIndex: 0, // x 轴所在的 grid 的索引，默认位于第一个 grid
    position: 'bottom',
    offset: 0,
    // type: 'category',
    name: '',
    nameRotate: 0,
    inverse: false,
    boundaryGap: true,
    scale: false,
    triggerEvent: false,
    axisLine: { // 坐标 轴线
      show: false,
      onZero: true
    },
    axisTick: { // 坐标轴刻度相关设置
      show: false,
      alignWithLabel: false,
      interval: 'auto',
      inside: false,
      length: 5
    },
    axisLabel: { // 坐标轴刻度标签的相关设置
      show: true,
      interval: 'auto',
      inside: false,
      rotate: 0,
      margin: 8,
      textStyle: {
        color: '#485465'
      }
    },
    splitLine: { // 坐标轴在 grid 区域中的分隔线。
      show: true,
      interval: 'auto',
      lineStyle: {
        color: '#F5F7F8',
        width: 1,
        opacity: 1
      }
    },
    splitArea: { // 坐标轴在 grid 区域中的分隔区域，默认不显示。
      interval: 'auto',
      show: false
    }
  },
  yAxis: {
    show: false,
    gridIndex: 0, // x 轴所在的 grid 的索引，默认位于第一个 grid
    position: 'bottom',
    offset: 0,
    // type: 'category',
    name: '',
    nameRotate: 0,
    inverse: false,
    boundaryGap: true,
    scale: false,
    triggerEvent: false,
    axisLine: { // 坐标 轴线
      show: false,
      onZero: true
    },
    axisTick: { // 坐标轴刻度相关设置
      show: false,
      alignWithLabel: false,
      interval: 'auto',
      inside: false,
      length: 5
    },
    axisLabel: { // 坐标轴刻度标签的相关设置
      show: true,
      interval: 'auto',
      inside: false,
      rotate: 0,
      margin: 8,
      textStyle: {
        color: '#485465'
      }
    },
    splitLine: { // 坐标轴在 grid 区域中的分隔线。
      show: true,
      interval: 'auto',
      lineStyle: {
        color: '#F5F7F8',
        width: 1,
        opacity: 1
      }
    },
    splitArea: { // 坐标轴在 grid 区域中的分隔区域，默认不显示。
      interval: 'auto',
      show: false
    }
    // data: []
  }
}
// --------------------------------------------------------------变量
let publicAttributesName = ['title', 'tooltip', 'toolbox', 'legend', 'dataZoom', 'visualMap', 'radar']
let coordinateDefaultOptionName = ['grid', 'xAxis', 'yAxis']
let featureName = ['dataView', 'magicType', 'restore', 'saveAsImage', 'dataZoom']
// --------------------------------------------------------------util
let isValidOption = function (option) {
  return isObject(option) &&
    !isEmptyObject(option) &&
    hasSeriesKey(option) &&
    isArray(option['series']) &&
    !isArrayEmpty(option['series'])
}

let isObject = function (option) {
  return Object.prototype.isPrototypeOf(option)
}

let isEmptyObject = function (option) {
  return Object.keys(option).length === 0
}

let hasSeriesKey = function (option) {
  return !!option['series']
}

let isArray = function (data) {
  return Array.isArray(data)
}

let isArrayEmpty = function (data) {
  return data.length === 0
}
let isInArray = function (arr, value) {
  for (var i = 0; i < arr.length; i++) {
    if (value === arr[i]) {
      return true
    }
  }
  return false
}
let isFunction = function (val) {
  return Object.prototype.toString.call(val) === '[object Function]'
}

// --------------------------------------------------------------
let getPublicAttributes = function (option, data) {
  for (let key in data) {
    if (isInArray(publicAttributesName, key) || isInArray(coordinateDefaultOptionName, key)) {
      option[key] = eval(key + '(data[key])')
    }
  }
  return option
}

let title = function (data) {
  if (isObject(data)) {
    setData('title', data, defaultOption)
  }
  return defaultOption.title
}
let tooltip = function (data) {
  setData('tooltip', data, defaultOption)
  return defaultOption.tooltip
}
let legend = function (data) {
  if (isObject(data)) {
    if (isArray(data['data'])) {
      let legendObj = []
      for (let i = 0; i < data['data'].length; i++) {
        if (!isObject(data['data'][i])) {
          legendObj.push({
            icon: 'circle',
            name: data['data'][i]
          })
        } else {
          legendObj.push(data['data'][i])
        }
      }
      defaultOption.legend['data'] = legendObj
      delete data['data']
    }
    setData('legend', data, defaultOption)
    return defaultOption.legend
  }
}
let dataZoom = function (data) {
  if (isArray(data) && !isArrayEmpty(data)) {
    return data
  } else {
    return defaultOption.dataZoom
  }
}
let toolbox = function (data) {
  let ft = {}
  for (let i = 0; i < data.length; i++) {
    if (isObject(data[i])) {
      ft[data[i].name] = data[i].data
    } else if (isInArray(featureName, data[i])) {
      ft[data[i]] = feature[data[i]]
    }
    defaultOption.toolbox.feature = ft
  }
  return defaultOption.toolbox
}

let grid = function (data) {
  if (isArray(data)) {
    defaultOption.grid = []
    for (let i = 0; i < data.length; i++) {
      defaultOption.grid.push(setData('grid', data[i], coordinateDefaultOption))
    }
  } else {
    defaultOption.grid = setData('grid', data, coordinateDefaultOption)
  }
  return defaultOption.grid
}
let xAxis = function (data) {
  setCoordinateData('xAxis', data)
  return defaultOption.xAxis
}
let yAxis = function (data) {
  setCoordinateData('yAxis', data)
  return defaultOption.yAxis
}
let setData = function (name, data, option) {
  option[name].show = true
  for (let key in data) {
    option[name][key] = data[key]
  }
  return option[name]
}
// 雷达图 外层radar 处理
let radar = function (data) {
  defaultOption.radar = setData('radar', data, defaultOption)
  return defaultOption.radar
}
// X,Y轴数据处理
let setCoordinateData = function (name, data) {
  if (isArray(data)) {
    defaultOption[name] = []
    for (let i = 0; i < data.length; i++) {
      let obj = setData(name, data[i], coordinateDefaultOption)
      obj.gridIndex = i
      defaultOption[name].push(setData(name, data[i], coordinateDefaultOption))
    }
  } else {
    defaultOption[name] = setData(name, data, coordinateDefaultOption)
  }
}
/*
id:图表容器ID
option: 图表所需参数  类似echarts 必传
customClass：图表容器大小 {width:"300px",height:"300px"}
*/
function Ischart (id, option, customClass) {
  if (isString(option.series[0].type)) {
    this.type = option.series[0].type
  } else {
    console.error('确认图表类型')
    return
  }
  this.id = arguments[0] || 'chart'
  this.option = arguments[1]
  this.customClass = arguments[2] || {
    width: '400px',
    height: '300px'
  }
  this.myEcharts = null
  this.handleOption = null
  this.init()
  return this.myEcharts
}
Ischart.prototype.init = function () {
  this.initDom()
  this.setOption()
}
Ischart.prototype.setOption = function () {
  this.getDefaultOption() // 获取series 外层参数
  this.handleOption.series = []
  if (this.option.series) {
    for (let i = 0; i < this.option.series.length; i++) {
      let obj = this.option.series[i]
      obj = mergeObject(this.setSeriesDefault(), obj) // 获取series  内层默认属性
      this.handleOption.series.push(obj)
    }
  }
  // console.log(JSON.stringify(this.handleOption))
  this.myEcharts.setOption(this.handleOption)
}

Ischart.prototype.getDefaultOption = function () {
  let defaultOption = {}
  let colorArray = null
  switch (this.type) {
  case 'bar':
    this.defaultOption = {
      series: []
    }
    break
  case 'radar':
    colorArray = ['rgba(74,206,180)', 'rgba(251,194,91)', 'rgba(253,177,176)', 'rgba(114,234,228)', 'rgba(195,183,255)', 'rgba(125,201,236)']
    let color = colorArray[Math.floor(Math.random() * colorArray.length)]
    defaultOption = {
      color: color,
      radar: [],
      tooltip: {
        trigger: 'item',
        backgroundColor: color.substring(0, color.length - 1) + ',0.5)',
        textStyle: {
          fontFamily: 'PingFangSC-Regular',
          fontSize: 12,
          color: 'rgba(0,0,0,0.6)'
        }
      },
      series: []
    }
    break
  case 'funnel':
    colorArray = ['#76DDFB', '#9AD5FF', '#CDEAFF', '#53A8E2', '#2C82BE'] // 颜色 取值顺序不确定
    defaultOption = {
      color: colorArray,
      tooltip: {},
      series: []
    }
    break
  case 'gauge':
    defaultOption = {
      tooltip: {},
      legend: {},
      series: []
    }
    break
  case 'graph':
    defaultOption = {
      tooltip: {},
      series: []
    }
    break
  case 'line':
    defaultOption = {
      title: {
        text: ''
      },
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: []
      },
      grid: {
        // left: '3%',
        // right: '5%',
        // bottom: '3%',
        containLabel: true
      },
      toolbox: {
        feature: {
          saveAsImage: {}
        }
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: []
      },
      yAxis: {
        type: 'value'
      },
      series: []
    }
    break
  case 'pie':
    colorArray = ['#DBECF8', '#2C82BE', '#53A8E2', '#76DDFB', '#F8DB71', '#F5647C', '#8AD89A']
    defaultOption = {
      color: colorArray,
      tooltip: {
        trigger: 'item',
        formatter: '{b} : {d}%'
      },
      legend: {
        top: '3%',
        left: '3%'
      },
      series: []
    }
    break
  case 'scatter':
    defaultOption = {
      tooltip: {},
      legend: {},
      series: []
    }
    break
  case 'tree':
    defaultOption = {
      tooltip: {},
      legend: {},
      series: []
    }
    break
  case 'wordCloud':
    defaultOption = {
      tooltip: {
        show: true,
        trigger: 'item'
      },
      series: []
    }
    break
  }
  this.handleOption = getPublicAttributes(defaultOption, this.option)
}
Ischart.prototype.initDom = function () {
  let container = document.getElementById(this.id)
  container.style.width = this.customClass.width
  container.style.height = this.customClass.height
  this.myEcharts = echarts.init(container)
}
Ischart.prototype.setSeriesDefault = function () {
  switch (this.type) {
  case 'radar':
    return {
      type: 'radar',
      symbol: 'circle', // ECharts 提供的标记类型包括 'circle', 'rect', 'roundRect', 'triangle', 'diamond', 'pin', 'arrow'
      symbolSize: 8,
      itemStyle: { // 折线拐点标志的样式。
        normal: {
          borderColor: '#000',
          borderWidth: 0
        }
      },
      label: { // 图形上的文本标签
        show: true
      },
      lineStyle: { // 线条样式。
        // color: '#4ACEB4',
        width: 1,
        type: 'solid'
      },
      areaStyle: { // 区域填充样式。
        // color: '#4ACEB4',
        opacity: 0.3
      },
      emphasis: { // 高亮样式
        areaStyle: {
          opacity: 0.5
        }
      }
    }
  case 'bar':
    break
  case 'funnel':
    return {
      type: 'funnel',
      top: 60,
      bottom: 0,
      min: 20, // 指定的数据最小值。
      max: 100, // 指定的数据最大值。
      minSize: '20%', // 数据最小值 min 映射的宽度。
      maxSize: '100%', // 数据最大值 max 映射的宽度。
      sort: 'descending', // 数据排序， 可以取 'ascending'，'descending'，'none'（表示按 data 顺序）
      gap: 5, // 图形之间的 间距 默认0
      label: { // 漏斗图图形上的文本标签
        normal: {
          show: true,
          position: 'inside', // 标签的位置。
          fontFamily: 'PingFangSC-Regular',
          fontSize: 14
        }

      },
      itemStyle: { // 图形样式。
        normal: {
          borderColor: '#fff',
          borderWidth: 0
        }
      },
      emphasis: {
        label: {
          fontSize: 16
        }
      }
    }
  case 'gauge':
    return {
      type: 'gauge',
      radius: '75%', // 仪表盘半径
      startAngle: 225, // 仪表盘起始角度。圆心 正右手侧为0度，正上方为90度，正左手侧为180度。
      endAngle: -45, // 仪表盘结束角度
      clockwise: true, // 仪表盘刻度是否是顺时针增长。
      min: 0, // 最小的数据值，映射到 minAngle。
      max: 100, // 最大的数据值，映射到 maxAngle。
      splitNumber: 10, // 仪表盘刻度的分割段数。
      axisLine: { // 仪表盘轴线相关配置。
        show: true,
        lineStyle: {}
      },
      splitLine: { // 分隔线样式。
        show: true,
        length: 30,
        lineStyle: {}
      },
      axisTick: { // 刻度样式。
        show: true,
        splitNumber: 5,
        length: 8,
        lineStyle: {}
      },
      axisLabel: { // 刻度标签。
        show: true,
        color: '#000'
      },
      pointer: { // 仪表盘指针。
        show: true,
        length: '80%',
        width: 8
      },
      itemStyle: { // 仪表盘指针样式。

      }
    }
  case 'graph':
    return {
      type: 'graph',
      layout: 'force', // 图的布局。  'circular' 采用环形布局 'force' 采用力引导布局， none
      animation: false,
      roam: true, // 是否开启鼠标缩放和平移漫游
      draggable: true, // 节点是否可拖拽，只在使用力引导布局的时候有用。
      resizable: true, // 节点是否可缩放，只在使用力引导布局的时候有用。
      symbol: 'circle', // 关系图节点标记的图形。
      symbolSize: 10,
      label: { // 图形上的文本标签
        normal: {
          position: 'right',
          formatter: '{b}'
        }
      },
      edgeSymbol: ['none', 'none'], // edgeSymbol: ['circle', 'arrow']
      emphasis: { // 高亮的图形样式。

      }
    }
  case 'line':
    break
  case 'pie':
    break
  case 'scatter':
    return {
      type: 'scatter', //
      symbol: 'circle', // 'circle', 'rect', 'roundRect', 'triangle', 'diamond', 'pin', 'arrow'
      symbolSize: 10, // [20, 10] 表示标记宽为20，高为10
      itemStyle: {

      }
    }
  case 'tree':
    return {
      type: 'tree', //
      layout: 'orthogonal', // radial 径向  orthogonal正交
      orient: 'LR', // 树图中 正交布局 的方向
      symbol: 'emptycircle', // 'circle', 'rect', 'roundRect', 'triangle', 'diamond', 'pin', 'arrow'
      symbolSize: 7,
      itemStyle: {}, // 树图中每个节点的样式，其中 itemStyle.color 表示节点的填充色
      label: {} // label 描述了每个节点所对应的文本标签的样式。
    }
  case 'wordCloud':
    return {
      type: 'wordCloud', //
      maskImage: this.imageSrc ? this.maskImage : '',
      sizeRange: [6, 66],
      rotationRange: [-45, 90],
      textPadding: 0,
      autoSize: {
        enable: true,
        minSize: 6
      },
      textStyle: {
        normal: {
          color: function () {
            return 'rgb(' + [
              Math.round(Math.random() * 160),
              Math.round(Math.random() * 160),
              Math.round(Math.random() * 160)
            ].join(',') + ')'
          }
        },
        emphasis: {
          shadowBlur: 10,
          shadowColor: '#333'
        }
      }
    }
  default:
    break
  }
}
window.Ischart = Ischart
