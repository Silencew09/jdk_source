package interview.sort;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author Silence_w
 */
public class SortTest {
    public static void main(String[] args) {
        Integer[] a = {4,1,2,6,9,5,8};
        MyBubbleSort myBubbleSort = new MyBubbleSort();
        myBubbleSort.sort(a);
        System.out.println(Arrays.toString(a));
    }
}
