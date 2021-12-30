package arithmetic.endOfMonth.day16;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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

    public static void main(String[] args) {
        Calendar instance1 = Calendar.getInstance();
        instance1.setTime(new Date(1635753187000L));
        Calendar instance2 = Calendar.getInstance();

        instance1.add(Calendar.MONTH,-1);
        System.out.println(instance1.getTime().getTime());
        instance2.add(Calendar.DAY_OF_MONTH,-1);
        System.out.println(instance2.getTime().getTime());
    }
}
