package arithmetic.endOfMonth.day23;

/**
 * @author Silence_w
 */
public class offer66 {

    public int[] constructArr(int[] a) {
        int len = a.length;
        if (len == 0) return new int[0];
        int [] re = new int[len];
        re[0] = 1;
        int tem = 1;
        for (int i = 1; i < a.length; i++) {
            re[i] = re[i-1]*a[i-1];
        }

        for (int j =len -2;j>= 0;j--){
            tem *= a[j +1];
            re[j] *= tem;
        }
        return re;

    }

    public static void main(String[] args) {
        T t = new T(1);

    }

   static class T{

        int i;

       public T(int i) {
           this.i = i;
       }
   }
}
