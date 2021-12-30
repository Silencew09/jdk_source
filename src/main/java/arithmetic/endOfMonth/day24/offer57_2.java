package arithmetic.endOfMonth.day24;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silence_w
 */
public class offer57_2 {
    public int[][] findContinuousSequence(int target) {
        int i = 1,j = 2,s = 3;
        List<int[]> res = new ArrayList<>();
        while (i<j){
            if (s == target){
                int[] ans = new int[j-i +1];
                for (int i1 = i; i1 <=j; i1++) {
                    ans[i1 -i] = i1;
                }
                res.add(ans);
            }
            if (s>=target){
                s-=i;
                i++;
            }else{
                j++;
                s+=j;
            }

        }
        return res.toArray(new int[0][]);
    }
}
