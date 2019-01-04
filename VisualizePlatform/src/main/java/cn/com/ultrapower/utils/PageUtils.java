package cn.com.ultrapower.utils;

import java.util.Map;

/**
 * Created by wfz on 2018/5/11.
 */
public class PageUtils {

    //组织分页信息
    public static Map getPageInfo(Map map) {
        String pageSize = map.get("pagesize") != null ? map.get("pagesize").toString() : "0";
        String pageNum = map.get("page") != null ? map.get("page").toString():"0";
        int pagesize = Integer.parseInt(pageSize);
        int page = (Integer.parseInt(pageNum) - 1 ) * pagesize;
        map.put("pagesize",pagesize);
        map.put("page",page);
        return map;
    }
}
