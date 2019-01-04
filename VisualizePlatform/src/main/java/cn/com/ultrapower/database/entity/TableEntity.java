package cn.com.ultrapower.database.entity;


public class TableEntity {
    private String tableName;       //表名(必填)
    private String columnName;      //列名(必填)
    private String dataType;        //数据类型(必填)
    private Long columnLength;      //列长度
    private Long columnPoint;       //小数点位数
    private Long isKey;             //该列是否是主键  1：是、0否
    private Long isNull;            //是否不可为空 1：是、0：否
    private String defaultValue;    //默认值
    private String annotations;     //注释
    //private String checkPid;        //校验外键
    //private String index;           //索引

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        if(!"".equals(columnName) && columnName != null)
            columnName = columnName.toUpperCase();
        this.columnName = columnName;
    }
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Long getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(Long columnLength) {
        this.columnLength = columnLength;
    }

    public Long getColumnPoint() {
        return columnPoint;
    }

    public void setColumnPoint(Long columnPoint) {
        this.columnPoint = columnPoint;
    }

    public Long getIsKey() {
        return isKey;
    }

    public void setIsKey(Long isKey) {
        this.isKey = isKey;
    }

    public Long getIsNull() {
        return isNull;
    }

    public void setIsNull(Long isNull) {
        this.isNull = isNull;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }


}
