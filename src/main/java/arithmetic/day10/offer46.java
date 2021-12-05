package arithmetic.day10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Silence_w
 */
public class offer46 {

    public static int translateNum(int num) {
        if (num < 10){
            return 1;
        }
       String tem = String.valueOf(num);
        int p1 = 0,p2 = 0,result = 1;
        for (int i = 0;i<tem.length();i++){
            p1 = p2;
            p2 = result;
            result = 0;
            result +=p2;
            if (i==0){
                continue;
            }
            String subTem = tem.substring(i-1,i+1);
            if (subTem.compareTo("25")<=0&&subTem.compareTo("10")>=0){
                result +=p1;
            }
        }
        return result;
    }

    private static LinkedList<Integer> getNumList(int num) {
        LinkedList<Integer>  tem = new LinkedList<>();
        int tem1;
        while (num !=0){
           tem1= num %10;
           num = num /10;
            tem.addFirst(tem1);
        }
        return tem;
    }


    public static void main(String[] args) {
        LinkedList<Integer> numList = getNumList(1);
        System.out.println(numList);
    }
}

