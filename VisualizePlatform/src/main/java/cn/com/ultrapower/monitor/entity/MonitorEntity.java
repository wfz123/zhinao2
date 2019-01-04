package cn.com.ultrapower.monitor.entity;

/**
 * Created by admin on 2018/9/14.监控任务实体
 */
public class MonitorEntity {
    private String pid;//主键
    private String taskName;//监控名称
    private String taskSign;//监控标示
    private String monitorTable;//监控表
    private String monitorCondition;//监控条件
    private String relationName;//关系计算名称
    private String relationCalculateSQL;//关系计算sql
    private String resultStoreTable;//结果存储表
    private String storeColumns;//存储字段
    private String createTime;//创建时间
    //private String updateTime;//修改时间

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskSign() {
        return taskSign;
    }

    public void setTaskSign(String taskSign) {
        this.taskSign = taskSign;
    }

    public String getMonitorTable() {
        return monitorTable;
    }

    public void setMonitorTable(String monitorTable) {
        this.monitorTable = monitorTable;
    }

    public String getMonitorCondition() {
        return monitorCondition;
    }

    public void setMonitorCondition(String monitorCondition) {
        this.monitorCondition = monitorCondition;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationCalculateSQL() {
        return relationCalculateSQL;
    }

    public void setRelationCalculateSQL(String relationCalculateSQL) {
        this.relationCalculateSQL = relationCalculateSQL;
    }

    public String getResultStoreTable() {
        return resultStoreTable;
    }

    public void setResultStoreTable(String resultStoreTable) {
        this.resultStoreTable = resultStoreTable;
    }

    public String getStoreColumns() {
        return storeColumns;
    }

    public void setStoreColumns(String storeColumns) {
        this.storeColumns = storeColumns;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
