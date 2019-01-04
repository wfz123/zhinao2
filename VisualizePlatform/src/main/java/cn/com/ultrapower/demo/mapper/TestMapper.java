package cn.com.ultrapower.demo.mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/8/22.
 */
public interface TestMapper {

    List<Map> queryList(Map map);

    int queryCount(Map map);
}
