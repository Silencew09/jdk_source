package arithmetic.day13;

/**
 * @author Silence_w
 */
public class offer57 {

    public int[] twoSum(int[] nums, int target) {
        int [] result = new int[2];
        if (nums == null || nums.length == 0 ||nums.length == 1){
            return result;
        }

        int left = 0;
        int right = nums.length -1;
        while (left != right){
            if (nums[left] + nums[right] == target){
                result[0] = nums[left];
                result[1] = nums[right];
                break;
            }else if(nums[left] + nums[right] < target){
                left++;
            }else{
                right ++;
            }

        }

       return   result;
    }
}
