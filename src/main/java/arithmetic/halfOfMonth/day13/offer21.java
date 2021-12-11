package arithmetic.halfOfMonth.day13;

import java.util.Arrays;

/**
 * @author Silence_w
 */
public class offer21 {


    public static int[] exchange(int[] nums) {
        if (nums == null ||nums.length == 0 ||  nums.length == 1){
            return nums;
        }
        int left = 0;
        int right = nums.length-1;
        while (left != right){
            if (nums[left] % 2 == 0){
                nums[left] =nums[left] ^ nums[right] ;
                nums[right] = nums[right]^nums[left] ;
                nums[left] = nums[right]^nums[left];
                right --;
            }else{
                left ++;
            }

        }

        return nums;
    }

    public static void main(String[] args) {
       int [] a = {};
        int[] exchange = exchange(a);
        System.out.println(Arrays.toString(exchange));
    }
}
