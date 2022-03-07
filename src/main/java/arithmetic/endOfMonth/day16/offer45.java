package arithmetic.endOfMonth.day16;

import java.util.*;

/**
 * @author Silence_w
 */
public class offer45 {

    public String minNumber(int[] nums) {
    String [] str = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            str[i] = String.valueOf(nums[i]);
        }
        Arrays.sort(str,(x,y)->(x+y).compareTo(y+x));
        StringBuffer result = new StringBuffer();
        for (String s : str) {
            result.append(s);
        }
        return result.toString();
    }

    public static void main(String[] args) {
//        Calendar instance1 = Calendar.getInstance();
//        instance1.setTime(new Date(1635753187000L));
//        Calendar instance2 = Calendar.getInstance();
//
//        instance1.add(Calendar.MONTH,-1);
//        instance2.add(Calendar.DAY_OF_MONTH,1);
//        System.out.println(differenceForDay(instance1.getTime(),instance2.getTime()));
        String s1 = "Programming";
        String s2 = new String("Programming");
        String s3 = "Program";
        String s4 = "ming";
        String s5 = "Program" + "ming";
        String s6 = s3 + s4;
//        System.out.println(s1 == s2);//false
//        System.out.println(s1 == s5);//true
//        System.out.println(s1 == s6);//false
        System.out.println(s5 == s6);//false
        System.out.println(s1 == s6.intern());//true
        System.out.println(s2 == s2.intern());//false


    }
    public static long differenceForDay(Date startTime, Date endTime) {
        if (startTime.compareTo(endTime) > 0 ){
            throw new IllegalArgumentException();
        }
        long start = startTime.getTime();
        long end = endTime.getTime();
        if ((end-start)%(1000*60*60*60*24)==0 ){
            return (end-start)/(1000*60*60*60*24);
        }else {
            return (end-start)/(1000*60*60*60*24)+1;
        }

    }
}
