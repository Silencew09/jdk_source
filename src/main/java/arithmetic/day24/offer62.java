package arithmetic.day24;

/**
 * @author Silence_w
 */
public class offer62 {


    public int lastRemaining(int n, int m) {
        return f(n,m);
    }

    private int f(int n, int m) {
        if (n == 1){
            return 0;
        }
        int x = f(n-1,m);
        return (m+x)%n;

    }

}
