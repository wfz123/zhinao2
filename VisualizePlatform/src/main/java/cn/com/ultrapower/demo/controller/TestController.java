package cn.com.ultrapower.demo.controller;

import cn.com.ultrapower.demo.service.TestService;
import cn.com.ultrapower.utils.StringUtils;
import net.sf.json.JSONObject;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by admin on 2018/8/22.
 */
@Controller
@RequestMapping("test")
public class TestController {
    @Autowired
    private TestService testService;

    /**
     * post方式联调
     * @param queryStr
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryList(String queryStr,HttpServletRequest request){
        /*String queryStr = "{'page':'1','pagesize':'5'}";*/
        if(StringUtils.isNotBlank(queryStr)) {
            System.out.println(queryStr);
        }
        //获取前端传递的token
        System.out.println(request.getHeader("X-Auth-Token"));
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        JSONObject json = testService.queryList(jsonMap);
        System.out.println("方法执行！！！！");
        return json;
    }

    /**
     * get方式联调
     * @param queryStr
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryListByGet", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryList2(String queryStr, HttpServletRequest request){
        /*String queryStr = "{'page':'1','pagesize':'5'}";*/
        if(StringUtils.isNotBlank(queryStr)) {
            System.out.println(queryStr);
        }
        //获取前端传递的token
        System.out.println(request.getHeader("X-Auth-Token"));
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        JSONObject json = testService.queryList(jsonMap);
        System.out.println("方法执行！！！！");
        return json;
    }

}
