package cn.com.ultrapower.logrecord.mapper;

import java.util.Map;

public interface LogRecordMapper {

    //添加组件日志记录
    public int saveLogRecord(Map map);

    //根据id查看组件相关配置记录
    public Map queryLogRecordById(Map map);

    //根据id修改组件
    public int updateLogRecorById(Map map);
}
