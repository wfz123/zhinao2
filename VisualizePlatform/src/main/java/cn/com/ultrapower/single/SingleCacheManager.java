package cn.com.ultrapower.single;

import net.sf.ehcache.CacheManager;

public class SingleCacheManager {
    private static CacheManager manager = null;
    private SingleCacheManager(){
        String configPath = this.getClass().getClassLoader().getResource("config/ehcache.xml").getPath();
        manager = CacheManager.create(configPath);
    }
    public static CacheManager getCacheManager(){
        if(manager==null)
            new SingleCacheManager();
        return manager;
    }
}
