package arithmetic.day22;

/**
 * @author Silence_w
 */
public class offer56_2 {

        public int singleNumber(int[] nums) {
            int ones = 0, twos = 0;
            for(int num : nums){
                ones = ones ^ num & ~twos;
                twos = twos ^ num & ~ones;
            }
            return ones;
        }


}
