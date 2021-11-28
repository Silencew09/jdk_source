package arithmetic.day04;

public class offer53 {
    public static void main(String[] args) {
        int [] tre = {1,2,3,4,4,5};
        int search = search(tre, 4);
        System.out.println(search);
    }

    public static int search(int[] nums, int target) {

        int end = nums.length-1;
        int start = 0;
        int tem = 0;
        int re = 0;
        while(start < end){
            int mid = (start-end)/2+start;
            tem = mid;
            if (mid > target){
                start = mid;
            }else if(mid < target){
                end = mid;
            }else{
                re ++;
                break;
            }

        }
        if (tem + 1 >=nums.length){
            return re;
        }
        for (int i = tem+1; i < nums.length; i++) {
            if (nums[i] == target){
                re++;
            }
        }
        if (tem - 1 < 0){
            return re;
        }
        for (int i = tem-1; i >=0 ; i--) {
            if (nums[i] == target){
                re++;
            }
        }

        return re;
    }




}
