//
// JM_util=======================================================================
//
var JM_util = {
  uuid: function () {
    var s = []
    var hexDigits = '0123456789abcdef'
    for (var i = 0; i < 36; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1)
    }
    s[14] = '4' // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1) // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = '-'

    var uuid = s.join('')
    return uuid
  },
  // 合并对象
  mergeObjectDeep: function (defaultObj, originalObj) {
    let newObj = this.deepCopy(defaultObj)
    for (let i in defaultObj) {
      let dv = defaultObj[i]
      let ov = originalObj[i]
      if (this.isObjectObject(dv) && this.checkNull(ov)) {
        newObj[i] = this.mergeObjectDeep(dv, ov)
      } else {
        if (this.checkNull(ov)) {
          newObj[i] = this.deepCopy(ov)
        }
      }
    }
    return newObj
  },
  deepCopy: function (source) {
    var sourceCopy = null
    if (this.isObjectObject(source)) {
      sourceCopy = {}
      for (var item in source) {
        sourceCopy[item] = this.deepCopy(source[item])
      }
    } else if (this.isArray(source)) {
      sourceCopy = []
      source.forEach(item => {
        sourceCopy.push(this.deepCopy(item))
      })
    } else {
      return source
    }
    return sourceCopy
  },
  // 获取变量的类型
  getType: function (obj) {
    var str = Object.prototype.toString.call(obj)
    var map = {
      '[object Boolean]': 'boolean',
      '[object Number]': 'number',
      '[object String]': 'string',
      '[object Function]': 'function',
      '[object Array]': 'array',
      '[object Date]': 'date',
      '[object RegExp]': 'regExp',
      '[object Undefined]': 'undefined',
      '[object Null]': 'null',
      '[object Object]': 'object'
    }
    if (obj instanceof Element) { // 判断是否是dom元素，如div等
      return 'element'
    }
    return map[str]
  },
  isArray: function (obj) {
    return Array.isArray(obj) || Object.prototype.toString.call(obj) === '[object Array]'
  },
  isObject: function (obj) {
    var type = typeof obj
    return (type === 'function' || type === 'object') && !!obj
  },
  isObjectObject: function (val) {
    return Object.prototype.toString.call(val) === '[object Object]'
  },
  isDate: function (val) {
    return Object.prototype.toString.call(val) === '[object Date]'
  },
  isFunction: function (val) {
    return Object.prototype.toString.call(val) === '[object Function]'
  },
  isString: function (val) {
    return Object.prototype.toString.call(val) === '[object String]'
  },
  isNumber: function (val) {
    return Object.prototype.toString.call(val) === '[object Number]'
  },
  // 校验是否为空
  checkNull: function (obj) {
    if (obj === null || obj === '' || obj === undefined) {
      return false
    } else if (JSON.stringify(obj) === '{}') {
      let a = false
      for (let i in obj) {
        a = true
      }
      return a
    } else if ((this.isString(obj) || this.isArray(obj)) && obj.length === 0) {
      return false
    } else {
      return true
    }
  },
  // 校验数组元素是否重复
  checkRepeat: function (arr) {
    for (let i = 0; i < arr.length; i++) {
      let arrCopy = [...arr]
      let item = arrCopy[i]
      arrCopy.splice(i, 1)
      if (arrCopy.includes(item)) {
        return false
      }
    }
    return true
  },
  // 字符串转常量名
  evil: function (str) {
    let fn = Function
    return new fn('return ' + str)()
  }

}
//
// JM_B_util=======================================================================
//
var JM_B_util = {}
function initBUtil () {
  JM_B_util = {
  //
  // =========================== new ==============================
  //
  // 创建地理坐标点
    newPoint (arr) {
      return new BMap.Point(...arr)
    },
    creatPoint (obj) {
      var point = null
      if (obj.point) {
        point = obj.point
      } else {
        point = this.newPoint([obj.lng, obj.lat])
      }
      return point
    },
    // 创建覆盖物所使用的图标
    newIcon (iconOption) {
      var url = iconOption.url
      var size = new BMap.Size(...iconOption.position)
      var anchor = null
      if (JM_util.checkNull(iconOption.anchor)) {
        anchor = new BMap.Size(...iconOption.anchor)
      } else {
        anchor = new BMap.Size(0.5 * iconOption.position[0], 0.5 * iconOption.position[1])
      }
      var imageOffset = new BMap.Size(...iconOption.imageOffset)
      var imageSize = new BMap.Size(...iconOption.imageSize)
      var option = {
        anchor: anchor,
        imageOffset: imageOffset,
        imageSize: imageSize
      }
      var icon = new BMap.Icon(url, size, option)
      return icon
    },
    // 创建点(点位信息对象，图标样式对象)
    newMarker (markerObj) {
      var point = this.creatPoint(markerObj)
      var style = markerObj.markerStyle
      var icon = null
      if (!style.isSymbol) {
        icon = this.newIcon(style)
      } else {
        style.symbolStyle.anchor = new BMap.Size(...style.symbolStyle.anchor)
        if (JM_util.checkNull(style.symbolPts)) {
          icon = new BMap.Symbol(style.symbolPts, style.symbolStyle)
        } else {
          var shape = this.getSymbolShapeType(style.SymbolShapeType)
          icon = new BMap.Symbol(shape, style.symbolStyle)
        }
      }
      var option = { title: markerObj.title || '', icon: icon }
      var marker = new BMap.Marker(point, option)
      this.addAttributes(marker, markerObj)

      return marker
    },
    // 创建文本标注
    newLabel (labelObj) {
      var point = this.newPoint([labelObj.lng, labelObj.lat])
      var style = labelObj.labelStyle
      style.transform += ' rotate(' + style.angle + 'deg)'
      var option = {
        position: point,
        offset: new BMap.Size(...style.offset),
        enableMassClear: style.enableMassClear
      }

      var label = new BMap.Label(labelObj.text, option)
      label.setStyle(style)
      this.addAttributes(label, labelObj)

      return label
    },
    // 创建圆
    newCircle (circleObj) {
      var point = this.newPoint(circleObj.center)
      var circle = new BMap.Circle(point, circleObj.radius, circleObj.circleStyle)
      this.addAttributes(circle, circleObj)
      return circle
    },
    // 创建折线
    newpPolyline (polylineObj) {
      var points = []
      if (JM_util.isArray(polylineObj.points[0])) {
        polylineObj.points.forEach(obj => {
          points.push(this.newPoint(obj))
        })
      } else {
        points = polylineObj.points
      }
      var polyline = new BMap.Polyline(points, polylineObj.polylineStyle)
      this.addAttributes(polyline, polylineObj)
      return polyline
    },
    // 创建弧线
    newCurveLine (CurveLineObj) {
      var points = []
      if (JM_util.isArray(CurveLineObj.points[0])) {
        CurveLineObj.points.forEach(obj => {
          points.push(this.newPoint(obj))
        })
      } else {
        points = CurveLineObj.points
      }
      var CurveLine = new BMapLib.CurveLine(points, CurveLineObj.CurveLineStyle)
      this.addAttributes(CurveLine, CurveLineObj)
      return CurveLine
    },
    // 创建多边形
    newPolygon (polygonObj) {
      var points = []
      polygonObj.points.forEach(obj => {
        points.push(this.newPoint(obj))
      })
      var polygon = new BMap.Polygon(points, polygonObj.polygonStyle)
      this.addAttributes(polygon, polygonObj)
      return polygon
    },
    // 创建驾车对象
    newDrivingRoute (map, opts) {
      var driving = new BMap.DrivingRoute(map, opts)
      return driving
    },
    // 创建路书对象
    newLuShu (map, pathPoints, option) {
      var opts = option.option
      opts.icon = this.newIcon(option.icon)
      var module = new BMapLib.LuShu(map, pathPoints, opts)
      return module
    },
    //
    // =========================== set ==============================
    //
    // 补充覆盖物id、info等属性
    addAttributes (overlay, overlayObj) {
      overlay.id = overlayObj.id
      overlay.info = overlayObj
      overlay.overlayType = overlayObj.overlayType
      if (overlay.overlayType !== 'label') {
        overlay.edit = true
      }
      return overlay
    },
    // 设置多边形/圆样式
    setPolygonStyle (polygon, style) {
      polygon.setStrokeWeight(style.strokeWeight)
      polygon.setStrokeColor(style.strokeColor)
      polygon.setStrokeOpacity(style.strokeOpacity)
      polygon.setStrokeStyle(style.strokeStyle)
      polygon.setFillColor(style.fillColor)
      polygon.setFillOpacity(style.fillOpacity)
    },
    // 设置折线样式
    setPolylineStyle (polygon, style) {
      polygon.setStrokeWeight(style.strokeWeight)
      polygon.setStrokeColor(style.strokeColor)
      polygon.setStrokeOpacity(style.strokeOpacity)
      polygon.setStrokeStyle(style.strokeStyle)
    },
    //
    // =========================== show ==============================
    //
    // 显示信息窗口（自定义）
    showInfoWin (infoWin, obj, infoWinStyle) {
      var point = this.creatPoint(obj)
      var option = {
        point: point,
        type: infoWinStyle.type,
        width: infoWinStyle.width,
        title: obj.title, // 信息窗口标题
        img: obj.img,
        imgSize: infoWinStyle.imgSize,
        content: obj.content,
        button: infoWinStyle.button,
        info: obj,
        contentHtml: obj.contentHtml,
        customClass: infoWinStyle.customClass
      }
      infoWin.show(option)
    },
    // 显示右键菜单
    showContextMenu (menu, info) {
      var point = this.creatPoint(info)
      var option = {
        point: point,
        menuItem: info.menuItem,
        info: info,
        customClass: info.customClass
      }
      menu.show(option)
    },
    //
    // =========================== clear ==============================
    //
    // 清除指定覆盖物
    clearOverlays (map, idList) {
      var overlays = map.getOverlays()
      overlays.forEach(e => {
        if (idList.includes(e.id)) {
          map.removeOverlay(e)
        }
      })
    },
    // 清除指定覆盖物
    clearClusterOverlays (clusterLayer, idList) {
      var all = clusterLayer.getMarkers()
      var markers = []
      all.forEach(marker => {
        if (idList.includes(marker.id)) {
          markers.push(marker)
        }
      })
      clusterLayer.removeMarkers(markers)
    },
    //
    // =========================== get ==============================
    //
    // 获取可编辑的覆盖物
    getEditOverlays (map) {
      var overlays = map.getOverlays()
      var editItems = []
      overlays.forEach(e => {
        if (e.edit) {
          editItems.push(e)
        }
      })
      return editItems
    },
    // 根据ID获取覆盖物
    getOverlayById (map, id) {
      var overlays = map.getOverlays()
      var target = null
      overlays.forEach(e => {
        if (e.id === id) {
          target = e
        }
      })
      return target
    },
    // 获取矢量图标类预设的图标样式
    getSymbolShapeType (type) {
      var SymbolShapeTypes = [
        { value: BMap_Symbol_SHAPE_CIRCLE, type: '圆形' },
        { value: BMap_Symbol_SHAPE_RECTANGLE, type: '矩形' },
        { value: BMap_Symbol_SHAPE_RHOMBUS, type: '菱形' },
        { value: BMap_Symbol_SHAPE_STAR, type: '五角星' },
        { value: BMap_Symbol_SHAPE_BACKWARD_CLOSED_ARROW, type: '方向向下的闭合箭头' },
        { value: BMap_Symbol_SHAPE_FORWARD_CLOSED_ARROW, type: '方向向上的闭合箭头' },
        { value: BMap_Symbol_SHAPE_BACKWARD_OPEN_ARROW, type: '方向向下的非闭合箭头' },
        { value: BMap_Symbol_SHAPE_FORWARD_OPEN_ARROW, type: '方向向上的非闭合箭头' },
        { value: BMap_Symbol_SHAPE_POINT, type: '定位点图标' },
        { value: BMap_Symbol_SHAPE_PLANE, type: '飞机形状' },
        { value: BMap_Symbol_SHAPE_CAMERA, type: '照相机形状' },
        { value: BMap_Symbol_SHAPE_WARNING, type: '警告符号' },
        { value: BMap_Symbol_SHAPE_SMILE, type: '笑脸形状' },
        { value: BMap_Symbol_SHAPE_CLOCK, type: '钟表形状' }
      ]
      var SymbolShapeType = null
      SymbolShapeTypes.forEach(e => {
        if (e.type === type) {
          SymbolShapeType = e.value
        }
      })
      return SymbolShapeType
    },
    //
    // =========================== other ==============================
    //
    // 动态加载js
    loadJScript (src, func, type) {
      var script = document.createElement('script')
      script.type = type || 'text/javascript'
      script.src = src
      if (func) {
        script.onload = function () {
          func()
        }
      }
      document.head.appendChild(script)
    },
    // 打开信息窗口并居中
    centerAndOpenInfoWindow (map, infoWin, obj, infoWinStyle) {
      var me = this
      var point = this.creatPoint(obj)
      infoWin.hide()
      map.panTo(point)
      setTimeout(function () { me.showInfoWin(infoWin, obj, infoWinStyle) }, 300)
    }
  }
}
//
// JM_option=======================================================================
//
var JM_option = {
  id: '', // 地图id
  area: '北京', // 地图显示的城市，必填
  center: [116.404, 39.915], // 中心点坐标，必填
  zoom: 12, // 地图初加载时展示的级别，必填
  minZoom: 1, // 地图允许展示的最小级别
  maxZoom: 30, // 地图允许展示的最大级别
  mapStyle: 'normal', // 地图样式
  viewMode: '2D', // 显示的地图类型
  resizeEnable: true,
  rotateEnable: true,
  pitchEnable: true,
  pitch: -90,
  rotation: 0,
  // skyColor: '#ff6600',
  showBuildingBlock: true,
  buildingAnimation: true, // 楼块出现是否带动画
  expandZoomRange: true,
  enableScrollWheelZoom: true, // 是否允许滚轮缩放地图
  enableDoubleClickZoom: true, // 是否允许通过双击鼠标放大地图
  enableDragging: true, // 是否允许拖拽
  enableMapClick: false, // 是否开启底图可点功能
  enableHighResolution: true, // 是否启用使用高分辨率地图。在iPhone4及其后续设备上，可以通过开启此选项获取更高分辨率的底图，v1.2,v1.3版本默认不开启，v1.4默认为开启状态
  enableAutoResize: true, // 是否自动适应地图容器变化
  icMapOnloadFunc: null, // 用户自定义地图加载完成后立即执行的方法
  // 比例尺控件
  scaleControl: {
    show: false, // 是否加载
    position: ['BOTTOM', 'LEFT'], // 先上下，后左右，大写
    x: 100,
    y: 24
  },
  // 缩放控件
  navigationControl: {
    show: false, // 是否加载
    position: ['BOTTOM', 'LEFT'], // 先上下，后左右，大写
    x: 17,
    y: 60
  },
  // 鹰眼(缩略图)
  overviewMapControl: {
    show: false, // 是否显示
    open: false, // 是否初始就打开，默认不打开false
    position: ['BOTTOM', 'RIGHT'], // 先上下，后左右，大写
    x: 10,
    y: 5
  },
  // 信息窗参数配置
  infoWinOption: {
    point: [0, 0],
    offset: [0, 33],
    type: 'normal',
    width: 300,
    title: '信息', // 信息窗口标题
    img: '',
    imgSize: ['100%', '100px'],
    content: '',
    button: [],
    info: null,
    contentHtml: '',
    customClass: ''
  },
  // 地图右键菜单
  icMapContextMenu: {
    isUsed: false, // 是否启用
    customClass: '',
    menuItem: [
      // {id: 菜单idstring, iconClass: 菜单图标类名string, label: 菜单文字string, clickEvent: 点击事件func} // 菜单项
    ]
  },
  // 覆盖物右键菜单参数配置
  ContextMenuOption: {
    isUsed: false, // 是否启用
    point: [0, 0],
    menuItem: [],
    info: '',
    customClass: ''
  },
  // 图像标注样式的默认参数
  markerStyle: {
    url: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_bs.png',
    position: [19, 33],
    anchor: [10, 33],
    imageOffset: [0, 0],
    imageSize: [19, 33],
    isSymbol: false,
    SymbolShapeType: '',
    symbolPts: [],
    symbolStyle: {
      anchor: [0, 0],
      scale: 1,
      rotation: 0,
      strokeWeight: 2,
      strokeColor: '#3D82EA',
      strokeOpacity: 1,
      fillColor: '#fff',
      fillOpacity: 0.6
    }
  },
  // 文本标注样式的默认参数
  labelStyle: {
    color: '#000',
    fontSize: '12px',
    height: 'auto',
    fontFamily: '微软雅黑',
    backgroundColor: '#fff',
    padding: '5px 10px',
    border: '1px solid #fff',
    borderRadius: 0,
    boxShadow: '1px 2px 1px rgba(0, 0, 0, .3)',
    transform: 'translate(-50%, -50%)',
    offset: [0, 0],
    angle: 0,
    enableMassClear: true
  },
  // 圆样式的默认参数
  circleStyle: {
    strokeWeight: 2,
    strokeColor: '#3D82EA',
    strokeOpacity: 1,
    strokeStyle: 'solid',
    fillColor: '#fff',
    fillOpacity: 0.6,
    enableMassClear: false
  },
  // 折线样式的默认参数
  polylineStyle: {
    strokeWeight: 4,
    strokeColor: '#3D82EA',
    strokeOpacity: 1,
    strokeStyle: 'solid',
    enableMassClear: false
  },
  // 弧线样式的默认参数
  CurveLineStyle: {
    strokeWeight: 4,
    strokeColor: '#3D82EA',
    strokeOpacity: 1,
    strokeStyle: 'solid',
    enableMassClear: false
  },
  // 多边形样式的默认参数
  polygonStyle: {
    strokeWeight: 2,
    strokeColor: '#3D82EA',
    strokeOpacity: 1,
    strokeStyle: 'solid',
    fillColor: '#fff',
    fillOpacity: 0.6,
    enableMassClear: false
  },
  // 行政区域边界
  icBoundary: {
    isUsed: false, // 是否启用
    area: [],
    polygonStyle: {
      strokeWeight: 2,
      strokeColor: '#3D82EA',
      strokeOpacity: 1,
      strokeStyle: 'solid',
      fillColor: '#fff',
      fillOpacity: 0,
      enableMassClear: false
    },
    hover: {
      isUsed: false, // 是否启用滑上效果
      polygonStyle: {
        strokeWeight: 2,
        strokeColor: '#3D82EA',
        strokeOpacity: 1,
        strokeStyle: 'solid',
        fillColor: '#fff',
        fillOpacity: 0
      },
      func: null
    }
  },
  // 热力图参数配置
  icHeatMap: {
    isUsed: true, // 是否用到热力图，用于控制是否加载相应js
    option: {
      radius: 20, // 热力图的半径
      visible: true, // 热力图是否显示
      // gradient: {}, // 热力图的渐变区间,JSON
      opacity: 0.6 // 热力的透明度,0~1
    }
  },
  // 点聚合参数配置
  icCluster: {
    isUsed: false, // 是否用到点聚合，用于控制是否加载相应js
    girdSize: 60, // 聚合计算时网格的像素大小，默认60
    maxZoom: 22, // 最大的聚合级别，大于该级别就不进行相应的聚合
    minClusterSize: 2, // 最小的聚合数量，小于该数量的不能成为一个聚合，默认为2
    isAverangeCenter: false, // 聚合点的落脚位置是否是所有聚合在内点的平均值，默认为否，落脚在聚合内的第一个点
    styles: [{ // 自定义聚合后的图标风格
      url: 'http://www.easyicon.net/api/resizeApi.php?id=501219&size=48', // 图片的url地址。(必选)
      // url: 'http://www.easyicon.net/api/resizeApi.php?id=501206&size=48', // 橙色
      // url: 'http://www.easyicon.net/api/resizeApi.php?id=501201&size=48', // 绿色
      // url: 'http://www.easyicon.net/api/resizeApi.php?id=501228&size=48', // 蓝色
      size: [48, 48], // 图片的大小。（必选）
      anchor: [0, 0], // 图标定位在地图上的位置相对于图标左上角的偏移值，默认偏移值为图标的中心位置。（可选）
      offset: [48, 48], // 图片相对于可视区域的偏移值，此功能的作用等同于CSS中的background-position属性。（可选）
      textSize: 14, // 文字的大小。（可选，默认10）
      textColor: '#fff' // 文字的颜色。（可选，默认black）
    }]
  },
  // 行车路线默认参数
  icDriving: {
    startIcon: {
      url: 'http://www.easyicon.net/api/resizeApi.php?id=1061279&size=48',
      position: [32, 32],
      anchor: [16, 32],
      imageOffset: [0, 0],
      imageSize: [32, 32]
    },
    endIcon: {
      url: 'http://www.easyicon.net/api/resizeApi.php?id=1061278&size=48',
      position: [32, 32],
      anchor: [16, 32],
      imageOffset: [0, 0],
      imageSize: [32, 32]
    },
    runIcon: {
      url: 'http://www.easyicon.net/api/resizeApi.php?id=11019&size=48',
      position: [48, 48],
      anchor: [24, 48],
      imageOffset: [0, 0],
      imageSize: [48, 48]
    },
    runIconTitle: '',
    pathStyle: {
      strokeWeight: 4,
      strokeColor: '#3D82EA',
      strokeOpacity: 0.6,
      strokeStyle: 'solid'
    },
    pathHoverStyle: {
      strokeWeight: 4,
      strokeColor: '#E6273E',
      strokeOpacity: 1,
      strokeStyle: 'solid'
    }
  },
  // 路书
  icLuShu: {
    isUsed: true, // 是否启用
    icon: {
      url: 'http://lbsyun.baidu.com/jsdemo/img/car.png',
      position: [52, 26],
      imageOffset: [0, 0],
      imageSize: [52, 26]
    },
    option: {
      speed: 4500,
      defaultContent: '',
      autoView: true,
      enableRotation: true,
      landmarkPois: []
    }
  },
  // 测距工具
  icDistanceTool: {
    isUsed: false, // 是否启用
    followText: '',
    unit: 'metric',
    lineColor: '#E6273E',
    lineStroke: 3,
    opacity: 1,
    lineStyle: 'solid'
    // secIcon: null,
    // closeIcon: null,
    // cursor: 'crosshair'
  },
  // 地图类型控件
  icMapTypeControl: {
    show: false, // 是否加载
    position: ['bottom', 'center'], // top/bottom，left/center/right
    x: 0,
    y: 5,
    customClass: '', // 自定义类名
    viewMode: ['2D', '3D', '卫星'],
    button: [
      { viewMode: '2D', label: '二 维' },
      { viewMode: '卫星', label: '实 景' },
      { viewMode: '3D', label: '三 维' }
    ] // 按钮上显示的文字
  },
  // 绘图控件
  icDrawControl: {
    isUsed: false, // 是否启用
    show: true, // 是否显示工具条
    position: ['top', 'center'], // top/bottom，left/center/right
    type: ['marker', 'circle', 'polyline', 'polygon', 'rectangle', 'clear'],
    defaultType: 'marker',
    x: 0,
    y: 20,
    customClass: '', // 自定义类名
    completeFunc: {
      marker: null,
      circle: null,
      polyline: null,
      polygon: null,
      rectangle: null,
      clear: null
    }
  },
  // 右上角工具条
  icToolBar: {
    show: false, // 是否显示
    customClass: '', // 自定义类名
    layout: ['center', 'traffic', 'edit', 'measure', 'coordinate', 'draw'], // 显示哪些功能
    position: ['right', 'top'], // 先左右，后上下
    x: 10,
    y: 20
  },
  // 搜索
  icSearchBar: {
    show: false,
    customClass: '', // 自定义类名
    keyword: '',
    placeholder: '请输入关键字',
    select: {
      show: false,
      value: [],
      dataList: [],
      // multiple: true,
      option: { label: 'text' }
    }
  },
  // 自定义模块
  icCustomBox: [],
  icCustomBoxItem: {
    show: false, // 是否显示
    customClass: '', // 自定义类名
    position: ['right', 'top'],
    x: 10,
    y: 0,
    data: []
  }
}
// 地图样式，百度
var JM_stylesB = [
  { style: 'normal', label: '默认风格' },
  { style: 'light', label: '清新蓝风格' },
  { style: 'dark', label: '黑夜风格' },
  { style: 'redalert', label: '红色警戒风格' },
  { style: 'googlelite', label: '精简风格' },
  { style: 'grassgreen', label: '自然绿风格' },
  { style: 'midnight', label: '午夜蓝风格' },
  { style: 'pink', label: '浪漫粉风格' },
  { style: 'darkgreen', label: '青春绿风格' },
  { style: 'bluish', label: '清新蓝绿风格' },
  { style: 'grayscale', label: '高端灰风格' },
  { style: 'hardedge', label: '强边界风格' }
]
//
// JM_html=======================================================================
//
var JM_html = {
  addMapDiv: function (domId, mapId) {
    var target = $('#' + domId)
    var html = '<div class=\'ic-map-div\'><div id=\'' + mapId + '\' class=\'ic-map\'></div></div>'
    target.append(html)
  },
  addMapTypeControl: function (box) {
    var html = '<div class=\'ic-map-type-control\'>' +
                '<button viewMode=\'2D\'>二维</button>' +
                '<button viewMode=\'卫星\'>实景</button>' +
                '<button viewMode=\'3D\'>三维</button>' +
                '<div class=\'ic-map-type-control-mark\'>' +
                  '<span class=\'ic-map-checkbox-icon\'><span class=\'ic-map-checkbox-fill\'></span></span>' +
                  '<span class=\'ic-map-checkbox-label\'>标注</span>' +
                '</div>' +
              '</div>'
    box.append(html)
  },
  addToolBar: function (box, area) {
    var html = '<div class=\'ic-map-toolbar\'>' +
                  '<span class=\'ic-map-toolbar-btn\'><i class=\'icon iconfont icon-position\'></i>' + area + '</span><b></b>' +
                  '<span class=\'ic-map-toolbar-btn\'><i class=\'icon iconfont icon-shishilukuang-copy\'></i>路况</span><b></b>' +
                  '<span class=\'ic-map-toolbar-btn\'><i class=\'icon iconfont icon-bianji-copy-copy\'></i>编辑</span><b></b>' +
                  '<span class=\'ic-map-toolbar-btn\'><i class=\'icon iconfont icon-tool\'></i>工具箱</span>' +
                  '<div class=\'ic-map-toolbar-tool\'>' +
                    '<b class=\'ic-map-toolbar-triangle\'></b>' +
                    '<span><i class=\'icon iconfont icon-zuobiao\'></i>拾取坐标</span>' +
                    '<span><i class=\'icon iconfont icon-draw\'></i>绘图</span>' +
                    '<span><i class=\'icon iconfont icon-juli-copy\'></i>测距</span>' +
                  '</div>' +
                '</div>'
    box.append(html)
  },
  addDrawBar: function (box, typeList) {
    var html = '<div class=\'ic-edit-control\'>'
    typeList.forEach(type => {
      html += '<button data-type=\'' + type + '\'><i class=\'icon iconfont icon-map-' + type + '\'></i></button>'
    })
    html += '</div>'
    box.append(html)
  },
  addSearchBar: function (box) {
    var html = ''
    box.append(html)
  },
  addAdvancedLabel: function (box) {
    var html = ''
    box.append(html)
  }
}
//
// JM_InfoWin=======================================================================
//
var JM_InfoWin = null
function assignInfoWin () {
  JM_InfoWin = function (option) {
    this.overlayType = 'InfoWin'
    this._default = Object.assign({}, option)
    this._option = Object.assign({}, option)
  }
  JM_InfoWin.prototype = new BMap.Overlay()
  // 初始化
  JM_InfoWin.prototype.initialize = function (map) {
    this._map = map

    var div = document.createElement('div')
    div.setAttribute('id', 'icMapInfoWin')
    div.setAttribute('class', 'ic-map-infoWin')
    map.getPanes().floatPane.appendChild(div)
    this._div = div

    return div
  }
  // 绘制
  JM_InfoWin.prototype.draw = function () {
    var position = this._map.pointToOverlayPixel(this._option.point)
    this._div.setAttribute('class', 'ic-map-infoWin ' + this._option.customClass)
    this._div.style.width = this._option.width + 'px'
    this._div.style.left = position.x - this._option.offset[0] + 'px'
    this._div.style.top = position.y - this._option.offset[1] + 'px'
  }
  // 显示
  JM_InfoWin.prototype.show = function (option) {
    var _this = this
    this._option = JM_util.mergeObjectDeep(this._default, option)
    if (this._div) {
      this._div.style.display = 'block'
      this._div.innerHTML = createInfoWinHtml(this._option)
      this.draw()

      // X按钮事件绑定
      document.getElementById('icMapInfoWinCancleBtn').onclick = function () {
        _this.hide()
      }

      // 禁止信息窗口上的地图双击事件
      document.getElementById('icMapInfoWin').ondblclick = function (ev) {
        var oEvent = ev || event
        oEvent.cancelBubble = true
        oEvent.stopPropagation()
      }

      // 按钮事件绑定
      if (this._option.button.length) {
        this._option.button.forEach(e => {
          document.getElementById(e.id).onclick = function () {
            e.clickEvent(_this._option.info)
          }
        })
      }
    }
  }
  // 隐藏
  JM_InfoWin.prototype.hide = function () {
    if (this._div) {
      var position = this._map.pointToOverlayPixel(this._default.point)
      this._div.style.left = position.x + 'px'
      this._div.style.top = position.y + 'px'
      this._div.style.display = 'none'
    }
  }
  // 信息窗dom
  function createInfoWinHtml (option) {
    if (option.type === 'normal') {
      return normalWin(option)
    } else if (option.type === 'custom') {
      return customWin(option)
    } else {
      return false
    }
  }
  // 常规（图片/一段文字/图片+一段文字）
  function normalWin (option) {
    var text = '<div class="ic-map-infoWin-title">' + option.title + '<i id="icMapInfoWinCancleBtn" class="icon iconfont icon-shanchu1"></i></div>' +
      '<span class="ic-map-infoWin-triangle"></span>' +
      '<div class="ic-map-infoWin-content">'
    if (JM_util.checkNull(option.img)) {
      text += '<span class="ic-map-infoWin-img" style="width:' + option.imgSize[0] + ';height:' + option.imgSize[1] + ';"><img src="' + option.img + '"/></span>'
    }
    if (JM_util.checkNull(option.content)) {
      text += '  <p>' + option.content + '</p>'
    }
    if (option.button.length) {
      option.button.forEach(e => {
        text += '<button id="' + e.id + '">' + e.label + '</button>'
      })
    }
    text += '</div>'
    return text
  }
  // 自定义
  function customWin (option) {
    var text = '<div class="ic-map-infoWin-title">' + option.title + '<i id="icMapInfoWinCancleBtn" class="icon iconfont icon-shanchu1"></i></div>' +
      '<span class="ic-map-infoWin-triangle"></span>' +
      '<div class="ic-map-infoWin-content">'
    if (JM_util.checkNull(option.contentHtml)) {
      text += option.contentHtml
    }
    if (option.button.length) {
      option.button.forEach(e => {
        text += '<button id="' + e.id + '">' + e.label + '</button>'
      })
    }
    text += '</div>'
    return text
  }
}
//
// JM_ContextMenu=======================================================================
//
var JM_ContextMenu = null
function assignContextMenu () {
  JM_ContextMenu = function (option) {
    this.overlayType = 'ContextMenu'
    this._default = option

    if (JM_util.checkNull(option)) {
      this._option = JM_util.mergeObjectDeep(this._default, option)
    } else {
      this._option = Object.assign({}, this._default)
    }
  }
  JM_ContextMenu.prototype = new BMap.Overlay()
  // 初始化
  JM_ContextMenu.prototype.initialize = function (map) {
    this._map = map

    var div = document.createElement('div')
    div.setAttribute('id', 'icMapContextMenu')
    div.setAttribute('class', 'ic-map-contextMenu')
    map.getPanes().labelPane.appendChild(div)
    this._div = div

    return div
  }
  // 绘制
  JM_ContextMenu.prototype.draw = function () {
    var position = this._map.pointToOverlayPixel(this._option.point)
    this._div.setAttribute('class', 'ic-map-contextMenu ' + this._option.customClass)
    this._div.style.left = position.x + 'px'
    this._div.style.top = position.y + 'px'
  }
  // 显示
  JM_ContextMenu.prototype.show = function (option) {
    var _this = this
    this._option = JM_util.mergeObjectDeep(this._default, option)
    if (this._div) {
      this._div.style.display = 'block'
      this._div.innerHTML = createMemuHtml(this._option)
      this.draw()

      // 禁止信息窗口上的地图双击事件
      document.getElementById('icMapContextMenu').ondblclick = function (ev) {
        var oEvent = ev || event
        oEvent.cancelBubble = true
        oEvent.stopPropagation()
      }

      // 单击关闭菜单
      document.onclick = function (ev) {
        _this.hide()
      }

      // 菜单项点击事件绑定
      if (this._option.menuItem.length) {
        this._option.menuItem.forEach(e => {
          document.getElementById(e.id).onclick = function () {
            _this.hide()
            e.clickEvent(_this._option.info)
          }
        })
      }
    }
  }
  // 隐藏
  JM_ContextMenu.prototype.hide = function () {
    if (this._div) {
      var position = this._map.pointToOverlayPixel(this._default.point)
      this._div.style.left = position.x + 'px'
      this._div.style.top = position.y + 'px'
      this._div.style.display = 'none'
    }
  }
  // dom
  function createMemuHtml (option) {
    var html = '<ul>'
    if (option.menuItem.length) {
      option.menuItem.forEach(e => {
        html += '<li id="' + e.id + '">'
        if (JM_util.checkNull(e.iconClass)) {
          html += '<i class="' + e.iconClass + '"></i>'
        }
        html += e.label + '</li>'
      })
    }
    html += '</ul>'
    return html
  }
}
//
// JM_map=======================================================================
//
var JM_map = null
function assignBaseMap () {
  JM_map = function () {
    var newObj = {
      isMapLoad: true, // 控制判断是否是第一次加载完成地图
      baseMap: {},
      infoWin: {},
      icContextMenu: {},
      mapStyle: 'normal',
      viewMode: '2D',
      mapTypeNow: BMAP_NORMAL_MAP,
      option: {},
      Api: {
        TrafficControl: 'http://api.map.baidu.com/library/TrafficControl/1.4/src/TrafficControl_min.js', // 路况
        Heatmap: 'http://api.map.baidu.com/library/Heatmap/2.0/src/Heatmap_min.js', // 热力
        TextIconOverlay: 'http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js', // 点聚合
        MarkerClusterer: 'http://api.map.baidu.com/library/MarkerClusterer/1.2/src/MarkerClusterer_min.js', // 点聚合
        DrawingManager: 'http://api.map.baidu.com/library/DrawingManager/1.4/src/DrawingManager_min.js', // 鼠标绘制管理类
        CurveLine: 'http://api.map.baidu.com/library/CurveLine/1.5/src/CurveLine.min.js', // 弧线、椭圆
        LuShu: 'http://api.map.baidu.com/library/LuShu/1.2/src/LuShu_min.js', // 路书
        DistanceTool: 'http://api.map.baidu.com/library/DistanceTool/1.2/src/DistanceTool.js' // 测距工具
      },
      status: {
        edit: false,
        draw: false,
        measure: false,
        coordinate: false,
        cluster: false
      },
      DrawingType: null,
      modules: {
        boundary: {}, // 边界实例
        traffic: {}, // 路况实例
        heatMap: {}, // 热力实例
        cluster: {}, // 点聚合实例
        drawing: {}, // 绘图工具实例
        distanceTool: {}, // 测距工具
        diving: [], // 驾车实例数组
        lushu: [] // 路书实例
      },
      //
      // =========================== init ==============================
      //
      // 初始化地图
      init (opt) {
        let me = this

        this.option = JM_util.deepCopy(opt)
        // console.log(this.option)
        // 修改百度地图API里透明度设置0时无效的bug
        if (this.option.icBoundary.polygonStyle.strokeOpacity === 0) {
          this.option.icBoundary.polygonStyle.strokeOpacity = '0'
        }
        if (this.option.icBoundary.polygonStyle.fillOpacity === 0) {
          this.option.icBoundary.polygonStyle.fillOpacity = '0'
        }
        if (this.option.icBoundary.hover.polygonStyle.strokeOpacity === 0) {
          this.option.icBoundary.hover.polygonStyle.strokeOpacity = '0'
        }
        if (this.option.icBoundary.hover.polygonStyle.fillOpacity === 0) {
          this.option.icBoundary.hover.polygonStyle.fillOpacity = '0'
        }

        this.option.mapType = this.convertMapType(this.option.viewMode)
        this.baseMap = new BMap.Map(this.option.id, this.option)
        this.viewMode = this.option.viewMode
        this.DrawingType = this.option.icDrawControl.defaultType
        this.baseMap.setMapStyle({ style: this.option.mapStyle })
        this.baseMap.setCurrentCity(this.option.area) // 设置显示城市
        this.baseMap.centerAndZoom(JM_B_util.newPoint(this.option.center), this.option.zoom) // 设置中心点和显示级别
        this.baseMap.enableScrollWheelZoom(this.option.enableScrollWheelZoom) // 是否允许滚轮缩放地图
        // 是否允许拖拽
        if (this.option.enableDragging) {
          this.baseMap.enableDragging()
        } else {
          this.baseMap.disableDragging()
        }
        // 是否允许通过双击鼠标放大地图
        if (this.option.enableDoubleClickZoom) {
          this.baseMap.enableDoubleClickZoom()
        } else {
          this.baseMap.disableDoubleClickZoom()
        }
        // 设置地图显示范围
        if (JM_util.checkNull(this.option.extent)) {
          JM_B_util.loadJScript('http://api.map.baidu.com/library/AreaRestriction/1.2/src/AreaRestriction_min.js', function () { me.initExtent() })
        }

        // 初始化各组件
        this.initComponents()
        // 绑定事件
        this.bindEvents()
      },
      // 初始化各组件
      initComponents () {
        let me = this

        // 生成信息窗并隐藏
        this.initInfoWin()
        this.infoWin.hide()

        // 生成右键菜单并隐藏
        this.initContextMenu()
        // this.icContextMenu.hide()

        // 设置比例尺控件
        this.initScaleControl()
        // 设置缩放控件
        this.initNavigationControl()
        // 设置鹰眼(缩略图)控件
        this.initOverviewMapControl()

        // 添加绘制弧线类
        JM_B_util.loadJScript(this.Api.CurveLine)

        // 添加绘制管理类
        if (this.option.icDrawControl.isUsed) {
          JM_B_util.loadJScript(this.Api.DrawingManager, function () { me.newDrawModule() })
        }
        // 添加绘制管理类
        if (this.option.icDistanceTool.isUsed) {
          JM_B_util.loadJScript(this.Api.DistanceTool, function () { me.newDistanceTool() })
        }
        // 添加路书
        if (this.option.icLuShu.isUsed) {
          JM_B_util.loadJScript(this.Api.LuShu)
        }
        // 设置路况图层
        if (this.option.icToolBar.layout.includes('traffic')) {
          JM_B_util.loadJScript(this.Api.TrafficControl)
        }
        // 加载热力图层
        if (this.option.icHeatMap.isUsed) {
          JM_B_util.loadJScript(this.Api.Heatmap, function () { me.newHeatMapModule() })
        }
        // 加载点聚合图层
        if (this.option.icCluster.isUsed) {
          JM_B_util.loadJScript(this.Api.TextIconOverlay)
          JM_B_util.loadJScript(this.Api.MarkerClusterer, function () { me.newClusterModule() })
        }
        // 加载边界
        if (this.option.icBoundary.isUsed) {
          this.newBoundaryModule()
        }
      },
      // 绑定事件
      bindEvents () {
        let me = this
        // 地图第一次加载完成时执行
        this.baseMap.addEventListener('tilesloaded', function () {
          if (me.isMapLoad) {
            if (me.option.mapOnloadFunc) {
              me.option.mapOnloadFunc() // 地图组件内置mapOnloadFun
            }
            if (me.option.icMapOnloadFunc) {
              me.option.icMapOnloadFunc() // 用户自定义mapOnloadFun
            }
            me.isMapLoad = false
          }
        })

        // 地图单击事件
        this.baseMap.addEventListener('click', function (e) {
          // 拾取坐标
          if (me.status.coordinate) {
            me.option.listenerFunc('coordinate', e.point.lng + ',' + e.point.lat)
          }
        })

        // 地图右键菜单
        this.baseMap.addEventListener('rightclick', function (e) {
          if (me.icContextMenu.isTargetMap) {
            let info = {
              lng: e.point.lng,
              lat: e.point.lat,
              menuItem: me.option.icMapContextMenu.menuItem,
              info: '',
              customClass: me.option.icMapContextMenu.customClass
            }
            JM_B_util.showContextMenu(me.icContextMenu, info)
          }
          me.icContextMenu.isTargetMap = true
        })
      },
      // 初始化地图显示范围
      initExtent () {
        let border = new BMap.Bounds(JM_B_util.newPoint(this.option.extent[0]), JM_B_util.newPoint(this.option.extent[1]))
        try {
          BMapLib.AreaRestriction.setBounds(this.baseMap, border)
        } catch (e) {
          alert(e)
        }
      },
      // 初始化自定义信息窗口
      initInfoWin () {
        this.option.infoWinOption.point = JM_B_util.newPoint(this.option.infoWinOption.point)
        this.infoWin = new JM_InfoWin(this.option.infoWinOption)
        this.baseMap.addOverlay(this.infoWin)
      },
      // 初始化自定义右键菜单
      initContextMenu () {
        this.icContextMenu = new JM_ContextMenu(this.option.ContextMenuOption)
        this.icContextMenu.isTargetMap = true
        this.baseMap.addOverlay(this.icContextMenu)
      },
      // 设置比例尺控件
      initScaleControl () {
        let _scaleControl = this.option.scaleControl
        if (_scaleControl.show) {
          let param = {}
          param.anchor = JM_util.evil('BMAP_ANCHOR_' + _scaleControl.position[0] + '_' + _scaleControl.position[1])
          param.offset = { width: _scaleControl.x, height: _scaleControl.y }
          this.baseMap.addControl(new BMap.ScaleControl(param))
        }
      },
      // 设置缩放控件
      initNavigationControl () {
        let _navigationControl = this.option.navigationControl
        if (_navigationControl.show) {
          let param = {}
          param.anchor = JM_util.evil('BMAP_ANCHOR_' + _navigationControl.position[0] + '_' + _navigationControl.position[1])
          param.offset = { width: _navigationControl.x, height: _navigationControl.y }
          this.baseMap.addControl(new BMap.NavigationControl(param))
        }
      },
      // 设置鹰眼(缩略图)控件
      initOverviewMapControl () {
        let _overviewMapControl = this.option.overviewMapControl
        if (_overviewMapControl.show) {
          let param = {}
          param.isOpen = _overviewMapControl.open
          param.anchor = JM_util.evil('BMAP_ANCHOR_' + _overviewMapControl.position[0] + '_' + _overviewMapControl.position[1])
          param.offset = { width: _overviewMapControl.x, height: _overviewMapControl.y }
          this.baseMap.addControl(new BMap.OverviewMapControl(param))
        }
      },
      //
      // =========================== new Module ==============================
      //
      // 加载热力图层
      newHeatMapModule () {
        this.modules.heatMap = new BMapLib.HeatmapOverlay(this.option.icHeatMap.option)
        this.modules.heatMap.overlayType = 'heatMap'
        this.baseMap.addOverlay(this.modules.heatMap)
      },
      // 加载点聚合图层
      newClusterModule () {
        this.option.icCluster.styles[0].size = new BMap.Size(...this.option.icCluster.styles[0].size)
        this.option.icCluster.styles[0].offset = new BMap.Size(...this.option.icCluster.styles[0].offset)
        this.modules.cluster = new BMapLib.MarkerClusterer(this.baseMap, this.option.icCluster)
      },
      // 加载行政区域边界
      newBoundaryModule () {
        let me = this
        this.modules.boundary = new BMap.Boundary()
        let areas = this.option.icBoundary.area
        areas.forEach(area => {
          // 生成行政区
          this.modules.boundary.get(area, function (border) {
            let count = border.boundaries.length // 行政区域的块数
            if (count === 0) {
              alert('未能获取当前设置的行政区域，无法展示区域边界！')
            } else {
              let pointArray = []
              // 一个行政区可能包括地图上的两块无交集区域
              for (var i = 0; i < count; i++) {
                let polygon = me.createBoundary(border.boundaries[i], me.option.icBoundary)
                polygon.areaName = area
                me.baseMap.addOverlay(polygon)
                pointArray = pointArray.concat(polygon.getPath())
              }
            }
          })
        })
      },
      // 创建一个区域边界
      createBoundary (boundary, option) {
        let polygon = new BMap.Polygon(boundary, option.polygonStyle) // 建立多边形覆盖物

        // 添加鼠标滑上滑出事件
        if (option.hover.isUsed) {
          polygon.addEventListener('mouseover', function (e) {
            if (JM_util.checkNull(option.hover.func)) {
              option.hover.func(polygon.areaName)
            }
            JM_B_util.setPolygonStyle(e.target, option.hover.polygonStyle)
          })
          polygon.addEventListener('mouseout', function (e) {
            JM_B_util.setPolygonStyle(e.target, option.polygonStyle)
          })
        }
        polygon.overlayType = 'boundary'

        return polygon
      },
      //
      // =========================== GPS ==============================
      //
      // 获取两点间的驾车路线规划方案
      getDrivingPath (start, end, speed, isLuShu, pathType, specificStyle) {
        let me = this
        let id = JM_util.uuid()
        if (JM_util.checkNull(specificStyle)) {
          this.option.icDriving = JM_util.mergeObjectDeep(this.option.icDriving, specificStyle)
        }

        let length = this.modules.diving.length
        this.modules.diving[length] = {
          id: id,
          drivingRoute: [],
          path: []
        }
        let drivingObj = this.modules.diving[length]

        let onSearchCompleteFunc = function (results) {
          me.addPathPoints(me, results, id)
          drivingObj.path = me.addPathLine(me, results, id, pathType)
          if (speed) {
            if (isLuShu) {
              me.addLuShu(me, results, specificStyle)
            } else {
              me.addRunAction(me, results, id, speed)
            }
          }
        }

        let opts = []
        opts[0] = {
          policy: BMAP_DRIVING_POLICY_FIRST_HIGHWAYS
        }
        opts[1] = {
          policy: BMAP_DRIVING_POLICY_AVOID_HIGHWAYS
        }
        opts[2] = {
          policy: BMAP_DRIVING_POLICY_AVOID_CONGESTION
        }
        opts[3] = {
          policy: BMAP_DRIVING_POLICY_DEFAULT
        }
        for (let i = 0; i < 4; i++) {
          opts[i].onSearchComplete = onSearchCompleteFunc
          drivingObj.drivingRoute[i] = JM_B_util.newDrivingRoute(this.baseMap, opts[i])
        }

        let startPoint = JM_B_util.newPoint(start)
        let endPoint = JM_B_util.newPoint(end)
        switch (pathType) {
        case ('优先高速'):
          drivingObj.drivingRoute[0].search(startPoint, endPoint)
          break
        case ('避开高速'):
          drivingObj.drivingRoute[1].search(startPoint, endPoint)
          break
        case ('避开拥堵'):
          drivingObj.drivingRoute[2].search(startPoint, endPoint)
          break
        case ('all'):
          for (let i = 0; i < 4; i++) {
            drivingObj.drivingRoute[i].search(startPoint, endPoint)
          }
          break
        default:
          drivingObj.drivingRoute[3].search(startPoint, endPoint)
        }
      },
      // 添加起点，终点
      addPathPoints (me, results, id) {
        // console.log('.......')
        let startObj = {
          id: id + '_start',
          overlayType: 'marker',
          point: results.getStart().point,
          markerStyle: me.option.icDriving.startIcon,
          title: '起点'
        }
        let endObj = {
          id: id + '_end',
          overlayType: 'marker',
          point: results.getEnd().point,
          markerStyle: me.option.icDriving.endIcon,
          title: '终点'
        }
        let start = this.createOverlay(startObj, true)
        let end = this.createOverlay(endObj, true)
        start.drivingAttr = true
        end.drivingAttr = true
        me.baseMap.addOverlay(start)
        me.baseMap.addOverlay(end)
      },
      // 画出路线
      addPathLine (me, results, id, pathType) {
        let path = []
        let pathNum = 1
        if (pathType === 'all') {
          pathNum = results.getNumPlans()
        }
        // console.log(results.getNumPlans())
        for (let i = 0; i < pathNum; i++) {
          let pts = results.getPlan(i).getRoute(i).getPath()
          let pathObj = {
            id: id + '_line_' + JM_util.uuid(),
            overlayType: 'polyline',
            points: pts,
            polylineStyle: me.option.icDriving.pathStyle,
            hoverStyle: me.option.icDriving.pathHoverStyle,
            drivingAttr: true
          }
          path.push(pathObj)
          let line = this.createOverlay(pathObj)
          line.drivingAttr = true
          me.baseMap.addOverlay(line)
        }
        return path
      },
      // 添加运动图标
      addRunAction (me, results, id, speed) {
        let runObj = {
          id: id + '_run',
          overlayType: 'marker',
          point: results.getStart().point,
          markerStyle: me.option.icDriving.runIcon,
          title: me.option.icDriving.runIconTitle
        }
        let run = this.createOverlay(runObj, true)
        run.drivingAttr = true
        me.baseMap.addOverlay(run)

        let pts = results.getPlan(0).getRoute(0).getPath()
        me.baseMap.setViewport(pts) // 根据路线坐标设置地图视野

        pts.forEach((point, index) => {
          me.reSetPosition(run, point, index, speed)
        })
      },
      // 移动运动图标
      reSetPosition (runObj, point, index, speed) {
        setTimeout(function () {
          runObj.setPosition(point)
        }, speed * index)
      },
      // 添加路书
      addLuShu (me, results, specificStyle) {
        console.log(specificStyle)
        let opt = me.option.icLuShu
        if (JM_util.checkNull(specificStyle)) {
          opt = JM_util.mergeObjectDeep(me.option.icLuShu, specificStyle)
        }
        let pts = results.getPlan(0).getRoute(0).getPath()
        let length = me.modules.lushu.length
        me.modules.lushu[length] = JM_B_util.newLuShu(me.baseMap, pts, opt)
      },
      // 控制路书对象行为
      setLuShuAction (action) {
        switch (action) {
        case ('start'):
          this.modules.lushu.forEach(element => {
            element.start()
            element._marker.overlayType = 'marker'
            element._marker.drivingAttr = true
          })
          break
        case ('stop'):
          this.modules.lushu.forEach(element => {
            element.stop()
          })
          break
        case ('pause'):
          this.modules.lushu.forEach(element => {
            element.pause()
          })
          break
        case ('showInfoWindow'):
          this.modules.lushu.forEach(element => {
            element.showInfoWindow()
          })
          break
        case ('hideInfoWindow'):
          this.modules.lushu.forEach(element => {
            element.hideInfoWindow()
          })
          break
        }
      },
      // 清除地图上的所有行车路线
      clearDrivingPath () {
        let me = this
        let overlays = this.baseMap.getOverlays()
        overlays.forEach(e => {
          if (e.drivingAttr) {
            me.baseMap.removeOverlay(e)
          }
        })
        this.modules.diving = []
        this.modules.lushu = []
      },
      //
      // =========================== set ==============================
      //
      // 切换地图样式
      setMapStyle (style) {
        this.baseMap.setMapStyle({ style: style })
      },
      // 切换地图类型
      setMapType (viewMode) {
        if (viewMode === this.viewMode) {
          return
        }
        this.viewMode = viewMode
        this.baseMap.setMapType(this.convertMapType(viewMode))
      },
      convertMapType (viewMode) {
        switch (viewMode) {
        case ('2D'):
          return BMAP_NORMAL_MAP
        case ('3D'):
          return BMAP_PERSPECTIVE_MAP
        case ('卫星'):
          return BMAP_HYBRID_MAP
        case ('卫星无路网'):
          return BMAP_SATELLITE_MAP
        }
      },
      // 开关路况
      setTrafficControl (flag) {
        if (flag) {
          this.modules.traffic = new BMap.TrafficLayer()
          this.baseMap.addTileLayer(this.modules.traffic)
        } else {
          this.baseMap.removeTileLayer(this.modules.traffic)
        }
      },
      // 开关拾取坐标
      setCoordinateStatus (flag) {
        this.status.coordinate = flag
      },
      // 开启/关闭编辑模式
      setEditStatus (flag) {
        this.status.edit = flag
        let items = JM_B_util.getEditOverlays(this.baseMap)
        let editTypes = ['circle', 'polyline', 'polygon', 'rectangle']

        if (flag && !items.length) {
          return false
        }
        if (flag && items.length) {
          this.modules.drawing.close()
          items.forEach(e => {
            if (e.overlayType === 'marker') {
              e.enableDragging()
            }
            if (editTypes.includes(e.overlayType)) {
              e.enableEditing()
              e.isEditing = true
            }
          })
        } else if (!flag && items.length) {
          if (this.status.draw) {
            this.modules.drawing.open()
            this.modules.drawing.setDrawingMode(this.DrawingType)
          }

          let updateOverlays = []
          items.forEach(e => {
            if (e.overlayType === 'marker') {
              e.disableDragging()
            }
            if (editTypes.includes(e.overlayType)) {
              e.disableEditing()
              e.isEditing = false
              if (e.lineupdate) {
                updateOverlays.push(e)
              }
            }
          })
          if (updateOverlays.length) {
            return updateOverlays
          }
        }
        return true
      },
      //
      // =========================== DistanceTool ==============================
      //
      // 初始化测距工具
      newDistanceTool () {
        let me = this
        this.modules.distanceTool = new BMapLib.DistanceTool(this.baseMap, this.option.icDistanceTool)
        this.modules.distanceTool.addEventListener('drawend', function (e) {
          me.option.listenerFunc('DistanceTool', e)
        })
      },
      // 开启关闭测距工具
      setDistanceToolStatus (flag) {
        if (flag) {
          this.modules.distanceTool.open()
        } else {
          this.modules.distanceTool.close()
        }
      },
      //
      // =========================== draw ==============================
      //
      // 初始化绘图工具
      newDrawModule () {
        let markerOptions = {
          icon: JM_B_util.newIcon(this.option.markerStyle),
          offset: new BMap.Size(...[0, -0.5 * this.option.markerStyle.imageSize[1]])
        }
        let opts = {
          isOpen: false,
          drawingType: this.DrawingType,
          enableCalculate: false,
          markerOptions: markerOptions,
          circleOptions: this.option.circleStyle,
          polylineOptions: this.option.polylineStyle,
          polygonOptions: this.option.polygonStyle,
          rectangleOptions: this.option.polygonStyle
        }
        this.modules.drawing = new BMapLib.DrawingManager(this.baseMap, opts)
        this.bindCompleteEvent()
      },
      // 绑定绘图操作完成后的方法
      bindCompleteEvent () {
        let me = this
        let listeners = ['markercomplete', 'circlecomplete', 'polylinecomplete', 'polygoncomplete', 'rectanglecomplete']
        let overlayTypes = ['marker', 'circle', 'polyline', 'polygon', 'rectangle']
        listeners.forEach((element, index) => {
          me.modules.drawing.addEventListener(element, function (e, overlay) {
            let overlayType = overlayTypes[index]
            overlay.overlayType = overlayType
            overlay.edit = true
            overlay.drawing = true
            if (JM_util.checkNull(me.option.icDrawControl.completeFunc[overlayType])) {
              me.option.icDrawControl.completeFunc[overlayType](overlay)
            }
            // console.log(element)
            me.newDrawModule()
            me.setDrawStatus(true)
            // console.log(me.DrawingType)
          })
        })
      },
      // 开启/关闭绘图模式
      setDrawStatus (flag) {
        this.status.draw = flag
        if (flag) {
          this.modules.drawing.open()
          this.modules.drawing.setDrawingMode(this.DrawingType)
        } else {
          this.modules.drawing.close()
        }
      },
      // 绘图操作
      setDrawMode (drawType) {
        switch (drawType) {
        case ('marker'):
          this.DrawingType = BMAP_DRAWING_MARKER
          break
        case ('circle'):
          this.DrawingType = BMAP_DRAWING_CIRCLE
          break
        case ('polyline'):
          this.DrawingType = BMAP_DRAWING_POLYLINE
          break
        case ('polygon'):
          this.DrawingType = BMAP_DRAWING_POLYGON
          break
        case ('rectangle'):
          this.DrawingType = BMAP_DRAWING_RECTANGLE
          break
        case ('clear'):
          this.clearAllDraw()
          break
        }
        this.modules.drawing.setDrawingMode(this.DrawingType)
      },
      // 清除绘图内容
      clearAllDraw () {
        let me = this
        let overlays = this.baseMap.getOverlays()
        overlays.forEach(e => {
          if (e.drawing) {
            me.baseMap.removeOverlay(e)
          }
        })
      },
      //
      // =========================== show ==============================
      //
      // overlayArrey：数据集合，overlayType：数据类型，flag：显示/隐藏
      showOverlays (overlayArrey, overlayType, flag) {
        this.hideInfoWin()
        this.hideContextMenu()

        if (!this.checkRepeatOverlays(overlayArrey, overlayType)) {
          alert('覆盖物id是唯一标识，不可重复！')
          return
        }
        if (overlayType === 'heatMap') {
          this.showHeatMap(overlayArrey, flag)
        } else {
          this.showOtherOverlays(overlayArrey, overlayType, flag)
        }
      },
      // 校验传入的数据是否存在重复id
      checkRepeatOverlays (overlayArrey, overlayType) {
        // 热力图数据不需要id
        if (overlayType === 'heatMap') {
          return true
        }

        let idList = []
        overlayArrey.forEach(overlayObj => {
          idList.push(overlayObj.id)
        })
        return JM_util.checkRepeat(idList)
      },
      // 显示/隐藏热力图，refresh：是否刷新新力图数据，true/false
      showHeatMap (overlayArrey, flag, refresh) {
        if (flag) {
          if (!JM_util.checkNull(this.modules.heatMap.data) || refresh) {
            let max = overlayArrey[0].max
            this.modules.heatMap.setDataSet({ data: overlayArrey, max: max })
            this.modules.heatMap.show()
          } else {
            this.modules.heatMap.show()
          }
        } else {
          this.modules.heatMap.hide()
        }
      },
      // 显示/隐藏其他覆盖物(overlayArrey：数据集合，overlayType：数据类型，flag：显示/隐藏)
      showOtherOverlays (overlayArrey, overlayType, flag) {
        let me = this
        if (flag) {
          overlayArrey.forEach(overlayObj => {
            let overlay = me.createOverlay(overlayObj)
            if (me.option.icCluster.isUsed && overlayType === 'marker') {
              me.modules.cluster.addMarker(overlay)
            } else {
              me.baseMap.addOverlay(overlay)
              if (JM_util.checkNull(overlayObj.animationType) && overlayType === 'marker') {
                this.addAnimation(overlay, overlayObj.animationType)
              }
            }
          })
        } else {
          let idList = []
          overlayArrey.forEach(overlayObj => {
            idList.push(overlayObj.id)
          })
          if (me.option.icCluster.isUsed && overlayType === 'marker') {
            JM_B_util.clearClusterOverlays(me.modules.cluster, idList)
          } else {
            JM_B_util.clearOverlays(me.baseMap, idList)
          }
        }
      },
      // marker添加动画效果
      addAnimation (marker, type) {
        if (type === 'bounce') {
          marker.setAnimation(BMAP_ANIMATION_BOUNCE)
        } else {
          marker.setAnimation(BMAP_ANIMATION_DROP)
        }
      },
      //
      // =========================== create ==============================
      //
      // 创建覆盖物，overlayObj：覆盖物信息对象
      createOverlay (overlayObj) {
        this.uniqueOverlay(overlayObj.id) // 如果地图上已存在相同id覆盖物，执行更新操作，先删除再创建，

        let styleName = overlayObj.overlayType + 'Style'
        overlayObj[styleName] = JM_util.mergeObjectDeep(this.option[styleName], overlayObj[styleName] || {})
        overlayObj.normalStyle = overlayObj[styleName]
        if (overlayObj.hoverStyle) {
          overlayObj.hoverStyle = JM_util.mergeObjectDeep(overlayObj.normalStyle, overlayObj.hoverStyle)
        }
        overlayObj.infoWinStyle = JM_util.mergeObjectDeep(this.option.infoWinOption, overlayObj.infoWinStyle || {})

        let overlay = null
        switch (overlayObj.overlayType) {
        case ('marker'):
          overlay = JM_B_util.newMarker(overlayObj)
          break
        case ('label'):
          overlay = JM_B_util.newLabel(overlayObj)
          break
        case ('circle'):
          overlay = JM_B_util.newCircle(overlayObj)
          break
        case ('polyline'):
          overlay = JM_B_util.newpPolyline(overlayObj)
          break
        case ('polygon'):
          overlay = JM_B_util.newPolygon(overlayObj)
          break
        case ('AdvancedLabel'):
          overlayObj.point = JM_B_util.newPoint(overlayObj.center)
          overlay = new AdvancedLabel(overlayObj)
          break
        case ('CurveLine'):
          overlay = JM_B_util.newCurveLine(overlayObj)
          break
        }
        this.bindOverlayEvent(overlay, overlayObj)
        return overlay
      },
      // 去除地图已有覆盖物
      uniqueOverlay (id) {
        JM_B_util.clearOverlays(this.baseMap, [id])
        if (this.option.icCluster.isUsed) {
          JM_B_util.clearClusterOverlays(this.modules.cluster, [id])
        }
      },
      // 覆盖物绑定事件
      bindOverlayEvent (overlay, overlayObj) {
        let me = this

        // 鼠标滑上事件
        overlay.addEventListener('mouseover', function () {
          if (JM_util.checkNull(overlayObj.mouseoverEvent)) {
            overlayObj.mouseoverEvent(overlay.info)
          }
          if (JM_util.checkNull(overlayObj.hoverStyle)) {
            me.setOverlayStyle(overlayObj.id, overlayObj.hoverStyle)
          }
        })

        // 鼠标滑出事件
        overlay.addEventListener('mouseout', function () {
          if (JM_util.checkNull(overlayObj.mouseleaveEvent)) {
            overlayObj.mouseleaveEvent(overlay.info)
          }
          if (JM_util.checkNull(overlayObj.hoverStyle)) {
            me.setOverlayStyle(overlayObj.id, overlayObj.normalStyle)
          }
        })

        // 点击事件
        overlay.addEventListener('click', function () {
          // 是否禁默认点击事件
          if (!overlayObj.stopDefaultClickEvent) {
            $('#' + me.option.domId).attr('val', JSON.stringify(overlayObj))
            vpEventTarget.fire({type: 'change', id: me.option.domId, source: 'map', data: JSON.stringify(overlayObj)})
            me.centerAndOpenInfoWindow(overlayObj, overlayObj.infoWinStyle)
          }
          if (JM_util.checkNull(overlayObj.clickEvent)) {
            overlayObj.clickEvent(overlay.info)
          }
        })

        // 右键菜单
        overlay.addEventListener('rightclick', function (e) {
          me.icContextMenu.isTargetMap = false
          me.icContextMenu.hide()
          if (overlayObj.contextMenu && overlayObj.contextMenu.isUsed) {
            let info = {
              lng: overlayObj.lng,
              lat: overlayObj.lat,
              menuItem: overlayObj.contextMenu.menuItem,
              info: overlayObj,
              customClass: overlayObj.contextMenu.customClass
            }
            JM_B_util.showContextMenu(me.icContextMenu, info)
          }
        })

        // marker拖拽事件
        if (overlay.overlayType === 'marker') {
          overlay.addEventListener('dragend', function (e) {
            if (overlayObj.dragendEvent) {
              overlayObj.dragendEvent(e.point, overlay.info)
            } else {
              console.log('未定义拖拽后执行的方法')
            }
          })
        }

        // 覆盖物的属性发生变化时触发
        let lineupdateType = ['circle', 'polyline', 'polygon', 'rectangle']
        if (lineupdateType.includes(overlay.overlayType)) {
          overlay.addEventListener('lineupdate', function (e) {
            if (overlay.isEditing) {
              overlay.lineupdate = true
              // console.log(overlay)
            }
          })
        }
      },
      // 设置覆盖物样式
      setOverlayStyle (id, style) {
        let overlay = JM_B_util.getOverlayById(this.baseMap, id)
        let overlayType = overlay.overlayType
        switch (overlayType) {
        case ('marker'):
          let icon = JM_B_util.newIcon(style)
          overlay.setIcon(icon)
          break
        case ('circle'):
          JM_B_util.setPolygonStyle(overlay, style)
          break
        case ('polyline'):
          JM_B_util.setPolylineStyle(overlay, style)
          break
        case ('CurveLine'):
          JM_B_util.setPolylineStyle(overlay, style)
          break
        case ('polygon'):
          JM_B_util.setPolygonStyle(overlay, style)
          break
        case ('label'):
          overlay.setStyle(style)
          break
        }
      },
      //
      // =========================== clear ==============================
      //
      // 关闭信息窗
      hideInfoWin () {
        this.infoWin.hide()
      },
      // 关闭右键菜单
      hideContextMenu () {
        this.icContextMenu.hide()
      },
      // 清除全部覆盖物
      clearAllOverlays () {
        let me = this
        let removeTypes = ['marker', 'label', 'circle', 'polyline', 'polygon', 'rectangle', 'AdvancedLabel', 'CurveLine']
        let hideTypes = ['InfoWin', 'ContextMenu', 'heatMap']
        let overlays = this.baseMap.getOverlays()
        console.log(overlays)
        overlays.forEach(e => {
          if (removeTypes.includes(e.overlayType)) {
            me.baseMap.removeOverlay(e)
          }
          if (hideTypes.includes(e.overlayType)) {
            e.hide()
          }
        })
      },
      //
      // =========================== other ==============================
      //
      // 打开信息窗口并居中(覆盖物对象，用户传入信息窗口样式)
      centerAndOpenInfoWindow (obj, infoWinStyle) {
        JM_B_util.centerAndOpenInfoWindow(this.baseMap, this.infoWin, obj, infoWinStyle)
      },
      // 设置地图显示级别
      zoomToFunc (level) {
        this.baseMap.setZoom(level)
      },
      // 移动到定点，arr：[坐标数组]/point
      panToFunc (param) {
        let point = JM_util.isArray(param) ? JM_B_util.newPoint(param) : param
        this.baseMap.panTo(point)
      },
      // 通过ID获取覆盖物对象
      getOverlayById (id) {
        return JM_B_util.getOverlayById(this.baseMap, id)
      }
    }
    return newObj
  }
}
//
// IcJsMap=======================================================================
//
var IcJsMap = function () {
  var icMap = this
  icMap.newObj = {
    box: null,
    option: '',
    map: null,
    init: function (domId, _option) {
      if (typeof BMap === 'undefined') {
        // alert('当前地图组件只支持百度在线地图，内网PGIS地图功能正在开发，敬请期待！')
        return
      } else {
        initBUtil()
        assignInfoWin()
        assignContextMenu()
        assignBaseMap()
      }
      var defaultOption = JM_util.deepCopy(JM_option)
      this.option = JM_util.mergeObjectDeep(defaultOption, _option)
      this.option.domId = domId
      this.option.id = 'map_' + JM_util.uuid()

      JM_html.addMapDiv(domId, this.option.id)
      this.box = $('#' + domId + ' .ic-map-div')
      this.map = new JM_map()
      this.map.init(this.option)

      this.initComponents()
    },
    // 初始化各组件
    initComponents: function () {
      if (this.option.icMapTypeControl.show) {
        this.mapTypeControl.init()
      }
      if (this.option.icToolBar.show) {
        this.toolBar.init()
      }
      if (this.option.icDrawControl.isUsed) {
        this.drawBar.init()
      }
    },
    //
    // =========================== base func ==============================
    //
    // 自定义模块控制
    showCustomOverlays (overlayArrey, index, indexChild, isInit) {
      if (!isInit) {
        this.setting.icCustomBox[index].data[indexChild].active = !this.setting.icCustomBox[index].data[indexChild].active
        this.setting.icCustomBox = [...this.setting.icCustomBox]
      }
      overlayArrey.active = this.setting.icCustomBox[index].data[indexChild].active
      overlayArrey = this.setOverlayObjAttributes(overlayArrey)
      this.map.showOverlays(overlayArrey.data, overlayArrey.overlayType, overlayArrey.active)
    },
    // 设置路况
    setTrafficStatus (flag) {
      this.map.setTrafficControl(flag)
    },
    // 设置编辑状态
    setEditStatus (flag) {
      let overlayShow = this.map.setEditStatus(flag)
      if (!overlayShow) {
        alert('地图上不存在可编辑的覆盖物！')
        this.status.edit = false
      } else if (JM_util.isArray(overlayShow)) {
        this.$emit('getEditedOverlays', overlayShow)
      }
    },
    // 设置绘图状态
    setDrawStatus (flag) {
      this.map.setDrawStatus(flag)
    },
    // 设置测距工具
    setDistanceToolStatus (flag) {
      this.map.setDistanceToolStatus(flag)
    },
    // 设置点聚合功能
    setClusterStatus (flag) {
      this.map.setClusterStatus(flag)
    },
    // 把信息窗、右键菜单、拖拽事件等一类覆盖物的属性赋给每个覆盖物
    setOverlayObjAttributes (overlayArrey) {
      overlayArrey.data.forEach((overlayObj, index) => {
        overlayObj.overlayType = overlayArrey.overlayType
        overlayObj.infoWinStyle = overlayArrey.infoWinStyle
        overlayObj.contextMenu = overlayArrey.contextMenu
        overlayObj.dragendEvent = overlayArrey.dragendEvent
        overlayObj.stopDefaultClickEvent = !!overlayArrey.stopDefaultClickEvent
        overlayObj.clickEvent = overlayArrey.clickEvent
        overlayObj.dblclickEvent = overlayArrey.dblclickEvent
        overlayObj.mouseoverEvent = overlayArrey.mouseoverEvent
        overlayObj.mouseleaveEvent = overlayArrey.mouseleaveEvent

        if (overlayArrey.overlayType === 'heatMap') {
          overlayObj.max = overlayArrey.max
        } else {
          let styleName = overlayObj.overlayType + 'Style'
          overlayObj[styleName] = overlayArrey[styleName]
          overlayObj.hoverStyle = overlayArrey.hoverStyle
          if (overlayArrey.overlayType === 'marker') {
            overlayObj.animationType = overlayArrey.animationType
          }
        }
        if (['circle', 'polyline', 'polygon', 'rectangle'].includes(overlayArrey.overlayType)) {
          overlayObj.lng = overlayObj.center[0]
          overlayObj.lat = overlayObj.center[1]
        }
        if (overlayArrey.overlayType === 'AdvancedLabel') {
          let dom = document.getElementsByName('ic-advanced-label-item')[index]
          overlayObj.innerHTML = dom.outerHTML
        }
      })
      return overlayArrey
    },
    // 设置控件位置
    setControlPosition (option) {
      let position = option.position
      let style = { [position[0]]: option.y + 'px' }
      if (position[1] !== 'center') {
        style[position[1]] = option.x + 'px'
      }
      return style
    },
    // =========================== 外部方法 ==============================
    // 获取所有地图样式
    $_getAllMapStyles () {
      return JM_stylesB
    },
    // 切换地图样式
    $_setMapStyle (style) {
      this.map.setMapStyle(style)
    },
    // 切换地图类型
    $_onChangeMapType (viewMode) {
      this.mapTypeControl.changeType(viewMode)
    },
    // 移动地图中心点
    $_onPanTo (coordinate) {
      this.map.panToFunc(coordinate)
    },
    // 改变地图显示比例
    $_onZoomTo (level) {
      this.map.zoomToFunc(level)
    },
    // 路况
    $_setTrafficStatus (flag) {
      this.toolBar.setTraffic(flag)
    },
    // 开启关闭编辑状态
    $_setEditStatus (flag) {
      if (this.status.edit !== flag) {
        this.status.edit = flag
        this.setEditStatus(flag)
      }
    },
    // 开启关闭测距工具
    $_setDistanceToolStatus (flag) {
      if (this.status.distance !== flag) {
        this.status.distance = flag
        this.setDistanceToolStatus(flag)
      }
    },
    // 开启关闭绘图状态
    $_setDrawStatus (flag) {
      this.toolBar.setDraw(flag)
    },
    // 设置绘图类型
    $_setDrawMode (drawType) {
      this.map.setDrawMode(drawType)
      icMap.newObj.drawBar.btns.each(function () {
        if ($(this).attr('data-type') === drawType) {
          if (!$(this).hasClass('active')) {
            icMap.newObj.drawBar.btns.removeClass('active')
            $(this).addClass('active')
          }
        }
      })
    },
    // 开启关闭拾取坐标功能
    $_setCoordinateStatus (flag) {
      this.toolBar.setCoordinate(flag)
    },
    // 显示/隐藏覆盖物
    $_showOverlays (overlayArrey, flag) {
      overlayArrey = this.setOverlayObjAttributes(overlayArrey)
      this.map.showOverlays(overlayArrey.data, overlayArrey.overlayType, flag)
    },
    // 清除全部覆盖物
    $_clearAllOverlays () {
      let me = this
      let icCustomBox = me.option.icCustomBox
      if (JM_util.checkNull(icCustomBox)) {
        icCustomBox.forEach((box, index) => {
          box.data.forEach(overlayArrey => {
            overlayArrey.active = false
          })
        })
      }
      // this.clearSearchResults()
      if (this.toolBar.show) {
        this.$_setTrafficStatus(false)
      }
      if (JM_util.checkNull(this.editBar)) {
        this.$_setEditStatus(false)
      }
      this.map.clearAllOverlays()
    },
    // 通过ID获取覆盖物对象
    $_getOverlayByID (id) {
      return this.map.getOverlayById(id)
    },
    // 设置覆盖物样式
    $_setOverlayStyle (id, style) {
      this.map.setOverlayStyle(id, style)
    },
    // 打开信息窗口并居中
    $_centerAndOpenInfoWindow (obj, infoWinStyle) {
      this.map.centerAndOpenInfoWindow(obj, infoWinStyle)
    },
    // 获取两点间的驾车路线规划方案
    $_getDrivingPath (start, end, pathType, specificStyle) {
      this.map.getDrivingPath(start, end, 0, false, pathType, specificStyle)
    },
    // 获取两点间的行车路线，并沿路线运动
    $_getDrivingPathAndRun (start, end, speed, pathType, specificStyle) {
      this.map.getDrivingPath(start, end, speed, false, pathType, specificStyle)
    },
    // 获取两点间的行车路线，并创建路书对象
    $_getDrivingPathAndLuShu (start, end, pathType, specificStyle) {
      this.map.getDrivingPath(start, end, true, true, pathType, specificStyle)
    },
    // 控制路书对象行为
    $_addLuShuAction (action) {
      this.map.setLuShuAction(action)
    },
    // 清除地图上的所有行车路线
    $_clearDrivingPath () {
      this.map.clearDrivingPath()
    }
  }
  //
  // =========================== mapTypeControl ==============================
  //
  icMap.newObj.mapTypeControl = {
    viewMode: '2D',
    control: null,
    buttons: null,
    mark: null,
    init: function () {
      // console.log(444, icMap.newObj)
      JM_html.addMapTypeControl(icMap.newObj.box)
      this.control = icMap.newObj.box.find('.ic-map-type-control')
      this.buttons = icMap.newObj.box.find('.ic-map-type-control button')
      this.mark = icMap.newObj.box.find('.ic-map-type-control-mark')

      this.buttons.eq(0).addClass('active')
      this.mark.hide()
      this.bind()
    },
    bind: function () {
      var me = this
      this.buttons.click(function () {
        $('.ic-map-type-control button').removeClass('active')
        $(this).addClass('active')

        var viewMode = $(this).attr('viewMode')
        me.changeType(viewMode)
      })
      this.mark.click(function () {
        if ($(this).hasClass('select')) {
          $(this).removeClass('select')
          me.changeType('卫星无路网')
        } else {
          $(this).addClass('select')
          me.changeType('卫星')
        }
      })
    },
    changeType: function (viewMode) {
      if (this.viewMode === viewMode) {
        return
      }
      icMap.newObj.map.setMapType(viewMode)
      this.viewMode = viewMode
      if (['卫星', '卫星无路网'].includes(viewMode)) {
        this.mark.addClass('select').show()
      } else {
        this.mark.hide()
      }
    }
  }
  //
  // =========================== toolBar ==============================
  //
  icMap.newObj.toolBar = {
    bar: null,
    areaBtn: null,
    trafficBtn: null,
    editBtn: null,
    toolBoxBtn: null,
    toolList: null,
    coordinateBtn: null,
    drawBtn: null,
    distanceBtn: null,
    init: function () {
      JM_html.addToolBar(icMap.newObj.box, icMap.newObj.option.area)
      this.bar = icMap.newObj.box.find('.ic-map-toolbar')
      this.areaBtn = icMap.newObj.box.find('.ic-map-toolbar-btn').eq(0)
      this.trafficBtn = icMap.newObj.box.find('.ic-map-toolbar-btn').eq(1)
      this.editBtn = icMap.newObj.box.find('.ic-map-toolbar-btn').eq(2)
      this.toolBoxBtn = icMap.newObj.box.find('.ic-map-toolbar-btn').eq(3)
      this.toolList = icMap.newObj.box.find('.ic-map-toolbar-tool')
      this.coordinateBtn = icMap.newObj.box.find('.ic-map-toolbar-tool>span').eq(0)
      this.drawBtn = icMap.newObj.box.find('.ic-map-toolbar-tool>span').eq(1)
      this.distanceBtn = icMap.newObj.box.find('.ic-map-toolbar-tool>span').eq(2)

      this.toolList.hide()
      this.bind()
    },
    bind: function () {
      let me = this
      this.areaBtn.click(function () {
        icMap.newObj.map.panToFunc(icMap.newObj.option.center)
      })
      this.trafficBtn.click(function () {
        me.setTraffic()
      })
      this.editBtn.click(function () {
        icMap.newObj.map.panToFunc(icMap.newObj.option.center)
      })
      this.toolBoxBtn.click(function () {
        me.toolList.toggle()
      })
      this.coordinateBtn.click(function () {
        me.setCoordinate()
      })
      this.drawBtn.click(function () {
        me.setDraw()
      })
    },
    setTraffic: function (flag) {
      if (flag === this.trafficBtn.hasClass('active')) {
        return
      }
      this.trafficBtn.toggleClass('active', flag)
      icMap.newObj.map.setTrafficControl(this.trafficBtn.hasClass('active'))
    },
    setCoordinate: function (flag) {
      if (flag === this.coordinateBtn.hasClass('active')) {
        return
      }
      this.coordinateBtn.toggleClass('active', flag)
      icMap.newObj.map.setCoordinateStatus(this.coordinateBtn.hasClass('active'))
    },
    setDraw: function (flag) {
      if (flag === this.drawBtn.hasClass('active')) {
        return
      }
      icMap.newObj.drawBar.bar.toggle()
      this.drawBtn.toggleClass('active', flag)
      icMap.newObj.setDrawStatus(this.drawBtn.hasClass('active'))
    }
  }
  //
  // =========================== toolBar ==============================
  //
  icMap.newObj.drawBar = {
    bar: null,
    btns: null,
    init: function () {
      JM_html.addDrawBar(icMap.newObj.box, icMap.newObj.option.icDrawControl.type)
      this.bar = icMap.newObj.box.find('.ic-edit-control')
      this.btns = this.bar.find('button')
      this.btns.eq(0).addClass('active')
      this.bar.hide()
      this.bind()
    },
    bind: function () {
      let me = this
      this.btns.click(function () {
        var drawType = $(this).attr('data-type')
        if (drawType !== 'clear') {
          if (!$(this).hasClass('active')) {
            me.btns.removeClass('active')
            $(this).addClass('active')
          }
        }
        icMap.newObj.map.setDrawMode(drawType)
      })
    }

  }

  return icMap.newObj
}
//
// 导出=======================================================================
//
window.IcJsMap = IcJsMap
window.myMaps = {}
window.JM_stylesB = JM_stylesB
