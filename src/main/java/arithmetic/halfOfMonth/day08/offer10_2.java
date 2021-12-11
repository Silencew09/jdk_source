package arithmetic.halfOfMonth.day08;

/**
 * @author Silence_w
 */
public class offer10_2 {
    final static int mod = 1000000007;

    public int numWays(int n) {
        final int tem = 1000000007;
        if (n<2){
            return 1;
        }
        int n1 = 1,n2 = 1,n3= 0;
        for (int i = 2; i <=n; i++) {
            n3 = (n1+n2)%tem;
            n1 = n2;
            n2 = n3;
        }
        return n3;
    }
}
