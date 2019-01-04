package cn.com.ultrapower.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 * Created by admin on 2018/6/5.
 */
public class ExportFileUtil {
    /**
     * 声明日志记录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportFileUtil.class);

    /**
     * 导出文本文件
     * @param response
     * @param jsonString
     * @param fileName
     */
    public static void writeToFile(HttpServletResponse response, String jsonString, String fileName,String fileType) {//设置响应的字符集
        boolean flag = false;
        response.setCharacterEncoding("utf-8");
        //设置响应内容的类型
        response.setContentType("text/plain");
        //设置文件的名称和格式
        response.addHeader(
                "Content-Disposition",
                "attachment; filename="
                        + FileUtils.genAttachmentFileName(fileName+ "_", "JSON_FOR_UCC_")
                        + MessageFormat.format("{0,date,yyyy-MM-dd HH:mm:ss}", new Object[]{Calendar.getInstance().getTime()})
                        + "."+fileType);//通过后缀可以下载不同的文件格式
        BufferedOutputStream buff = null;
        ServletOutputStream outStr = null;
        try {
            outStr = response.getOutputStream();
            buff = new BufferedOutputStream(outStr);
            buff.write(delNull(jsonString).getBytes("UTF-8"));
            buff.flush();
            buff.close();
            flag = true;
        } catch (Exception e) {
            flag = false;
            LOGGER.error("导出文件文件出错，e:{}",e);
        } finally {try {
            buff.close();
            outStr.close();
        } catch (Exception e) {
            LOGGER.error("关闭流对象出错 e:{}",e);
        }
        }
        //return flag;
    }

    /**
     * 如果字符串对象为 null，则返回空字符串，否则返回去掉字符串前后空格的字符串
     * @param str
     * @return
     */
    public static String delNull(String str) {
        String returnStr="";
        if (StringUtils.isNotBlank(str)) {
            returnStr=str.trim();
        }
        return returnStr;
    }
}
