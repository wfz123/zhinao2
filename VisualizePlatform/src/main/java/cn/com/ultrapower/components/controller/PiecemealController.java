package cn.com.ultrapower.components.controller;

import cn.com.ultrapower.components.service.PiecemealService;
import cn.com.ultrapower.utils.JointJsonResult;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("piecemeal")
public class PiecemealController {
    @Autowired
    private PiecemealService piecemealService;
    /**
     * 添加组件
     * @param insertStr
     * @return
     */
    @RequestMapping(value = "/savePiecemeal", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject savePiecemeal(String insertStr){
        JSONObject jsonObject = JSONObject.fromObject(insertStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return piecemealService.savePiecemeal(jsonMap);
    }
    /**
     * 查询组件
     * @param queryLabel
     * @return
     */
    @RequestMapping(value = "/selectPiecemeal", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject selectPiecemeal(String  queryLabel){
        JSONObject jsonObject = JSONObject.fromObject(queryLabel);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return piecemealService.selectPiecemeal(jsonMap);
    }
    /**
     * 删除组件
     * @param idStr
     * @return
     */
    @RequestMapping(value = "/delPiecemealById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject delPiecemealById(String  idStr){
        JSONObject jsonObject = JSONObject.fromObject(idStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return piecemealService.delPiecemealById(jsonMap);
    }

    /**
     * 文件上传
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject upload(@RequestParam("file") CommonsMultipartFile file, HttpServletRequest request) {
        if (file.getSize()==0) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        // 获取项目的实际路径
        String path = request.getSession().getServletContext().getRealPath("/");
        String fileName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + file.getOriginalFilename();
        File targetFile = new File(path, fileName);
        Map dataMap = new HashMap();
        try {
            // 通过CommonsMultipartFile的方法直接保存文件
            file.transferTo(targetFile);
            dataMap.put("path", path);
        } catch (IOException e) {
            e.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }
}
