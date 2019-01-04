package cn.com.ultrapower.user.service;

import cn.com.ultrapower.user.entity.BaseUserEntity;
import cn.com.ultrapower.user.mapper.BaseUserMapper;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.CryptUtils;
import cn.com.ultrapower.utils.UUIDGenerator;
import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BaseUserService {
    @Autowired
    private BaseUserMapper baseUserMapper;

    /**
     * 保存或修改角色数据
     * @param createUser
     * @param paramMap
     * @return
     */
    public JSONObject saveOrUpdateUserInfo(String createUser, Map paramMap){
        if(paramMap==null||paramMap.size()==0)
            return JointJsonResult.joinJson("failed","param is null.");
        int i = 0;
        long time = System.currentTimeMillis()/1000;
        try{
            paramMap.put("lastModifier",createUser);
            paramMap.put("lastModifytime",time);
            if(paramMap.get("pid")!=null&&!StringUtils.isBlank(paramMap.get("pid").toString())){
                JSONObject obj = findByUserId(paramMap);
                BaseUserEntity entity = (BaseUserEntity) JSONObject.toBean(obj.getJSONObject("RESULT"),BaseUserEntity.class);
                if(!entity.getPwd().equals(paramMap.get("pwd").toString()))
                    paramMap.put("pwd", CryptUtils.getInstance().encode(paramMap.get("pwd").toString()));
                i = baseUserMapper.updateUserInfo(paramMap);
            }else {
                String pid = UUIDGenerator.getUUIDoffSpace();
                paramMap.put("pid",pid);
                paramMap.put("creater",createUser);
                paramMap.put("createTime",time);
                paramMap.put("pwd", CryptUtils.getInstance().encode(paramMap.get("pwd").toString()));
                i = baseUserMapper.saveUserInfo(paramMap);
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
    public JSONObject queryUserData(Map whereMap){
        if (whereMap==null)
            return JointJsonResult.joinJson("failed","param is null.");
        Map jsonMap = new HashMap();
        try {
            String pageSize = whereMap.get("pagesize") != null ? whereMap.get("pagesize").toString() : "0";
            String pageNum = whereMap.get("page") != null ? whereMap.get("page").toString():"0";
            int pagesize = Integer.parseInt(pageSize);
            int page = (Integer.parseInt(pageNum) - 1 ) * pagesize;
            whereMap.put("pagesize",pagesize);
            whereMap.put("page",page);
            List<BaseUserEntity> users = baseUserMapper.queryUserData(whereMap);
            int count = baseUserMapper.queryUserCount(whereMap);
            if(users!=null){
                jsonMap.put("num",count);
                jsonMap.put("list",users);
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
    public JSONObject findByUserId(Map whereMap){
        if (whereMap==null||whereMap.get("pid")==null||StringUtils.isBlank(whereMap.get("pid").toString()))
            return JointJsonResult.joinJson("failed","param is null.");
        List<BaseUserEntity> users = baseUserMapper.queryUserData(whereMap);
        try {
            if(users==null&&users.size()<0)
                return JointJsonResult.joinJson("failed","not found data by id.");
        }catch (Exception e){
           return JointJsonResult.joinJson("error","Request Error");
        }
        return JointJsonResult.joinJson("success",users.get(0));

    }

    /**
     * 根据ID删除用户
     * @param whereMap
     * @return
     */
    public JSONObject delByUserId(Map whereMap){
        if (whereMap==null||whereMap.get("pid")==null||StringUtils.isBlank(whereMap.get("pid").toString()))
            return JointJsonResult.joinJson("failed","param is null.");
        int i = 0;
        try{
            i = baseUserMapper.deleteUserInfo(whereMap);
            if(i==0)
                return JointJsonResult.joinJson("warn","remove user is failed.");
        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }
        return JointJsonResult.joinJson("success",i>0?"ok":"error");
    }

    /**
     * 批量删除用户   多个ID用“;”分隔
     * @param whereMap
     * @return
     */
    public JSONObject batchDelUser(Map whereMap){
        if (whereMap==null||whereMap.get("pid")==null||StringUtils.isBlank(whereMap.get("pid").toString()))
            return JointJsonResult.joinJson("failed","param is null.");
        int i = 0;
        try{
            String [] pids = whereMap.get("pid").toString().split(";");
            i = baseUserMapper.batchDeleteUser(Arrays.asList(pids));
            if(i==0)
                return JointJsonResult.joinJson("warn","remove user is failed.");
        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }
        return JointJsonResult.joinJson("success",i>0?"ok":"error");
    }

    /**
     * 批量修改用户信息（部门id，部门名称）
     * @param userEntity
     * @return
     */
    public JSONObject updateUserList(BaseUserEntity userEntity){
        if(userEntity == null){
            return JointJsonResult.joinJson("failed","param is null");
        }
        int a = 0;
        try {
            List<BaseUserEntity> listUpdate = new ArrayList<>();
            String [] ids = userEntity.getPid().split(",");
            String time = System.currentTimeMillis()/1000+"";
            for (int i = 0;i<ids.length;i++){
                BaseUserEntity baseUserEntity = new BaseUserEntity();
                baseUserEntity.setDepid(userEntity.getDepid());
                baseUserEntity.setDepName(userEntity.getDepName());
                baseUserEntity.setPid(ids[i]);
                baseUserEntity.setCreateTime(time);
                listUpdate.add(baseUserEntity);
            }
            a = baseUserMapper.updateDepToUserList(listUpdate);

        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }
        return JointJsonResult.joinJson("success",a>0);
    }

    /**
     * 批量移除用户信息（部门id，部门名称）
     * @param userEntity
     * @return
     */
    public JSONObject delUserList(BaseUserEntity userEntity){
        if(userEntity == null){
            return JointJsonResult.joinJson("failed","param is null");
        }
        int a = 0;
        try {
            List<BaseUserEntity> listUpdate = new ArrayList<>();
            String [] ids = userEntity.getPid().split(",");
            String time = System.currentTimeMillis()/1000+"";
            for (int i = 0;i<ids.length;i++){
                BaseUserEntity baseUserEntity = new BaseUserEntity();
                baseUserEntity.setDepid("");
                baseUserEntity.setDepName("");
                baseUserEntity.setPid(ids[i]);
                baseUserEntity.setCreateTime(time);
                listUpdate.add(baseUserEntity);
            }
            a = baseUserMapper.updateDepToUserList(listUpdate);

        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }
        return JointJsonResult.joinJson("success",a>0);
    }

    /**
     * 批量移除用户信息（角色id，角色名称）
     * @param userEntity
     * @return
     */
    public JSONObject delRoleToUserList(BaseUserEntity userEntity){
        if(userEntity == null){
            return JointJsonResult.joinJson("failed","param is null");
        }
        int a = 0;
        try {
        List<BaseUserEntity> listUpdate = new ArrayList<BaseUserEntity>();
        String [] ids = userEntity.getPid().split(",");
        String time = System.currentTimeMillis()/1000+"";
        for (int i = 0;i<ids.length;i++){
            BaseUserEntity baseUserEntity = new BaseUserEntity();
            baseUserEntity.setRoleid("");
            baseUserEntity.setRoleName("");
            baseUserEntity.setPid(ids[i]);
            baseUserEntity.setCreateTime(time);
            listUpdate.add(baseUserEntity);
        }
        a = baseUserMapper.updateRoleToUserList(listUpdate);

        }catch (Exception e){
            return JointJsonResult.joinJson("error","Request Error");
        }
        return JointJsonResult.joinJson("success",a>0);
    }


}
