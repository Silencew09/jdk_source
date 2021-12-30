package arithmetic.endOfMonth.day17;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silence_w
 */
public class offer41 {
    static class MedianFinder {
        List<Integer> res ;
        int size;

        public MedianFinder() {
            res = new ArrayList<>();
            size = 0;
        }

        public void addNum(int num) {
            int tem = -1;
            for (int i = 0; i < res.size(); i++) {
                if (num <res.get(i)){
                    tem = i;
                    break;
                }
            }
            if (tem == -1){
                res.add(num);
            }else{
                res.add(tem,num);
            }

            size++;
        }

        public double findMedian() {
            if (size % 2 == 0){
                int right = size/2;
                int left = right -1;
                return ((double)(res.get(right) + res.get(left)))/2;
            }else {
                int mid = size/2;
                return res.get(mid);
            }
        }
    }

}
