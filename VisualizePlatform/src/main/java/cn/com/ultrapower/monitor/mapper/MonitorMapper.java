package cn.com.ultrapower.monitor.mapper;

import cn.com.ultrapower.monitor.entity.MonitorEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/9/14.
 */
public interface MonitorMapper {
    public int insertMonitor(Map queryMap);//添加

    public List<MonitorEntity> queryMonitorList(Map queryMap);//查询列表

    public int queryMonitorCount(Map queryMap);//查询列表数量

    public int delMonitorById(Map queryMap);//删除记录

    public Map queryMonitorById(Map map);//回显

    public String checkTaskSign(Map map);//验证标识是否存在

    public int receiveData(Map dataMap);//接收kafka数据
}
