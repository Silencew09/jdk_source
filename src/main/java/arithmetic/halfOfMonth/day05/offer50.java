package arithmetic.halfOfMonth.day05;

import java.util.*;

/**
 * @author Silence_w
 */
public class offer50 {

    public static char firstUniqChar(String s) {
        if (s == null){
            return ' ';
        }
        char[] charsS = s.toCharArray();
        if (charsS.length == 1){
            return charsS[0];
        }
        Map<Character,Integer> sign = new TreeMap<>();
        char tem =  ' ';
        for (int i = 0; i < charsS.length; i++) {
            if (sign.containsKey(charsS[i])){
                Integer integer = sign.get(charsS[i]);
                integer+=1;
                sign.put(charsS[i],integer);
            }else{
                sign.put(charsS[i],1);
            }
        }
        for (int i = 0; i < charsS.length; i++) {
            Integer integer = sign.get(charsS[i]);
            if (integer == 1){
                tem = charsS[i];
                break;
            }
        }
        return tem ;
    }

    public static void main(String[] args) {
        String s= "leetcode";
        char c = firstUniqChar(s);
        System.out.println(c);
    }
}
