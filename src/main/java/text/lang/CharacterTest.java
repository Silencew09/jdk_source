package text.lang;

/**
 * @author Silence_w
 */
public class CharacterTest {
    public static void main(String[] args) {
        reverseBytesTest();
    }

    public static void filed(){
        char u = 1;
        int i = Character.hashCode(u);
        System.out.println(u);
        System.out.println(i);
    }

    public static void isMirroredText(){
        boolean mirrored = Character.isMirrored('a');
        System.out.println(mirrored);
    }

    public static void reverseBytesTest(){
        char q = Character.reverseBytes('q');
        System.out.println(q);
    }


}
