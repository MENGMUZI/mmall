package com.mmall.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author : mengmuzi
 * create at:  2019-02-28  22:11
 * @description: token本地缓存，使用guava缓存实现
 *
 * 使用token机制的身份验证方法，在服务器端不需要存储用户的登录记录。大概的流程：
 * 客户端使用用户名和密码请求登录。服务端收到请求，验证用户名和密码。验证成功后，服务端会生成一个token，然后把这个token发送给客户端。
 * 客户端收到token后把它存储起来，可以放在cookie或者Local Storage（本地存储）里。客户端每次向服务端发送请求的时候都需要带上服务端发给的token。
 * 服务端收到请求，然后去验证客户端请求里面带着token，如果验证成功，就向客户端返回请求的数据。
 *
 */
public class TokenCache {
    //设置一个token的前缀常量
    public static final String TOKEN_PRFIX = "token_";
    // 创建logback的logger
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    // 声明一个静态的内存块,guava里面的本地缓存

    //构建本地缓存，调用链的方式 ,1000是设置缓存的初始化容量，maximumSize是设置缓存最大容量，当超过了最大容量，guava将使用LRU算法（最少使用算法），来移除缓存项
    //expireAfterAccess(12,TimeUnit.HOURS)设置缓存有效期为12个小时
    private static LoadingCache<String ,String> localCache = CacheBuilder
            .newBuilder()
            .initialCapacity(1000)
            .maximumSize(10000)
            .expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    //为什么要把return的null值写成字符串，因为到时候用null去.equal的时候，会报空指针异常
                    return "null";
                }
            });

    //添加本地缓存

    public static void setKey(String key,String value){
        localCache.put(key, value);
    }

    //得到本地缓存
    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }

}
