package arithmetic.endOfMonth.day21;

/**
 * @author Silence_w
 */
public class offer15 {

    public int hammingWeight(int n) {
        int res = 0;
        while(n!=0){
            n &=(n-1);
            res++;
        }
        return res;
    }


}


