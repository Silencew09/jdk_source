package arithmetic.day10;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silence_w
 */
public class offer48 {

    public static int lengthOfLongestSubstring(String s) {

        Map<Character,Integer> tem = new HashMap<>();
        int result = 0,t= 0;
        for (int i = 0; i < s.length(); i++) {
            int j = tem.getOrDefault(s.charAt(i), -1);
            tem.put(s.charAt(i),i);
            t = t <i-j ? t+1:i-j;
            result = Math.max(result,t);
        }
            return result;
    }

    public static void main(String[] args) {
        lengthOfLongestSubstring("asdfvdade");
    }
}
