package cn.com.ultrapower.demo.service;

import cn.com.ultrapower.demo.mapper.TestMapper;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.PageUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/8/22.
 */
@Service
public class TestService {

    @Autowired
    private TestMapper testMapper;

    public JSONObject queryList(Map queryMap) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map map = PageUtils.getPageInfo(queryMap);
        List<Map> list = testMapper.queryList(map);
        int num = testMapper.queryCount(map);
        Map dataMap = new HashMap();
        if (list != null && list.size() > 0) {
            dataMap.put("num", num);
            dataMap.put("list", list);
        }
        return JointJsonResult.joinJson("success", dataMap);
    }
}
