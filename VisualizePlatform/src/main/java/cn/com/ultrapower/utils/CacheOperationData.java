package cn.com.ultrapower.utils;

import cn.com.ultrapower.single.SingleCacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class CacheOperationData {

    /**
     *缓存存储方法；
     *======注：接收的参数规定必须是Map类型======
     * @param cacheVal
     * @return
     * @throws Exception
     */
    public static String saveCache(Object cacheVal)throws Exception{
        String uuidKey = UUIDGenerator.getUUIDoffSpace();
        CacheManager singleCacheManager = SingleCacheManager.getCacheManager();
        Cache cache = singleCacheManager.getCache("excelExpCache");
        Element element = new Element(uuidKey,cacheVal);
        cache.put(element);
        return uuidKey;
    }

    /**
     * 缓存查询方法
     * @param cacheKey
     * @return
     * @throws Exception
     */
    public static Object getCache(String cacheKey) throws Exception{
        CacheManager singleCacheManager = SingleCacheManager.getCacheManager();
        Cache cache = singleCacheManager.getCache("excelExpCache");
        Element elementVal = cache.get(cacheKey);
        if (elementVal==null)
            return null;
        return elementVal.getObjectValue();
    }

}
