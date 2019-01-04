package cn.com.ultrapower.components.service;

import cn.com.ultrapower.components.mapper.PiecemealMapper;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.PageUtils;
import cn.com.ultrapower.utils.UUIDGenerator;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PiecemealService {
    @Autowired
    private  PiecemealMapper piecemealMapper;

    /**
     * 添加组件
     * @param inserMap
     * @return
     */
    public JSONObject savePiecemeal(Map inserMap){
        if (inserMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        try {
            inserMap.put("pid", UUIDGenerator.getUUIDoffSpace());
            inserMap.put("createTime", System.currentTimeMillis()/1000);
            int num = piecemealMapper.savePiecemeal(inserMap);
            dataMap.put("insert", num);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }
    /**
     * 查询组件
     * @param queryMap
     * @return
     */
    public JSONObject selectPiecemeal(Map queryMap){
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        try {
            if(!"".equals(queryMap.get("page"))&&!"".equals(queryMap.get("pagesize"))) {
                queryMap = PageUtils.getPageInfo(queryMap);
            }
            List<String> list = piecemealMapper.selectPiecemeal(queryMap);
            if (list != null && list.size() > 0) {
                dataMap.put("list", list);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }
    /**
     * 删除组件
     * @param map
     * @return
     */
    public JSONObject delPiecemealById(Map map){
        if (map==null) {
            return JointJsonResult.joinJson("failed", "query parameter is null");
        }
        try {
            int num = piecemealMapper.delPiecemealById(map);
            Map dataMap = new HashMap();
            dataMap.put("delete",num);
            return JointJsonResult.joinJson("success",dataMap);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "request happen exception");
        }
    }
}
