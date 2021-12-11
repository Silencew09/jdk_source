package arithmetic.halfOfMonth.day04;

/**
 * @author Silence_w
 */
public class offer53_ {
    public static int missingNumber(int[] nums) {

        int len = nums.length;
        if (len == 1){
            if (nums[0] == 0){
                return 1;
            }else{
                return 0;
            }
        }
        int left = 0 ;
        int right = len-1;
        while (left <= right){
            int mid = (left +right) /2 ;
            if (nums[mid] == mid){
                left = mid+1;
            }else{
                right = mid -1;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        int [] tre = {1,2,3};
        int i = missingNumber(tre);
        System.out.println(i);
    }
}
