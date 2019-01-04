package cn.com.ultrapower.role.service;

import cn.com.ultrapower.role.entity.BaseRoleEntity;
import cn.com.ultrapower.role.entity.BaseRoleRelationMenu;
import cn.com.ultrapower.role.mapper.BaseRoleMapper;
import cn.com.ultrapower.utils.JointJsonResult;

import cn.com.ultrapower.user.entity.BaseUserEntity;
import cn.com.ultrapower.user.mapper.BaseUserMapper;
import cn.com.ultrapower.utils.UUIDGenerator;
import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BaseRoleService {
    @Autowired
    private BaseRoleMapper roleMapper;

    @Autowired
    private BaseUserMapper userMapper;


    /**
     * 保存或修改角色数据
     * @param createUser
     * @param paramMap
     * @return
     */
    public JSONObject saveOrUpdateRoleInfo(String createUser, Map paramMap){
        if(paramMap==null||paramMap.size()==0)
            return JointJsonResult.joinJson("failed","param is null.");
        int i = 0;
        try {
            long time = System.currentTimeMillis()/1000;
            paramMap.put("lastModifier",createUser);
            paramMap.put("lastModifyTime",time);
            paramMap.put("defineType",1);
            if(paramMap.get("pid")!=null&&!StringUtils.isBlank(paramMap.get("pid").toString())){
                i = roleMapper.updateRoleInfo(paramMap);
            }else {
                String pid = UUIDGenerator.getUUIDoffSpace();
                paramMap.put("pid",pid);
                paramMap.put("creater",createUser);
                paramMap.put("createTime",time);
                i = roleMapper.saveRoleInfo(paramMap);
            }
        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }

        return JointJsonResult.joinJson("success",i>0?"ok":"error");
    }

    /**
     * 查询数据方法
     * @param whereMap
     * @return
     */
    public JSONObject queryRoleData(Map whereMap){
        if (whereMap==null)
            return JointJsonResult.joinJson("failed","param is null.");
        Map jsonMap = new HashMap();
        try {
            List<BaseRoleEntity> roles = roleMapper.queryRoleData(whereMap);
            if(roles!=null){
                jsonMap.put("list",roles);
            }
        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }
        return JointJsonResult.joinJson("success",jsonMap);
    }

    /**
     * 根据ID查询数据
     * @param whereMap
     * @return
     */
    public JSONObject findByRoleId(Map whereMap){
        if (whereMap==null||whereMap.get("pid")==null||StringUtils.isBlank(whereMap.get("pid").toString()))
            return JointJsonResult.joinJson("failed","param is null.");
        List<BaseRoleEntity> roles = roleMapper.queryRoleData(whereMap);
        if(roles!=null&&roles.size()>0)
            return JointJsonResult.joinJson("success",roles.get(0));
        else
            return JointJsonResult.joinJson("failed","not found data by id.");
    }

    /**
     * 根据ID删除角色
     * @param whereMap
     * @return
     */
    public JSONObject delByRoleId(Map whereMap){
        if (whereMap==null||whereMap.get("pid")==null||StringUtils.isBlank(whereMap.get("pid").toString()))
            return JointJsonResult.joinJson("failed","param is null.");
        int i = 0;
        try {
            i = roleMapper.queryRoleRelationUserCount(whereMap);
            if (i==0){
                 i = roleMapper.deleteRoleInfo(whereMap);
            }else {
                List<BaseUserEntity> userList = new ArrayList<>();
                List<BaseUserEntity> list = roleMapper.queryRoleToPeo(whereMap);
                for (BaseUserEntity baseUserEntity : list){
                    String [] ids = baseUserEntity.getPid().split(",");
                    for (int a = 0;a<ids.length;a++){
                        BaseUserEntity userEntity = new BaseUserEntity();
                        userEntity.setRoleid("");
                        userEntity.setRoleName("");
                        userEntity.setPid(ids[a]);
                        userList.add(userEntity);
                    }

                }
                int num = userMapper.updateRoleToUserList(userList);
                i = roleMapper.deleteRoleInfo(whereMap);
            }

        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request error");
        }
        return JointJsonResult.joinJson("success",i>0?"ok":"error");
    }

    /**
     * 根据角色ID查询菜单操作权限
     * @param whereMap
     * @return
     */
    public JSONObject queryRoleRelationMenutree(Map whereMap){
        if (whereMap==null||whereMap.get("rolePid")==null||StringUtils.isBlank(whereMap.get("rolePid").toString()))
            return JointJsonResult.joinJson("failed","param is null.");
        Map jsonMap = new HashMap();
        try {
            List<BaseRoleRelationMenu> roleMenus = roleMapper.queryRoleRelationMenutree(whereMap);
            if(roleMenus!=null){
                jsonMap.put("list",roleMenus);
            }
        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }

        return JointJsonResult.joinJson("success",jsonMap);
    }

    /**
     * 新增角色菜单关联数据(批量)
     * @param userName
     * @param roleMenu
     * @return
     */
    public JSONObject saveRoleRelationMenuS(String userName,BaseRoleRelationMenu roleMenu){
        if (roleMenu==null)
            return JointJsonResult.joinJson("failed","param is null.");
        int a = 0;
        try {
            List<BaseRoleRelationMenu> list = new ArrayList<BaseRoleRelationMenu>();
            String time = System.currentTimeMillis()/1000+"";
            String [] ids = roleMenu.getMenuPid().split(",");
            for (int i = 0;i< ids.length;i++){
                BaseRoleRelationMenu baseRoleRelationMenu = new BaseRoleRelationMenu();
                baseRoleRelationMenu.setCreater(userName);
                baseRoleRelationMenu.setCreateTime(time);
                baseRoleRelationMenu.setLastModifyTime(time);
                baseRoleRelationMenu.setLastModifier(userName);
                baseRoleRelationMenu.setRolePid(roleMenu.getRolePid());
                baseRoleRelationMenu.setRoleMenuPid(UUIDGenerator.getUUIDoffSpace());
                baseRoleRelationMenu.setMenuPid(ids[i]);
                list.add(baseRoleRelationMenu);
            }
            a = roleMapper.saveRoleRelationMenuList(list);
        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }

        return JointJsonResult.joinJson("success",a>0?"ok":"error");
    }

    /**
     * 删除角色菜单关联数据(批量删除)
     * @param whereMap
     * @return
     */
    public JSONObject delRoleRelationMenuS(Map whereMap){
        if (whereMap==null||whereMap.get("menuPid")==null||StringUtils.isBlank(whereMap.get("menuPid").toString())||
                whereMap==null||whereMap.get("rolePid")==null||StringUtils.isBlank(whereMap.get("rolePid").toString()))
            return JointJsonResult.joinJson("failed","param is null.");
        int a = 0;
        try {
            String [] mpids = whereMap.get("menuPid").toString().split(",");
            List list = Arrays.asList(mpids);
            whereMap.put("menuPid",list);
            a = roleMapper.delRoleRelationMenuS(whereMap);
        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }

        return JointJsonResult.joinJson("success",a>0?"ok":"error");
    }

    /**
     * 角色中所在的人员
     * @param whereMap
     * @return
     */
    public JSONObject queryRoleToPeo(Map whereMap){
        if(whereMap == null){
            return JointJsonResult.joinJson("failed","paramter is null");
        }
        Map jsonMap = new HashMap();
        try {
            String pageSize = whereMap.get("pagesize") != null ? whereMap.get("pagesize").toString() : "0";
            String pageNum = whereMap.get("page") != null ? whereMap.get("page").toString():"0";
            int pagesize = Integer.parseInt(pageSize);
            int page = (Integer.parseInt(pageNum) - 1 ) * pagesize;
            whereMap.put("pagesize",pagesize);
            whereMap.put("page",page);
            List<BaseUserEntity> userEntities = roleMapper.queryRoleToPeo(whereMap);
            int count = roleMapper.queryUserPeoCount(whereMap);
            if(userEntities!=null){
                jsonMap.put("num",count);
                jsonMap.put("list",userEntities);
            }
        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }
        return JointJsonResult.joinJson("success",jsonMap);
    }

}
