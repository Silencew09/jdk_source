package text;


import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silence_w
 */
public class text {
    public static void main(String[] args) {
        Map<String,String>map =  new HashMap<>();
        map.put("1","1");
        new Annotation(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        };

    }
}
