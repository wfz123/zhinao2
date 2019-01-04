package cn.com.ultrapower.role.entity;

public class BaseRoleRelationMenu {
    private String menuPid;         //菜单ID
    private String nodeName;        //菜单节点名称
    private String menuParentId;    //菜单父节点ID
    private String nodeType;        //菜单节点类型
    private String roleMenuPid;     //roleMenutree表的主ID字段
    private String rolePid;         //角色ID
    private String nodeurl;
    private int isSelected;         //是否被选中的标识
    private String creater;         //创建人
    private String createTime;      //创建时间
    private String lastModifier;    //最后修改人
    private String lastModifyTime;  //最后修改时间

    public String getNodeurl() {
        return nodeurl;
    }

    public void setNodeurl(String nodeurl) {
        this.nodeurl = nodeurl;
    }

    public String getMenuPid() {
        return menuPid;
    }

    public void setMenuPid(String menuPid) {
        this.menuPid = menuPid;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getMenuParentId() {
        return menuParentId;
    }

    public void setMenuParentId(String menuParentId) {
        this.menuParentId = menuParentId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getRoleMenuPid() {
        return roleMenuPid;
    }

    public void setRoleMenuPid(String roleMenuPid) {
        this.roleMenuPid = roleMenuPid;
    }

    public String getRolePid() {
        return rolePid;
    }

    public void setRolePid(String rolePid) {
        this.rolePid = rolePid;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
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
}
