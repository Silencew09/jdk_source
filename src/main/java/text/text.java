package text;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silence_w
 */
public class text {
    public static void main(String[] args) {
//        Map<String,String>map =  new HashMap<>();
//        map.put("1","1");
//
//        Class<t1> t1Class = t1.class;
//        Class<? extends t> aClass = t1Class.asSubclass(t.class);
//        System.out.println(aClass);

    }

   static class t{

        public int re(Integer a){
            return  1;
        }
    }
    static class t1 extends  t{
        public int re1(Integer a){
            return  1;
        }
    }

}
