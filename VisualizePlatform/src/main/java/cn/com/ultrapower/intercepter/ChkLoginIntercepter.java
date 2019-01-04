package cn.com.ultrapower.intercepter;

import cn.com.ultrapower.single.SingleCacheManager;
import cn.com.ultrapower.utils.CryptUtils;
import cn.com.ultrapower.utils.ModelObject;
import cn.com.ultrapower.utils.TimeUtil;
import com.alibaba.fastjson.JSON;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.json.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChkLoginIntercepter implements HandlerInterceptor {
    private String [] allowUrls;
    public void setAllowUrls(String[] allowUrls) {
        this.allowUrls = allowUrls;
    }
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        return true;
        boolean flag = false;
        String token = request.getHeader("token");
        String applicationName = "/"
                + request.getContextPath().split("/")[request.getContextPath().split("/").length - 1] + "/";
        //获取请求路径
        String url = request.getRequestURI();
        if (url.endsWith(applicationName)) {
            flag = true;
        }else {
            for(String str:allowUrls) {
                if(url.contains(str)) {
                    flag = true;
                    break;
                }
            }
        }
        if(!flag) {
            CacheManager manager = SingleCacheManager.getCacheManager();
            Cache cache = manager.getCache("loginCache");
            Element element = cache.get(token);
//            System.out.println(TimeUtil.getCurrentDate("YYYY-MM-dd HH:mm:ss")+",token:"+token+",url:'"+url+"'通过拦截器访问缓存中token值:"+element);
            if (element != null && element.getObjectKey() != null) {
                JSONObject jsonObject = JSONObject.fromObject(element.getObjectValue());
                ModelObject modelObject = (ModelObject) JSONObject.toBean(jsonObject, ModelObject.class);
                long time = System.currentTimeMillis() - Long.parseLong(modelObject.getTime());
                if (time > 60 * 60 * 1000) {
                    flag = false;
                } else {
                    modelObject.setTime(System.currentTimeMillis() + "");
                    Element newElement = new Element(token, JSON.toJSONString(modelObject));
                    // 更新缓存数据的时间
                    cache.replace(newElement);
                    flag = true;
                }
            } else {
                flag = false;
            }
        }
        if(!flag) {
            response.sendError(444,"user is unload");
        }
        return flag;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
