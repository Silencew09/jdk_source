package arithmetic.day09;

import java.util.Arrays;

/**
 * @author Silence_w
 */
public class offer42 {

    public static int maxSubArray(int[] nums) {
        int pre = 0,maxAns = nums[0];
        for (int num : nums) {
            pre = Math.max(pre+num,num);
            maxAns = Math.max(maxAns,pre);
        }
        return maxAns;
    }

    public static void main(String[] args) {
        int[] nums = {-2,1,-3,4,-1,2,1,-5,1,4};
        maxSubArray(nums);
    }
}
