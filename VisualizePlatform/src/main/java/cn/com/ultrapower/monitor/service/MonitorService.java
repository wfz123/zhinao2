package cn.com.ultrapower.monitor.service;

import cn.com.ultrapower.monitor.entity.MonitorEntity;
import cn.com.ultrapower.monitor.mapper.MonitorMapper;
import cn.com.ultrapower.utils.*;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by admin on 2018/9/14.
 */
@Service
public class MonitorService {
    @Autowired
    private MonitorMapper monitorMapper;

    /**
     * 添加监控任务
     * @param inserMap
     * @return
     */
    public JSONObject insertMonitor(Map inserMap) {
        if (inserMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        try {
            //远程调用创建监听视图接口
           /* String returnData = this.createView(inserMap);
            JSONObject jsonObject = JSONObject.fromObject(returnData);
            if(jsonObject.getInt("code")!=200) {
                return JointJsonResult.joinJson("error", "远程服务异常");
            }*/
            inserMap.put("pid", UUIDGenerator.getUUIDoffSpace());
            inserMap.put("createTime", System.currentTimeMillis()/1000);
            int num = monitorMapper.insertMonitor(inserMap);
            dataMap.put("insert", num);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 远程调用创建视图的接口
     * @param map
     * @return
     */
    public String createView(Map map) {

        Properties properties =PropertiesUtil.initProperties("config/config.properties");
        String url=properties.getProperty("createViewUrl");
        Map sendMap = new HashMap();
        Map paraMap = new HashMap();
        paraMap.put("subscribed_name", map.get("taskSign"));
        paraMap.put("topic", map.get("taskSign"));
        paraMap.put("sql", map.get("monitorCondition"));
        if(map.containsKey("storeColumns")) {
            JSONObject jsonObject = JSONObject.fromObject(map.get("storeColumns"));
            Map<String,String> columnsMap = (Map) JSONObject.toBean(jsonObject, Map.class);
            StringBuffer stringBuffer = new StringBuffer();
            //传递的是存储字段
            for(String keys:columnsMap.keySet()) {
                if(stringBuffer.length()>0) stringBuffer.append(",");
                stringBuffer.append(keys);
            }
            paraMap.put("columns",stringBuffer.toString());
        }
        String sendStr = JSONObject.fromObject(paraMap).toString();
        sendMap.put("param", sendStr);
        String returnData = HttpClientUtil.doPost(url, sendMap);
        return returnData;
    }

    /**
     * 远程调用删除视图
     * @param map
     * @return
     */
    public String deleteView(Map map) {
        Properties properties =PropertiesUtil.initProperties("config/config.properties");
        String url=properties.getProperty("deleteViewUrl");
        Map paraMap = new HashMap();
        Map sendMap = new HashMap();
        paraMap.put("subscribed_name", map.get("taskSign"));
        String sendStr = JSONObject.fromObject(paraMap).toString();
        sendMap.put("param", sendStr);
        String returnData = HttpClientUtil.doPost(url, sendMap);
        return returnData;
    }
    /**
     * 查询接口
     * @param queryMap
     * @return
     */
    public JSONObject queryMonitor(Map queryMap) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        try {
            if(!"".equals(queryMap.get("page"))&&!"".equals(queryMap.get("pagesize"))) {
                queryMap = PageUtils.getPageInfo(queryMap);
            }
            List<MonitorEntity> list = monitorMapper.queryMonitorList(queryMap);
            int num = monitorMapper.queryMonitorCount(queryMap);
            if (list != null && list.size() > 0) {
                dataMap.put("num", num);
                dataMap.put("list", list);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 根据pid删除数据
     * @param map
     * @return
     */
    public JSONObject delMonitorById(Map map) {
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        int num = 0;
        try {
            /*Map taskMap = monitorMapper.queryMonitorById(map);
            String returnData = this.deleteView(taskMap);
            JSONObject jsonObject = JSONObject.fromObject(returnData);
            if(jsonObject.getInt("code")!=200) {
                return JointJsonResult.joinJson("error", "远程服务异常");
            }*/
            num = monitorMapper.delMonitorById(map);
            Map dataMap = new HashMap();
            dataMap.put("delete",num);
            return JointJsonResult.joinJson("success",dataMap);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }

    }

    /**
     * 更新,不能修改标识，也就是不能修改视图
     * @param map
     * @return
     */
    public JSONObject updateMonitor(Map map) {
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        int num = 0;
        try {
            num = monitorMapper.delMonitorById(map);
            map.put("createTime", System.currentTimeMillis() / 1000);
            num = monitorMapper.insertMonitor(map);
            Map dataMap = new HashMap();
            dataMap.put("update",num);
            return JointJsonResult.joinJson("success",dataMap);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }

    }

    /**
     * 根据id查询
     * @param map
     * @return
     */
    public JSONObject queryMonitorById(Map map) {
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        try {
            Map dataMap = monitorMapper.queryMonitorById(map);
            return JointJsonResult.joinJson("success",dataMap);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }
    }

    /**
     * 校验该标识是否存在
     * @param map
     * @return
     */
    public JSONObject checkTaskSign(Map map) {
        //查询数据库中是否存在某个数据库
        Map dataMap = new HashMap();
        boolean flag = false;
        try {
            String taskSign = monitorMapper.checkTaskSign(map);
            if (StringUtils.isNotBlank(taskSign)) {
                flag = true;
            }
            dataMap.put("isExist", flag);
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 接收kafka数据
     * @param map
     * @return
     */
    public JSONObject receiveData(Map map) {
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        int num =0;
        try {
            if(map.containsKey("topic")) {
                Map topicMap = monitorMapper.queryMonitorById(map);
                String resultStoreTable = (String) topicMap.get("resultStoreTable");
                System.out.println(resultStoreTable);
                JSONObject jsonObject = JSONObject.fromObject(map.get("data"));
                Map valueMap = (Map) JSONObject.toBean(jsonObject, Map.class);
                List<String> keys = new ArrayList<>();
                Set<String> values = valueMap.keySet();
                for (String key : values) {keys.add(key);}
                dataMap.put("tableName", resultStoreTable);
                dataMap.put("keys", keys);
                dataMap.put("params", valueMap);
                num = monitorMapper.receiveData(dataMap);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success","接收数据成功");
    }
}
