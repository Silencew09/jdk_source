package arithmetic.day04;

import java.util.Random;

public class offer53 {
    public static void main(String[] args) {
        int [] tre = {5,7,7,8,8,10};
        int search = search(tre, 6);
        System.out.println(search);
    }

    public static int search(int[] nums, int target) {
        int re =0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i]==target){
                re++;
            }
        }
        return re;
    }

    public static void check(){
        Random r = new Random(1);
        for (int i = 0; i < 5; i++) {
            int i1 = r.nextInt(50);
            int[] ints = new int[i1];
            for (int i2 = 0; i2 < ints.length; i2++) {
                ints[i2] = r.nextInt();
            }
            search(ints,r.nextInt());
        }

    }




}
