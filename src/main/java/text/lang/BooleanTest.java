package text.lang;

/**
 * @author Silence_w
 */
public class BooleanTest {
    public static void main(String[] args) {
//        Boolean yes = new Boolean("true");
//        Boolean no = new Boolean("yes");
//        System.out.println(yes);
//        System.out.println(no);
        logicalXorText();
    }

    public static void logicalAndText(){

        System.out.println(Boolean.logicalAnd(true,true)); ;
    }
    public static void logicalXorText(){

        System.out.println(Boolean.logicalXor(true,false)); ;
    }
}
