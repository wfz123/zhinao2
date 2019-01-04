package cn.com.ultrapower.method.service.analysis;

import cn.com.ultrapower.utils.HttpClientUtil;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.StringUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wfz on 2018/12/24.
 */
@Service
public class AnalysisMethodService {
    /**
     * map中含有objectId,Url,
     * @param paramMap
     * @return
     */
    public JSONObject executeAnalysis(Map paramMap) {
        if (paramMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        Map dataMap = new HashMap();
        String objectKey = (String) paramMap.get("objectKey");
        boolean matches = objectKey.matches("^[A-Za-z]+$");
        if(matches) paramMap.put("objectSign", objectKey);
        else paramMap.put("objectId", objectKey);
        //paramMap.put("text", "经出警到现场，系报警人吴刚（男，高中文化，汉族，22岁，1994年11月13日生，身份证号码：522425199411132416，户籍地：贵州省织金县牛场镇群裕村中山组，现住：贵阳市云岩区改茶路127号）于2016年11月01日7时许，其在贵阳市云岩区改茶路127号4-2房家中被人技术性开锁盗走一部直板金色OPPOR9手机，串号不详，电话：18786670836，购于2016年8月，当时购价3299元，一部直板黑色华为荣耀6手机，串号不详，电话：15185011549，购于2015年5月，当时购价1200元，被盗现金500元都是面值100元的。");
        try {
            String returnData = "";
            if(StringUtils.isNotBlank((String) paramMap.get("text"))&&paramMap.containsKey("httpUrl")&&(paramMap.containsKey("objectId")||paramMap.containsKey("objectSign"))) {
                returnData = HttpClientUtil.doPost((String) paramMap.get("httpUrl"),paramMap);
            }
            dataMap.put("list", returnData);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "analysis happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }
}
