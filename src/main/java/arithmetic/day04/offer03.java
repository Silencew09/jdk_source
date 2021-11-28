package arithmetic.day04;

import java.util.HashMap;
import java.util.Map;

public class offer03 {
    public static void main(String[] args) {

    }
    public int findRepeatNumber(int[] nums) {
       Map<Integer, Integer> hashMap = new HashMap<>();

        for(int i= 0;i<nums.length;i++){
            if (hashMap.containsKey(nums[i])){
                return nums[i];
            }
            hashMap.put( nums[i],i);
        }

        return -1;
    }
}
