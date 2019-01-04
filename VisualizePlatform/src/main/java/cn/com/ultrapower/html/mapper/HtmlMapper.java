package cn.com.ultrapower.html.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/8/28.
 */
public interface HtmlMapper {


    //保存html文件内容
    public int saveHtmlContent(Map map);
    //根据html的id查询html组件内容
    public Map queryHtmlById(Map map);
    //根据html的id删除html组件内容
    public int deleteHtmlById(Map map);
    //查询页面列表
    public List<Map> queryHtmlList(Map map);
    public int queryHtmlCount(Map map);
}
