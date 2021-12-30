package arithmetic.endOfMonth.day27;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Silence_w
 */
public class offer59_1 {

    public int[] maxSlidingWindow(int[] nums, int k) {
    if(nums.length == 0 || k == 0) return new int[0];
    Deque<Integer> deque = new LinkedList<>();
    int[] res = new int[nums.length - k +1];
        for (int j = 0,i = 1 -k; j < nums.length; j++,i++) {

            if (i>0&&deque.peekFirst() == nums[i-1]){
                deque.removeFirst();
            }
            while(!deque.isEmpty() && deque.peekLast() <nums[j]){
                deque.removeLast();
            }
            deque.addLast(nums[j]);
            if (i>=0){
                res[i] = deque.peekFirst();
            }

        }
        return res;
    }
}
