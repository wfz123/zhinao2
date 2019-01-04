package cn.com.ultrapower.components.mapper;

import java.util.List;
import java.util.Map;

public interface PiecemealMapper {
    //添加组件
    public int savePiecemeal(Map map);
    //查询组件
    public List<String> selectPiecemeal(Map map);
    //删除组件
    public int delPiecemealById(Map map);
}
