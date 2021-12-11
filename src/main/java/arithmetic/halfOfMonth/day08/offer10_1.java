package arithmetic.halfOfMonth.day08;

/**
 * @author Silence_w
 */
public class offer10_1 {

    public static int fib1(int n) {
        if (n == 0 ){
            return  0;
        }
        if (n == 1 ){
            return  1;
        }
        return fib1(n-1)+fib1(n-2);
    }
    public static int fib(int n) {
        final int tem = 1000000007;
        if (n<2){
            return n;
        }
       int n1 = 0,n2 = 1,n3= 0;
        for (int i = 2; i <=n; i++) {
            n3 = (n1+n2)%tem;
            n1 = n2;
            n2 = n3;
        }
        return n3;
    }

    public static void main(String[] args) {
        int fib = fib(44);
        int fib1 = fib1(44);
        System.out.println(fib);
        System.out.println(fib1);
    }
}
