package text.lang;

/**
 * @author Silence_w
 */
public class DoubleTest {
    public static void main(String[] args) {
        Double a = Math.pow(10.0d,-4d);
        String s = a.toString();
        System.out.println(s);
        String s1 = Double.toHexString(0d);
        System.out.println(s1);
    }
}
