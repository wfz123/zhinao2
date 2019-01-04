package cn.com.ultrapower.login.service;

import cn.com.ultrapower.login.mapper.LoginMapper;
import cn.com.ultrapower.role.entity.BaseRoleRelationMenu;
import cn.com.ultrapower.role.mapper.BaseRoleMapper;
import cn.com.ultrapower.user.entity.BaseUserEntity;
import cn.com.ultrapower.utils.JointJsonResult;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginService {
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private BaseRoleMapper roleMapper;

    public BaseUserEntity queryUser(BaseUserEntity entity){
        return loginMapper.queryUser(entity);
    }
    public JSONObject queryMenuList(String loginnanme){
        BaseUserEntity loginUser = new BaseUserEntity();
        loginUser.setLoginName(loginnanme);
        BaseUserEntity user=loginMapper.queryUser(loginUser);
        String roleid=user.getRoleid();
        Map whereMap=new HashMap();
        whereMap.put("rolePid",roleid);
        if (whereMap==null||whereMap.get("rolePid")==null|| StringUtils.isBlank(whereMap.get("rolePid").toString()))
            return JointJsonResult.joinJson("failed", "param is null.");
        List<BaseRoleRelationMenu> roleMenus = roleMapper.queryRoleRelationMenutree(whereMap);
        Map jsonMap = new HashMap();
        if(roleMenus!=null){
            jsonMap.put("list",roleMenus);
        }
        return JointJsonResult.joinJson("success",jsonMap);
    }
}