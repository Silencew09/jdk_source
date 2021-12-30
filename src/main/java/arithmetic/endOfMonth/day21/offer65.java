package arithmetic.endOfMonth.day21;

/**
 * @author Silence_w
 */
public class offer65 {
    public int add(int a, int b) {
        while(b!=0){
            int c = (a&b)<<1;
            a^=b;
            b = c;
        }
        return a;
    }
}
