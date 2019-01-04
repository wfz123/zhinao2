package cn.com.ultrapower.method.entity;


import java.util.List;

/**
 * Created by wfz on 2018/10/12.
 */
public class TreeEntity{
    private String label;
    private String id;
    private String pid;
    private List<TreeEntity> children;


    public TreeEntity(String label, String id, String pid, List<TreeEntity> children) {
        super();
        this.label = label;
        this.id = id;
        this.pid = pid;
        this.children = children;
    }

    public TreeEntity() {
        super();
    }

    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<TreeEntity> getChildren() {
        return children;
    }
    public void setChildren(List<TreeEntity> children) {
        this.children = children;
    }
}
