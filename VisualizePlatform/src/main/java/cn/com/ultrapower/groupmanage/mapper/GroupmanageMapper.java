package cn.com.ultrapower.groupmanage.mapper;

import java.util.List;
import java.util.Map;

public interface GroupmanageMapper {
    //添加分组
    public int saveGroupmanage(Map map);
    //查询分组(根据名称模糊查询)
    public List<String> queryGroupmanageName(Map map);
    //根据id查询分组信息
    public Map queryGroupmanageById(Map map);
    //根据id修改分组
    public int updateGroupnamageById(Map map);
    //添加分组关系
    public int saveGrouprelationhtml(Map map);
    //根据htmlId修改分组关系
    public int updateByhtmlId(Map map);
    //根据htmlId删除分组关系
    public int deleteByhtmlId(Map map);
    //删除分组
    public int dropGroupmanageByGroupId(Map map);
    //删除分组关系
    public int dropGrouprelationhtmlByGroupId(Map map);
    //查询未文组页面数量、最新时间
    public Map queryUnGroupConTim();
    //查询未分组页面
    public List<Map> queryUnGroupHtmlList(Map map);
    //查询分组名、描述、页面数量、页面最新时间
    public List<Map> queryGroupConTim(Map map);
    //查询分组页面
    public List<Map> queryGroupHtmlByGroupId(Map map);
    //根据页面id删除关系数据
    public int delRelationByHtmlId(Map groupMap);
    //查询已分组页面数量
    public  int queryGroupCount(Map map);
    //查询未分组页面数量
    public int queryUnGroupCount(Map map);
    //分组名称重名验证
    public String queryGroupName(Map map);
}
