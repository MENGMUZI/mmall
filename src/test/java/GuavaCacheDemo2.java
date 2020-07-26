import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author : mengmuzi
 * create at:  2019-04-16  12:58
 * @description: 使用Callable方法
 */
public class GuavaCacheDemo2 {
    static Cache<String, String> testCache = CacheBuilder.newBuilder()
            .maximumSize(3)
            .build();
    public static void main(String[] args){
        testCache.put("1234","45");

        System.out.println(testCache.getIfPresent("key6"));
        try {

            System.out.println(testCache.get("123", new Callable<String>() {
                public String call() throws Exception {
                    return "134";
            }
            }));

            System.out.println(testCache.get("1234", new Callable<String>() {
                public String call() throws Exception {
                    return "134";
            }
            }));
            System.out.println(testCache.get("mengmuzi", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return "hehe";
                }
            }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
