package cn.com.ultrapower.logrecord.service;

import cn.com.ultrapower.logrecord.mapper.LogRecordMapper;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.UUIDGenerator;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LogRecordService {

    @Autowired
    private LogRecordMapper logRecordMapper;

    /**
     * 根据id查看组件相关配置记录
     * @param map
     * @return
     */
    public JSONObject queryLogRecordById(Map map) {
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        try {
            dataMap = logRecordMapper.queryLogRecordById(map);
            return JointJsonResult.joinJson("success",dataMap);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }

    }

    /**
     * 将组件日志记录添加到数据库
     * @param map
     * @return
     */
    public JSONObject saveLogRecord(Map map){
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        try {
            map.put("pid", UUIDGenerator.getUUIDoffSpace());
            map.put("createTime", System.currentTimeMillis() / 1000);
            int num = logRecordMapper.saveLogRecord(map);
            dataMap.put("insert", num);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }
        return JointJsonResult.joinJson("success",dataMap);
    }

    /**
     * 根据id修改组件日志记录
     * @param map
     * @return
     */
    public JSONObject updateLogRecord(Map map){
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        Map dataMap = new HashMap();
        try {
            int num=logRecordMapper.updateLogRecorById(map);
            dataMap.put("update",num);
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }
        return JointJsonResult.joinJson("success",dataMap);
    }
}
