package text.lang;

/**
 * @author Silence_w
 */
public class ByteTest {
    public static void main(String[] args) {
        parseByteTest();
    }
    public static void parseByteTest(){
        byte b = Byte.parseByte("+011", 2);
        System.out.println(b);
        Byte decode = Byte.decode("-2");
        System.out.println(decode);
        byte c = -80;
        int i = Byte.toUnsignedInt(c);
        long l = Byte.toUnsignedLong(c);
        System.out.println(l);

        System.out.println(i);

    }
}
