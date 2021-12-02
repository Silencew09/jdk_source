package text.lang;

/**
 * @author Silence_w
 */
public class CloneableTest {
    public static void main(String[] args) {
        Test test = new Test();
        try {
            Object clone = test.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("ero....");
        }
        String a = "a";
        String b = "a";
        System.out.println(a.equals(b)&&a==b);
    }
    static class Test {
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
