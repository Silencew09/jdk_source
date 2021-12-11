package arithmetic.day17;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author Silence_w
 */
public class offer40 {
    public int[] getLeastNumbers(int[] arr, int k) {
        Arrays.sort(arr);
        int[] ints1 = Arrays.copyOf(arr, k);
        return ints1;
    }
}
