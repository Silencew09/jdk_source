package arithmetic.day03;

/**
 * @author Silence_w
 */
public class offer58 {

    public static String reverseLeftWords(String s, int n) {
        char[] chars = s.toCharArray();
        String s1 = new String(chars, 0, n);
        String s2 = new String(chars, n, chars.length-n);
        return s2 + s1;
    }

    public static void main(String[] args) {
        System.out.println(reverseLeftWords("12345678",3));
    }
}
