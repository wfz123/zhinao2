package cn.com.ultrapower.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/4/24.读取客户端上传的加密文件进行解密
 */
public class ReadFile {
    /**
     * 读取服务端的文件，得到加密的字符串
     * @param path
     * @return
     */
    public static String readServiceFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                stringBuffer.append(tempString);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return stringBuffer.toString();
    }





    public static List<String> readFile(String path) {
       // File file = new File("H:\\company\\110警情扫黑除恶智能研判产品0314\\05_产品工程\\后台代码\\LicenseManager\\src\\main\\webapp\\aa.txt");
//        File file = new File("C:\\Users\\admin\\Desktop\\测试文件.txt");
        File file = new File(path);
        List<String> list = new ArrayList();
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                //stringBuffer.append(tempString+";");
                list.add(tempString);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }

    /**
     * 读取客户端信息文件
     * @param path
     * @return
     */
    public static String readHostFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                stringBuffer.append(tempString);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args){
        try {
            String hostinfo = Base64Utils.encode("hostid:132135;startTime:14721251221;endTime:14781251842".getBytes());
            String module = Base64Utils.encode("modelId:0000000001,0000000002,000000003,000000004,000000005,000000006,000000007,000000008,000000009,000000010,000000011,000000012,000000013,000000014".getBytes());
           /* List<String> list = readFile();
            System.out.println(list.get(0));
            System.out.println(list.get(1));*/
            System.out.println(hostinfo+"这个是加密1");
            System.out.println(module);

         /* String hostinfoDecode = new String(Base64Utils.decode(list.get(0)));
            String moduleDefo = new String(Base64Utils.decode(list.get(1)));*/
            String hostinfoDecode = new String(Base64Utils.decode(hostinfo));
            String moduleDefo = new String(Base64Utils.decode(module));
            System.out.println(hostinfoDecode);
            System.out.println(moduleDefo);
             /* System.out.println(hostinfo+"这个是第一");
            System.out.println(module+"这个是第二个");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
