package cn.com.ultrapower.utils;


import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import java.util.*;


public class FileOperateUtil {


    private static final String CONTENTTYPE = "contentType";
    private static final String CREATETIME = "createTime";

    /**
     * 将上传的文件进行重命名
     *
     * @param name
     * @return
     */
    private static String rename(String name) {
        String fileName = UUIDGenerator.getUUIDoffSpace();
        if (name.indexOf(".") != -1) {
            fileName += name.substring(name.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     * 上传文件
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> fileUpload(HttpServletRequest request) throws Exception {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        // 获得上传的文件
        Map<String, MultipartFile> fileMap = mRequest.getFileMap();
        // 文件上传后存放的地址
        String uploadDir = PropertiesUtil.initProperties("config/config.properties").getProperty("uploadDir");
        File file = new File(uploadDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        // 文件名称
        String fileName = null;
        int i = 0;
        for (Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet().iterator(); it.hasNext(); i++) {
            Map.Entry<String, MultipartFile> entry = it.next();
            MultipartFile mFile = entry.getValue();
            if (mFile.getSize() == 0)
                continue;
            // 上传的文件名
            fileName = mFile.getOriginalFilename();
            // 将文件名重新命名
            //String storeName = rename(fileName);
            // 上传文件的路径+名称
            //String noZipName = uploadDir + storeName;
            String noZipName = uploadDir+fileName;
            OutputStream outputStream = new FileOutputStream(noZipName);
            FileCopyUtils.copy(mFile.getInputStream(), outputStream);
            Map<String, Object> map = new HashMap<String, Object>();
            // 固定参数值对
            map.put(FileOperateUtil.CONTENTTYPE, "application/octet-stream"); // 格式
            map.put(FileOperateUtil.CREATETIME, new Date()); // 创建日期
            map.put("path", uploadDir);
            map.put("allPath", noZipName);
            map.put("fileName", fileName);
            result.add(map);
        }
        return result;
    }

    /**
     * 文件下载
     *
     * @param fileName
     * @param response
     * @return
     */

    public static void downloadFile(String fileName, HttpServletResponse response) {

        if (fileName != null) {
            String uploadDir = PropertiesUtil.initProperties("config/config.properties").getProperty("uploadDir");//得到文件所在位置
            String realPath = uploadDir + fileName;
            File file = new File(realPath);
            if (file.exists()) {
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));// 设置文件名
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}