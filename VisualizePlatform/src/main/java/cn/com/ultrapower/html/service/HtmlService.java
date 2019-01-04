package cn.com.ultrapower.html.service;

import cn.com.ultrapower.groupmanage.mapper.GroupmanageMapper;
import cn.com.ultrapower.groupmanage.service.GroupmanageService;
import cn.com.ultrapower.html.mapper.HtmlMapper;
import cn.com.ultrapower.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/8/23.
 */
@Service
public class HtmlService {

    @Autowired
    private HtmlMapper htmlMapper;
    @Autowired
    private GroupmanageMapper groupmanageMapper;

    /**
     * 生成html文件
     * @param jsonO
     * @param request
     * @return
     */
    public JSONObject genHtml(JSONObject jsonO,HttpServletRequest request,String groupId) {
        String basePath = PropertiesUtil.initProperties("config/config.properties").getProperty("requestUrl");
        /*String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
      */ if (jsonO == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        String action = jsonO.getString("action");
        String htmlId = jsonO.getString("id");
        //保存页面分组信息
        Map groupMap = new HashMap();
        groupMap.put("htmlId",htmlId);
        int number = groupmanageMapper.delRelationByHtmlId(groupMap);
        if(StringUtils.isNotBlank(groupId)) {
            groupMap.put("groupId", groupId);
            groupMap.put("pid", UUIDGenerator.getUUIDoffSpace());
            groupMap.put("createTime", System.currentTimeMillis() / 1000);
            number = groupmanageMapper.saveGrouprelationhtml(groupMap);
        }
        Map dataMap = new HashMap();
        //这后期改成已发布/未发布
        if(StringUtils.isNotBlank(action)&&action.equals("publish")) {
            //获取页面的组件内容
            JSONArray jsonArray = jsonO.getJSONArray("component");
            //获取页面的标题
            String title = jsonO.getString("title");
            StringBuffer stringBuffer = new StringBuffer("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <title>"+title+"</title>\n" +
                    //引入css样式文件
                    "<link href=\""+basePath+"view/css/vpPageView.css\" rel=\"stylesheet\" type=\"text/css\">"+
                    "<link href=\""+basePath+"view/icon_component/vp/iconfont.css\" rel=\"stylesheet\" type=\"text/css\">"+
                    "<link href=\""+basePath+"view/icon_component/ic/iconfont.css\" rel=\"stylesheet\" type=\"text/css\">"+
                    //"<link href=\"http://at.alicdn.com/t/font_822251_brz05ubrazb.css\" rel=\"stylesheet\" type=\"text/css\">"+
                    //"<link href=\"http://at.alicdn.com/t/font_631457_b2jv049wnjk.css\" rel=\"stylesheet\" type=\"text/css\">"+
                    //引入js文件
                    //"<script type=\"text/javascript\" src=\"http://api.map.baidu.com/api?v=3.0&ak=ICy36B65ubGVH61IIwmQp7eIxmqGAtxx\"></script>"+
                    "<script type=\"text/javascript\" src=\""+basePath+"view/js/jquery-1.11.1.js\"></script>"+
                    "<script type=\"text/javascript\" src=\""+basePath+"view/js/vpTree.js\"></script>"+
                    "<script type=\"text/javascript\" src=\""+basePath+"view/js/Ischart.js\"></script>"+
                    "<script type=\"text/javascript\" src=\""+basePath+"view/js/vpDatePicker.js\"></script>"+
                    "<script type=\"text/javascript\" src=\""+basePath+"view/js/vpJsMap.js\"></script>"+
                    "<script type=\"text/javascript\" src=\""+basePath+"view/js/viewComFunc.js\"></script>"+
                    "<script type=\"text/javascript\" src=\""+basePath+"view/js/echarts.min.js\"></script>"+
					"<script type=\"text/javascript\" src=\""+basePath+"view/js/vpCustormEvent.js\"></script>"+
                   // "<script type=\"text/javascript\" src=\"https://cdnjs.cloudflare.com/ajax/libs/echarts/4.1.0.rc2/echarts.min.js\"></script>"+
                    "</head>\n");
            String hoverStyle = jsonO.getString("hoverStyle");
            stringBuffer.append("<style>" + hoverStyle + "\n</style>\n<body>");
            String pageHeight = jsonO.getString("pageHeight");//页面高度
            String pageWidth = jsonO.getString("pageWidth");//页面宽度
            stringBuffer.append("<div class=\"vp-container\" style=\"height: "+pageHeight+";width:"+pageWidth+"\">\n");
            try {
                //对传递的页面数据进行解析
                this.genMainHtml(jsonArray, stringBuffer);

                boolean script = jsonO.containsKey("script");
                if (script) {
                    stringBuffer.append("<script type=\"text/javascript\">"+jsonO.getString("script")+"</script>");
                }
                stringBuffer.append("</div>\n</body>\n</html>");

                Map queryMap = new HashMap();
                queryMap.put("id", htmlId);
                Map queryReturnMap = htmlMapper.queryHtmlById(queryMap);
                if(queryReturnMap!=null&&queryReturnMap.size()>0) {
                    //删除页面数据并删除页面
                    Map delMap = new HashMap();
                    delMap.put("pid", htmlId);
                    this.deleteHtmlById(delMap,request);
                }
                Map filePathMap = this.genHtmlPath(stringBuffer.toString(),request,htmlId);//拼接页面主体
                //保存页面数据
                int num = this.saveHtmlContent(filePathMap,jsonO);
                dataMap.put("filePath", filePathMap.get("requestUrl"));
                dataMap.put("htmlId", htmlId);
            }
            catch (Exception ex){
                ex.printStackTrace();
                return JointJsonResult.joinJson("error", "request happen exception");
            }
        }
        //保存数据但不生成页面
        else {
            try {
                Map queryMap = new HashMap();
                queryMap.put("id", htmlId);
                Map queryReturnMap = htmlMapper.queryHtmlById(queryMap);
                Map delMap = new HashMap();
                if(queryReturnMap!=null&&queryReturnMap.size()>0) {
                    //删除页面数据并删除页面
                    delMap.put("pid", htmlId);
                    this.deleteHtmlById(delMap,request);
                }
                //保存页面数据
                int num = this.saveHtmlContent(delMap,jsonO);
                dataMap.put("save", num);
            }catch (Exception ex){
                ex.printStackTrace();
                return JointJsonResult.joinJson("error", "request happen exception");
            }
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 将页面内容保存到数据库
     * @param fileUrlMap
     * @param jsonO
     * @return
     */
    public int saveHtmlContent(Map fileUrlMap,JSONObject jsonO) {
        Map htmlMap = (Map) JSONObject.toBean(jsonO, Map.class);
        String businessComponents = String.valueOf(jsonO);
        htmlMap.put("businessComponents", businessComponents);
        htmlMap.put("pid",jsonO.get("id"));
        htmlMap.put("createTime", System.currentTimeMillis() / 1000);
        htmlMap.put("resolution",(jsonO.get("pageWidth")+"*"+jsonO.get("pageHeight")).replaceAll("px",""));
        if(fileUrlMap!=null&&fileUrlMap.size()>0) {
            htmlMap.put("storeUrl", fileUrlMap.get("storeUrl"));
            htmlMap.put("requestUrl", fileUrlMap.get("requestUrl"));
        }
        //TODO 获取当前登录的用户，后期需要修改
        htmlMap.put("publisher", "admin");
        int num = htmlMapper.saveHtmlContent(htmlMap);
        return num;
    }

    //对前端传递的参数进行递归拼接
    private String genMainHtml(JSONArray jsonArray,StringBuffer stringBuffer) {
        StringBuffer listScript = new StringBuffer();
        //对传递的页面数据进行解析
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            //标签的类型放入到tag标签中
            boolean tag = jsonObject.containsKey("tag");
            String type = jsonObject.getString("type");
            if (tag) {
                stringBuffer.append("<" + jsonObject.getString("tag"));
            }
            //组件的id
            boolean componentId = jsonObject.containsKey("id");
            String componentIdStr = "";
            if (componentId) {
                componentIdStr = jsonObject.getString("id");
                stringBuffer.append(" id=\"" +componentIdStr+"\"");
            }
            if("icon".equals(type)) stringBuffer.append(" class=\"vp-componnent vp-"+type+" iconfont "+jsonObject.getString("className")+"\"");
            else if("table".equals(type)) {
                if(jsonObject.containsKey("instyle")) {
                    stringBuffer.append(" instyle=\'"+jsonObject.getString("instyle")+"\'");
                    System.out.println(jsonObject.getString("instyle")+"12345678956789");
                }
                stringBuffer.append(" class=\"vp-componnent vp-"+type+"\"");
            }
            else stringBuffer.append(" class=\"vp-componnent vp-"+type+"\"");
            boolean style = jsonObject.containsKey("style");
            if(style) {
                stringBuffer.append(" style=\"" + jsonObject.getString("style"));
                if(jsonObject.containsKey("displayNone")) {
                    Boolean displayNone = jsonObject.getBoolean("displayNone");
                    if (displayNone == true) {
                        stringBuffer.append(";display:none;");
                    }
                }
                stringBuffer.append("\"");
                boolean attr = jsonObject.containsKey("attr");
                if(attr) {
                    JSONObject json = jsonObject.getJSONObject("attr");
                    Iterator<String> keys = json.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        stringBuffer.append(" " + key + "=\"" + json.get(key)+"\"");
                    }
                }
            }
            stringBuffer.append(">");
            boolean text = jsonObject.containsKey("text");
            if (text) {stringBuffer.append(jsonObject.getString("text"));}
            boolean chrildren = jsonObject.containsKey("chrildren");
            if(chrildren){genMainHtml(jsonObject.getJSONArray("chrildren"), stringBuffer);}//递归调用
            stringBuffer.append("</"+jsonObject.getString("tag")+">\n");
            if("list".equals(type)) {listScript.append("pvCreatList('" + componentIdStr + "'," +jsonObject.getString("dom")+"); ");}//拼接页面引入
            else if("radio".equals(type)) {listScript.append("vpCreateRadio('" + componentIdStr + "'," +jsonObject.getString("dom")+"); ");}//拼接页面引入
            else if("checkbox".equals(type)) {listScript.append("vpCreateCheckBox('" + componentIdStr + "'," +jsonObject.getString("dom")+"); ");}//拼接页面引入
            else if("select".equals(type)) {listScript.append("vpCreateSelect('" + componentIdStr + "'," +jsonObject.getString("dom")+"); ");}//拼接页面引入
            else if("timer".equals(type)) {listScript.append("vpCreateDatePicker('" + componentIdStr + "');");}//拼接页面引入
            else if("map".equals(type)) {listScript.append("vpMapInitFunc('" +componentIdStr+ "'," +jsonObject.getString("dom")+"); ");}//拼接页面引入
            else if("tree".equals(type)) {listScript.append("vpCreateTree('" + componentIdStr + "',null);");}//拼接页面引入
        }
        stringBuffer.append("<script>").append(listScript).append("</script>");
        return stringBuffer.toString();
    }

    /**
     * 将文件生成服务器路径
     * @param htmlStr
     * @param request
     * @param htmlId
     * @return
     */
    private  Map genHtmlPath(String htmlStr,HttpServletRequest request,String htmlId) {
        Map map = new HashMap();
        String webappPath = request.getSession().getServletContext().getRealPath("file");
        String targetServerUrl = PropertiesUtil.initProperties("config/config.properties").getProperty("targetServerUrl");
        String storeHtmlUrl = PropertiesUtil.initProperties("config/config.properties").getProperty("storeHtmlUrl");
        String requestUrl = targetServerUrl+ htmlId + ".html";
        //整合工程目录
        String storeUrl = storeHtmlUrl+htmlId + ".html";
        //本工程目录
        //String storeUrl = webappPath+ "\\"+htmlId + ".html";
        map.put("storeUrl", storeUrl);
        OutputStreamWriter oStreamWriter = null;
        try {
            oStreamWriter = new OutputStreamWriter(new FileOutputStream(storeUrl), "utf-8");
            oStreamWriter.append(htmlStr);
            oStreamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (oStreamWriter!=null) {
                try {
                    oStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        map.put("requestUrl", requestUrl);
        return map;
    }
    /**
     * 根据id查询页面
     * @param map
     * @return
     */
    public JSONObject queryHtmlById(Map map) {
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        try {
            dataMap = htmlMapper.queryHtmlById(map);
            return JointJsonResult.joinJson("success",dataMap);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }

    }
    /**
     * 根据id删除页面
     * @param map
     * @return
     */
    public JSONObject deleteHtmlById(Map map,HttpServletRequest request) {
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        int num = 0;
        try {
            num = htmlMapper.deleteHtmlById(map);
            boolean flag = deleHtmlByHtmlId(request, (String) map.get("pid"));
            Map dataMap = new HashMap();
            if(flag&&num>0) {
                dataMap.put("flag","删除成功");
            }
            return JointJsonResult.joinJson("success",dataMap);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }

    }


    /**
     * 将生成的页面进行删除
     * @param request
     * @param htmlId
     * @return
     */
    public boolean deleHtmlByHtmlId(HttpServletRequest request,String htmlId) {
        String webappPath = request.getSession().getServletContext().getRealPath("file");
        String storeHtmlUrl = PropertiesUtil.initProperties("config/config.properties").getProperty("storeHtmlUrl");
        //工程整合目录
        String path = storeHtmlUrl+ htmlId + ".html";
        //本工程目录
        //String path = webappPath+"\\"+ htmlId + ".html";
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                //文件不存在
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    /**
     * 生成页面ID或者根据页面ID进行查询
     * @param map
     * @return
     */
    public JSONObject genOrQueryHtmlId(Map map) {
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        try {
            String id = (String) map.get("id");
            if (StringUtils.isBlank(id)) {
                id = UUIDGenerator.getUUIDoffSpace();
                dataMap.put("id",id);
            }
            else {
                dataMap = htmlMapper.queryHtmlById(map);
            }
            return JointJsonResult.joinJson("success",dataMap);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }

    }

    /**
     * 查询列表
     * @param queryMap
     * @return
     */
    public JSONObject queryHtmlInfo(Map queryMap,String encUsername) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        CryptUtils cryp = CryptUtils.getInstance();
        String username = cryp.decode(encUsername);
        queryMap.put("promulgator", username);
        if(!"".equals(queryMap.get("page"))&&!"".equals(queryMap.get("pagesize"))) {
            queryMap = PageUtils.getPageInfo(queryMap);
        }
        List<Map> list = htmlMapper.queryHtmlList(queryMap);
        int num = htmlMapper.queryHtmlCount(queryMap);
        Map dataMap = new HashMap();
        //if (list != null && list.size() > 0) {
            dataMap.put("num", num);
            dataMap.put("list", list);
        //}
        return JointJsonResult.joinJson("success", dataMap);
    }

    //main方法
    public static void main(String[] args) {
       /* writeFile("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body><div class=\"vp-componnent\"><input id=\"29e9f02e-f3cf-4ece-8746-98c468f1d0a1\" style=\"z-index: 10; top: 250px; left: 660px;\" type=\"text\"</input><button id=\"8cc3daf5-f2d1-4286-8faa-e0ac17d8d46b\" style=\"z-index: 11; top: 240px; left: 60px;\"</button><table id=\"33149f14-4abc-4195-8a8a-608be6fef6bc\" style=\"z-index: 12; top: 90px; left: 160px;\"</table></div></body>\n" +
                "</html>", "aaa");*/
        //Map data = null;
        //System.out.println(JointJsonResult.joinJson("success",data));
        String type = "icontest";
        String result = " class=\"vp-componnent vp-" + type + " iconfont" + type + "\"";
        System.out.println(result);
        String str = "[{\n" +
                "\t\t\"attr\": {\n" +
                "\t\t\t\"type\": \"text\"\n" +
                "\t\t},\n" +
                "\t\t\"id\": \"29e9f02e-f3cf-4ece-8746-98c468f1d0a1\",\n" +
                "\t\t\"label\": \"文本框\",\n" +
                "\t\t\"left\": \"0px\",\n" +
                "\t\t\"style\": \"z-index: 10; top: 250px; left: 660px;\",\n" +
                "\t\t\"top\": \"0px\",\n" +
                "\t\t\"type\": \"input\",\n" +
                "\t\t\"tag\": \"input\",\n" +
                "\t\t\"zIndex\": 10,\n" +
                "\t\t\"chrildren\": [{\n" +
                "\t\t\t\t\"attr\": {\n" +
                "\t\t\t\t\t\"type\": \"text\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"id\": \"29e9f02e-f3cf-4ece-8746-98c468f1d0a1\",\n" +
                "\t\t\t\t\"label\": \"文本框\",\n" +
                "\t\t\t\t\"left\": \"0px\",\n" +
                "\t\t\t\t\"style\": \"z-index: 10; top: 250px; left: 660px;\",\n" +
                "\t\t\t\t\"top\": \"0px\",\n" +
                "\t\t\t\t\"type\": \"input\",\n" +
                "\t\t\t\t\"tag\": \"input\",\n" +
                "\t\t\t\t\"zIndex\": 10\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"id\": \"8cc3daf5-f2d1-4286-8faa-e0ac17d8d46b\",\n" +
                "\t\t\t\t\"label\": \"按钮\",\n" +
                "\t\t\t\t\"left\": \"0px\",\n" +
                "\t\t\t\t\"style\": \"z-index: 11; top: 240px; left: 60px;\",\n" +
                "\t\t\t\t\"top\": \"0px\",\n" +
                "\t\t\t\t\"type\": \"button\",\n" +
                "\t\t\t\t\"tag\": \"button\",\n" +
                "\t\t\t\t\"zIndex\": 11,\n" +
                "\t\t\t\t\"text\": \"按钮\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8cc3daf5-f2d1-4286-8faa-e0ac17d8d46b\",\n" +
                "\t\t\"label\": \"按钮\",\n" +
                "\t\t\"left\": \"0px\",\n" +
                "\t\t\"style\": \"z-index: 11; top: 240px; left: 60px;\",\n" +
                "\t\t\"top\": \"0px\",\n" +
                "\t\t\"type\": \"button\",\n" +
                "\t\t\"tag\": \"button\",\n" +
                "\t\t\"zIndex\": 11,\n" +
                "\t\t\"text\": \"按钮\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"33149f14-4abc-4195-8a8a-608be6fef6bc\",\n" +
                "\t\t\"label\": \"表格\",\n" +
                "\t\t\"left\": \"0px\",\n" +
                "\t\t\"style\": \"z-index: 12; top: 90px; left: 160px;\",\n" +
                "\t\t\"top\": \"0px\",\n" +
                "\t\t\"type\": \"table\",\n" +
                "\t\t\"tag\": \"table\",\n" +
                "\t\t\"zIndex\": 12\n" +
                "\t}\n" +
                "]";
        JSONArray jsonArray = JSONArray.fromObject(str);
        StringBuffer stringBuffer = new StringBuffer();
        /*String b = genMainHtml(jsonArray, stringBuffer);
        System.out.println(b);*/


    }
}
