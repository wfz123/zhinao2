package cn.com.ultrapower.utils;

/**
 * Created: Vincent
 * Date: 2018/2/5 13:52
 * Description:
 */
public class ModelObject implements java.io.Serializable {
    private String loginName;
    private String token;
    private String time;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ModelObject() {
        this.loginName = loginName;
        this.token = token;
        this.time = time;
    }


}
