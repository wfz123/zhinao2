package cn.com.ultrapower.user.controller;

import cn.com.ultrapower.user.service.BaseUserService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/baseUser")
public class BaseUserController {
    @Autowired
    private BaseUserService baseUserService;

    @RequestMapping(value = "/mergeUserData",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject saveOrUpdateUser(String paramStr){
        JSONObject obj = JSONObject.fromObject(paramStr);
        Map paramPam = (Map)JSONObject.toBean(obj,Map.class);
        return baseUserService.saveOrUpdateUserInfo("Demo",paramPam);
    }

    @RequestMapping(value = "/queryUserData",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject queryUserData(String whereStr){
        JSONObject obj = JSONObject.fromObject(whereStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return baseUserService.queryUserData(whereMap);
    }

    @RequestMapping(value = "/findUserById",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject queryUserById(String idStr){
        JSONObject obj = JSONObject.fromObject(idStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return baseUserService.findByUserId(whereMap);
    }

    @RequestMapping(value = "/delUserById",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject delUserById(String idStr){
        JSONObject obj = JSONObject.fromObject(idStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return baseUserService.delByUserId(whereMap);
    }

    @RequestMapping(value = "/batchDelUser",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject batchDelUser(String idStr){
        JSONObject obj = JSONObject.fromObject(idStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return baseUserService.batchDelUser(whereMap);
    }

}
