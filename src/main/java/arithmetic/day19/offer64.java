package arithmetic.day19;

public class offer64 {

    public int sumNums(int n) {

       boolean isSumNums= n>0 && (n += sumNums(--n))>0;
        return n;
    }
}
