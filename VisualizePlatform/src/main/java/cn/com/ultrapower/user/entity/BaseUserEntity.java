package cn.com.ultrapower.user.entity;

public class BaseUserEntity {
    private String pid;             //主键ID
    private String jobnum;          //工号
    private String loginName;       //登录名
    private String fullName;        //姓名
    private String pwd;             //密码
    private String sex;             //性别
    private String position;        //职位
    private String type;            //用户类型
    private String mobile;          //手机
    private String phone;           //电话
    private String fax;             //传真
    private String email;           //邮箱
    private String status;          //状态    1.启用、0.停用
    private String orderNum;        //用名顺序
    private String image;           //头像路径
    private String locationzone;    //用户所属片区
    private String depid;           //部门ID
    private String depName;         //部门名称
    private String roleid;          //角色ID
    private String roleName;        //角色名称
    private String groupId;         //所属组ID
    private String groupName;       //所属组名称
    private String ptdepId;         //兼职部门id
    private String ptdepName;       //兼职部门名称
    private String profession;      //专业
    private String creater;         //创建者
    private String createTime;      //创建时间
    private String lastModifier;    //最后修改人
    private String lastModifytime;  //最后修改时间
    private String lastLoginTime;   //最后登录时间
    private String systemSkin;      //用户登录系统样式
    private String ipAddress;       //最后登录的IP地址
    private String msn;             //用户MSN
    private String qq;              //用户QQ
    private String remark;          //备注
    private String pyname;          //拼音姓名
    private String systemark;       //系统标识
    private String rongtoken;       //token
    private String imagestr;        //头像base64编码存储
    private String warnsignal;        //警号

    public String getWarnsignal() {
        return warnsignal;
    }

    public void setWarnsignal(String warnsignal) {
        this.warnsignal = warnsignal;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getJobnum() {
        return jobnum;
    }

    public void setJobnum(String jobnum) {
        this.jobnum = jobnum;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocationzone() {
        return locationzone;
    }

    public void setLocationzone(String locationzone) {
        this.locationzone = locationzone;
    }

    public String getDepid() {
        return depid;
    }

    public void setDepid(String depid) {
        this.depid = depid;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPtdepId() {
        return ptdepId;
    }

    public void setPtdepId(String ptdepId) {
        this.ptdepId = ptdepId;
    }

    public String getPtdepName() {
        return ptdepName;
    }

    public void setPtdepName(String ptdepName) {
        this.ptdepName = ptdepName;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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

    public String getLastModifytime() {
        return lastModifytime;
    }

    public void setLastModifytime(String lastModifytime) {
        this.lastModifytime = lastModifytime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getSystemSkin() {
        return systemSkin;
    }

    public void setSystemSkin(String systemSkin) {
        this.systemSkin = systemSkin;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPyname() {
        return pyname;
    }

    public void setPyname(String pyname) {
        this.pyname = pyname;
    }

    public String getSystemark() {
        return systemark;
    }

    public void setSystemark(String systemark) {
        this.systemark = systemark;
    }

    public String getRongtoken() {
        return rongtoken;
    }

    public void setRongtoken(String rongtoken) {
        this.rongtoken = rongtoken;
    }

    public String getImagestr() {
        return imagestr;
    }

    public void setImagestr(String imagestr) {
        this.imagestr = imagestr;
    }



}
