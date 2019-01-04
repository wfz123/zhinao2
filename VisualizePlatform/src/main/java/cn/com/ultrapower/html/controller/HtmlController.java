package cn.com.ultrapower.html.controller;

import cn.com.ultrapower.html.service.HtmlService;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by admin on 2018/8/23.
 */
@Controller
@RequestMapping("html")
public class HtmlController {

    @Autowired
    private HtmlService htmlService;

    /**
     * 查询页面列表
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/queryHtmlInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryProInfo(@Param("queryStr") String queryStr,HttpServletRequest request) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        String encUsername = request.getHeader("X-Auth-Token");
        JSONObject json = htmlService.queryHtmlInfo(jsonMap,encUsername);
        return json;
    }

    /**
     *点击发布保存数据并生成页面
     * @param HtmlEle
     * @param request
     * @return
     */
    @RequestMapping(value = "/genHtml", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject genHtml(String HtmlEle, HttpServletRequest request,String groupId){
        JSONObject jsonObject = JSONObject.fromObject(HtmlEle);
        JSONObject json = htmlService.genHtml(jsonObject,request,groupId);
        return json;
    }

    /**
     * 根据页面ID进行查询页面
     * @param htmlStr
     * @return
     */
    @RequestMapping(value = "/queryHtmlById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryHtmlById(String htmlStr){
        JSONObject jsonObject = JSONObject.fromObject(htmlStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return htmlService.queryHtmlById(map);
    }

    /**
     * 生成页面ID或者根据页面id进行查询
     * @return
     */
    @RequestMapping(value = "/genOrQueryHtmlId", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject genHtmlId(String idStr){
        JSONObject jsonObject = JSONObject.fromObject(idStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        JSONObject js = htmlService.genOrQueryHtmlId(map);
        return js;
    }
    /**
     * 根据页面ID删除页面
     * @param idStr
     * @return
     */
    @RequestMapping(value = "/deleteHtmlById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject deleteHtmlById(String idStr,HttpServletRequest request){
        JSONObject jsonObject = JSONObject.fromObject(idStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return htmlService.deleteHtmlById(map,request);
    }

}
