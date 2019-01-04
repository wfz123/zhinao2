package cn.com.ultrapower.groupmanage.service;

import cn.com.ultrapower.groupmanage.mapper.GroupmanageMapper;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.PageUtils;
import cn.com.ultrapower.utils.StringUtils;
import cn.com.ultrapower.utils.UUIDGenerator;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupmanageService {
    @Autowired
    private GroupmanageMapper groupmanageMapper;

    /**
     * 添加分组
     *
     * @param inserMap
     * @return
     */
    public JSONObject saveGroupmanage(Map inserMap) {
        if (inserMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        try {
            inserMap.put("pid", UUIDGenerator.getUUIDoffSpace());
            inserMap.put("createTime", System.currentTimeMillis() / 1000);
            int num = groupmanageMapper.saveGroupmanage(inserMap);
            dataMap.put("insert", num);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "save happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询分组
     *
     * @param queryMap
     * @return
     */
    public JSONObject queryGroupmanageName(Map queryMap) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        try {
            if (!"".equals(queryMap.get("page")) && !"".equals(queryMap.get("pagesize"))) {
                queryMap = PageUtils.getPageInfo(queryMap);
            }
            List<String> list = groupmanageMapper.queryGroupmanageName(queryMap);
            if (list != null && list.size() > 0) {
                dataMap.put("list", list);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 根据id查询分组信息
     *
     * @param queryMap
     * @return
     */
    public JSONObject queryGroupmanageById(Map queryMap) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        try {
            dataMap = groupmanageMapper.queryGroupmanageById(queryMap);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 根据id修改分组
     *
     * @param map
     * @return
     */
    public JSONObject updateGroupnamageById(Map map) {
        if (map == null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        try {
            int num = groupmanageMapper.updateGroupnamageById(map);
            dataMap.put("update", num);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "update happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 添加分组关系
     *
     * @param inserMap
     * @return
     */
    public JSONObject saveGrouprelationhtml(Map inserMap) {
        if (inserMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        if (StringUtils.isNotBlank((String) inserMap.get("groupId"))){
            try {
                inserMap.put("pid", UUIDGenerator.getUUIDoffSpace());
                inserMap.put("createTime", System.currentTimeMillis() / 1000);
                int num = groupmanageMapper.saveGrouprelationhtml(inserMap);
                dataMap.put("insert", num);
            } catch (Exception ex) {
                ex.printStackTrace();
                return JointJsonResult.joinJson("error", "save happen exception");
            }
        }else {
            return JointJsonResult.joinJson("error", "save happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 根据htmlId修改分组关系
     *
     * @param map
     * @return
     */
    public JSONObject updateByhtmlId(Map map) {
        if (map == null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        int num;
        Map dataMap = new HashMap();
        try {
            if (StringUtils.isBlank((String) map.get("groupId"))) {
                num = groupmanageMapper.deleteByhtmlId(map);
                dataMap.put("update", num);
            } else {
                num = groupmanageMapper.updateByhtmlId(map);
                dataMap.put("update", num);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "update happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 根据groupId删除分组
     *
     * @param map
     * @return
     */
    public JSONObject dropGroup(Map map) {
        if (map == null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        int num = 0;
        try {
            num = groupmanageMapper.dropGroupmanageByGroupId(map);
            num = groupmanageMapper.dropGrouprelationhtmlByGroupId(map);
            dataMap.put("delete", num);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "drop happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询分组页面信息
     *
     * @param queryMap
     * @return
     */
    public JSONObject queryGroupList(Map queryMap) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        try {
            Map unGroupMap = groupmanageMapper.queryUnGroupConTim();
            List<Map> groupList = groupmanageMapper.queryGroupConTim(queryMap);
            if (queryMap.get("groupName").equals("")) {
                groupList.add(0, unGroupMap);
            }
            dataMap.put("list", groupList);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询分组页面列表
     *
     * @param queryMap
     * @return
     */
    public JSONObject queryGroupHtmlList(Map queryMap) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        try {
            if (!"".equals(queryMap.get("page")) && !"".equals(queryMap.get("pagesize"))) {
                queryMap = PageUtils.getPageInfo(queryMap);
            }
            List<Map> list = null;
            int num = 0;
            if (StringUtils.isNotBlank((String) queryMap.get("groupId"))) {
                list = groupmanageMapper.queryGroupHtmlByGroupId(queryMap);
                num = groupmanageMapper.queryGroupCount(queryMap);
            } else {
                list = groupmanageMapper.queryUnGroupHtmlList(queryMap);
                num = groupmanageMapper.queryUnGroupCount(queryMap);
            }
            if (list != null && list.size() > 0) {
                dataMap.put("list", list);
                dataMap.put("num", num);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 分组名称重名验证
     *
     * @param queryMap
     * @return
     */
    public JSONObject queryGroupName(Map queryMap) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        boolean flag=false;
        try {
            String str = groupmanageMapper.queryGroupName(queryMap);
            if (StringUtils.isNotBlank(str)){
                flag=true;
            }
            dataMap.put("flag",flag);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }
}
