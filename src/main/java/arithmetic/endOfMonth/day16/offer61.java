package arithmetic.endOfMonth.day16;

import java.util.Arrays;
import java.util.Calendar;

/**
 * @author Silence_w
 */
public class offer61 {
    public static void main(String[] args) {
        int [] re = {9,1,13,2,13};
        boolean straight = isStraight(re);
        System.out.println(straight);
        Calendar instance = Calendar.getInstance();
        System.out.println(instance.getTime());
    }
    public static boolean isStraight(int[] nums) {
        int joker = 0;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length-1; i++) {
            if (nums[i]==0) joker++;
            else if (nums[i] == nums[i+1]) return false;
        }
        return nums[nums.length-1]-nums[joker] <5;
    }
}
