package cn.com.ultrapower.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties initProperties(String path){
        Properties prop =  new  Properties();
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
        try  {
            prop.load(in);
        }  catch  (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
