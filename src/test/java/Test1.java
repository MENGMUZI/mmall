import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : mengmuzi
 * create at:  2019-04-17  11:56
 * @description:
 */
public class Test1 {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put(null,"mengmuzi");
        map.put("mengmuzi",null);
        map.put(null,null);
        Hashtable hashtable = new Hashtable();
        hashtable.put("1",null);  //java.lang.NullPointerException
        System.out.println(hashtable);
        Map<String,String> currmap = new ConcurrentHashMap<>();
        currmap.put("mengmuzi",null);//java.lang.NullPointerException
        currmap.put(null,"mengmuzi");  //java.lang.NullPointerException
    }

}
