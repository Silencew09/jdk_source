package arithmetic.endOfMonth.day22;

/**
 * @author Silence_w
 */
public class offer56_1 {
    public int[] singleNumbers(int[] nums) {
        int ret = 0;
        for (int n : nums) {
            ret ^= n;
        }
        int div = 1;
        while ((div & ret) == 0) {
            div <<= 1;
        }
        int a = 0, b = 0;
        for (int n : nums) {
            if ((div & n) != 0) {
                a ^= n;
            } else {
                b ^= n;
            }
        }
        return new int[]{a, b};
    }

    public static void main(String[] args) {
        int i= 10;
        Silence:
         while (true){
             if (i == 1){
                 break ;
             }

             while (i >=0){
                 i --;
                 if (i == 1){
                     continue Silence;
                 }

             }
         }

         scan:
         for (int j = 0; j < 10; j++) {
             if (j == 2){
                 System.out.println(j);
                 break scan;
                // break;
             }
         }
        test();
    }

    public static void test(){

        String str = "boo:and:foo";
        String[] os = str.split("oo",5);

    }

}
