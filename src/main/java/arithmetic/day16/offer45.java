package arithmetic.day16;

import java.util.Arrays;

/**
 * @author Silence_w
 */
public class offer45 {

    public String minNumber(int[] nums) {
    String [] str = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            str[i] = String.valueOf(nums[i]);
        }
        Arrays.sort(str,(x,y)->(x+y).compareTo(y+x));
        StringBuffer result = new StringBuffer();
        for (String s : str) {
            result.append(s);
        }
        return result.toString();
    }
}
