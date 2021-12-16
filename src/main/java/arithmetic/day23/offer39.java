package arithmetic.day23;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silence_w
 */
public class offer39 {

    public static int majorityElement(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length/2];
    }

    public static void main(String[] args) {
        int [] re = {1};
        majorityElement(re);
        int te = 0x61c88647;
        System.out.println(te);
    }
}
