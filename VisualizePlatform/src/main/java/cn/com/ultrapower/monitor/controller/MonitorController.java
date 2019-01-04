package cn.com.ultrapower.monitor.controller;

import cn.com.ultrapower.monitor.service.MonitorService;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by admin on 2018/9/14.
 */
@Controller
@RequestMapping("monitor")
public class MonitorController {
    @Autowired
    private MonitorService monitorService;

    /**
     * 添加
     * @param insertStr 添加JSON字符串
     * @return
     */
    @RequestMapping(value = "/insertMonitor", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject insertMonitor(@Param("insertStr") String insertStr) {
        JSONObject jsonObject = JSONObject.fromObject(insertStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        JSONObject json = monitorService.insertMonitor(jsonMap);
        return json;
    }

    /**
     * 查询
     * @param queryStr 查询条件
     * @return
     */
    @RequestMapping(value = "/queryMonitor", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryMonitor(@Param("queryStr") String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        JSONObject json = monitorService.queryMonitor(jsonMap);
        return json;
    }

    /**
     * 删除
     * @param idStr pid
     * @return
     */
    @RequestMapping(value = "/delMonitorById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject delMonitorById(String idStr){
        JSONObject jsonObject = JSONObject.fromObject(idStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return monitorService.delMonitorById(map);
    }

    /**
     * 根据pid进行编辑回显
     * @param idStr pid
     * @return
     */
    @RequestMapping(value = "/queryMonitorById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryMonitorById(String idStr){
        JSONObject jsonObject = JSONObject.fromObject(idStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return monitorService.queryMonitorById(map);
    }

    /**
     * 更新
     * @param updateStr 更新JSON字符串
     * @return
     */
    @RequestMapping(value = "/updateMonitor", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject updateMonitor(String updateStr){
        JSONObject jsonObject = JSONObject.fromObject(updateStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return monitorService.updateMonitor(map);
    }

    /**
     * 校验任务标识
     * @param taskSignStr 任务标识
     * @return
     */
    @RequestMapping(value = "/checkTaskSign", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject checkTaskSign(String taskSignStr){
        JSONObject jsonObject = JSONObject.fromObject(taskSignStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return monitorService.checkTaskSign(map);
    }

    /**
     * 接收kafka数据
     * @param receiveData
     * @return
     */
    @RequestMapping(value = "/receiveData", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject receiveData(String receiveData){
        JSONObject jsonObject = JSONObject.fromObject(receiveData);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return monitorService.receiveData(map);
    }
}
