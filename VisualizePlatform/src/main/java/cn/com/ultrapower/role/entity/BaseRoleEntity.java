package cn.com.ultrapower.role.entity;

public class BaseRoleEntity {
    private String pid;             //主键ID
    private String roleName;        //角色名称
    private String parentId;        //上级角色ID
    private String roleDNS;         //上级角色DNS
    private String roleDN;          //角色DNS
    private String defineType;      //角色启用
    private String creater;         //创建人
    private String createTime;      //创建时间      秒值
    private String lastModifier;    //最后修改人
    private String lastModifyTime;  //最后修改时间    秒值
    private String remark;           //备用字段

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRoleDNS() {
        return roleDNS;
    }

    public void setRoleDNS(String roleDNS) {
        this.roleDNS = roleDNS;
    }

    public String getRoleDN() {
        return roleDN;
    }

    public void setRoleDN(String roleDN) {
        this.roleDN = roleDN;
    }

    public String getDefineType() {
        return defineType;
    }

    public void setDefineType(String defineType) {
        this.defineType = defineType;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
