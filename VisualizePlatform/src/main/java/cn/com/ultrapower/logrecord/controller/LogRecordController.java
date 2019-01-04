package cn.com.ultrapower.logrecord.controller;

import cn.com.ultrapower.logrecord.service.LogRecordService;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("logrecord")
public class LogRecordController {

    @Autowired
    private LogRecordService logRecordService;

    /**
     *点击发布将组件日志记录添加到数据库
     * @param insertStr 添加JSON字符串
     * @return
     */
    @RequestMapping(value = "/saveLogRecord", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject saveLogRecord(@Param("insertStr") String insertStr){
        JSONObject jsonObject = JSONObject.fromObject(insertStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        JSONObject js = logRecordService.saveLogRecord(map);
        return js;
    }

    /**
     * 根据id查看组件相关配置记录
     *  @param idStr
     * @return
     */
    @RequestMapping(value = "/queryLogRecordById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryLogRecordById(String idStr){
        JSONObject jsonObject = JSONObject.fromObject(idStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        JSONObject js = logRecordService.queryLogRecordById(map);
        return js;
    }

    /**
     * 根据id修改组件
     *  @param logRecordStr
     * @return
     */
    @RequestMapping(value = "/updateLogRecordById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject updateLogRecordById(@Param("logRecordStr")String logRecordStr){
        JSONObject jsonObject = JSONObject.fromObject(logRecordStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        JSONObject js = logRecordService.updateLogRecord(map);
        return js;
    }
}
