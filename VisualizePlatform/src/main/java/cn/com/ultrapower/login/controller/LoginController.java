package cn.com.ultrapower.login.controller;

import cn.com.ultrapower.login.service.LoginService;
import cn.com.ultrapower.single.SingleCacheManager;
import cn.com.ultrapower.user.entity.BaseUserEntity;
import cn.com.ultrapower.utils.CryptUtils;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.ModelObject;
import cn.com.ultrapower.utils.TimeUtil;
import com.alibaba.fastjson.JSON;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    LoginService loginService;

    @RequestMapping(value = "/signIn", produces = "application/json")
    @ResponseBody
    public JSONObject signIn(HttpServletRequest request) {
        Map token = new HashMap();
        token.put("flag", false);
        token.put("token", "");
        //token.put("menu", "");
        BaseUserEntity user = new BaseUserEntity();
        String username = request.getParameter("loginName");
        String password = request.getParameter("pwd");
        //密码加密
        CryptUtils cryp = CryptUtils.getInstance();
        String passWd = cryp.encode(password);
        user.setPwd(passWd);
        user.setLoginName(username);
        //验证用户名和密码
        BaseUserEntity entity = loginService.queryUser(user);
        if (entity != null) {
            if(!"1".equals(entity.getStatus())){
                return JointJsonResult.joinJson("disabled",token);
            }else{
                CacheManager cacheManager = SingleCacheManager.getCacheManager();
                try {
                    token.put("flag", true);
                    token.put("token", cryp.encode(username));//token值前端存储
                   // token.put("menu", loginService.queryMenuList(username));
                    // 2. 获取缓存对象
                    Cache cache = cacheManager.getCache("loginCache");
                    ModelObject cacheusertemp = new ModelObject();
                    cacheusertemp.setLoginName(username);
                    cacheusertemp.setTime(String.valueOf(System.currentTimeMillis()));
                    Element element = new Element(cryp.encode(username), JSON.toJSONString(cacheusertemp));
                    // 3. 将元素添加到缓存
                    cache.put(element);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return JointJsonResult.joinJson("success",token);
            }
        } else {
            return JointJsonResult.joinJson("error",token);
        }
    }

    /**
     * 登出系统
     * @param request  传递用户名
     * @return
     */
    @RequestMapping(value = "/signOut", produces = "application/json")
    public JSONObject signOut(HttpServletRequest request){
        String username = request.getParameter("loginName");
        try {
            CryptUtils cryp = CryptUtils.getInstance();
            String token = cryp.encode(username);
            CacheManager manager = SingleCacheManager.getCacheManager();
            Cache cache = manager.getCache("loginCache");
            Element element = cache.get(token);
//            System.out.println(TimeUtil.getCurrentDate("YYYY-MM-dd HH:mm:ss")+"------登出-----"+element);
            if(element!=null){
                cache.remove(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JointJsonResult.joinJson("500","error");
        }
        return JointJsonResult.joinJson("200","success");
    }
    @RequestMapping(value = "/getMenuListByRole", produces = "application/json")
    @ResponseBody
    public JSONObject getMenuListByRole(HttpServletRequest request, String loginname) {
        return loginService.queryMenuList(loginname);
    }
    public static void main(String [] ar){
        CryptUtils cryptUtils = CryptUtils.getInstance();
        String pwd = cryptUtils.decode("RooOR5dtLDA=");
        System.out.println(pwd);
    }
}