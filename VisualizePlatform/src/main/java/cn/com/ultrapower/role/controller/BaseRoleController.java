package cn.com.ultrapower.role.controller;

import cn.com.ultrapower.role.entity.BaseRoleRelationMenu;
import cn.com.ultrapower.role.service.BaseRoleService;
import cn.com.ultrapower.user.entity.BaseUserEntity;
import cn.com.ultrapower.user.service.BaseUserService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/baseRole")
public class BaseRoleController {
    @Autowired
    private BaseRoleService roleService;

    @Autowired
    private BaseUserService baseUserService;

    @RequestMapping(value = "/mergeRoleData",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject saveOrUpdateRole(String paramStr){
        JSONObject obj = JSONObject.fromObject(paramStr);
        Map paramPam = (Map)JSONObject.toBean(obj,Map.class);
        return roleService.saveOrUpdateRoleInfo("Demo",paramPam);
    }

    @RequestMapping(value = "/queryRoleData",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject queryRoleData(String whereStr){
        JSONObject obj = JSONObject.fromObject(whereStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return roleService.queryRoleData(whereMap);
    }

    @RequestMapping(value = "/findRoleById",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject queryRoleById(String idStr){
        JSONObject obj = JSONObject.fromObject(idStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return roleService.findByRoleId(whereMap);
    }

    @RequestMapping(value = "/delRoleById",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject delRoleById(String idStr){
        JSONObject obj = JSONObject.fromObject(idStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return roleService.delByRoleId(whereMap);
    }

    @RequestMapping(value = "/queryRoleMenu",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject queryRoleMenu(String whereStr){
        JSONObject obj = JSONObject.fromObject(whereStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return roleService.queryRoleRelationMenutree(whereMap);
    }


    //批量添加
    @RequestMapping(value = "/saveRoleRelationMenu",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject saveRoleRelationMenu(String paramStr){
        JSONObject obj = JSONObject.fromObject(paramStr);
        BaseRoleRelationMenu roleMenu = (BaseRoleRelationMenu)JSONObject.toBean(obj,BaseRoleRelationMenu.class);
        return roleService.saveRoleRelationMenuS("Demo",roleMenu);
    }

    //批量删除
    @RequestMapping(value = "/delRoleMenuList",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject delRoleMenuList(String whereStr){
        JSONObject obj = JSONObject.fromObject(whereStr);
        Map whereMap = (Map)JSONObject.toBean(obj,Map.class);
        return roleService.delRoleRelationMenuS(whereMap);
    }

    /**
     * 角色所在的人员
     * @param userStr
     * @return
     */
    @RequestMapping(value = "/queryUserPeoCount",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject queryUserPeoCount(String userStr){
        JSONObject object = JSONObject.fromObject(userStr);
        Map map = (Map) JSONObject.toBean(object,Map.class);
        return roleService.queryRoleToPeo(map);
    }


    /**
     * 批量移除角色中所在的人员的信息(或移除一个)
     * @param delListStr
     * @return
     */
    @RequestMapping(value = "/delRoleToPeoList",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JSONObject delRoleToPeoList(String delListStr){
        JSONObject jsonObject = JSONObject.fromObject(delListStr);
        BaseUserEntity baseUserEntity = (BaseUserEntity)JSONObject.toBean(jsonObject,BaseUserEntity.class);
        return baseUserService.delRoleToUserList(baseUserEntity);
    }
}
