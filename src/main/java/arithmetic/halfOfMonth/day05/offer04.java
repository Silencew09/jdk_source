package arithmetic.halfOfMonth.day05;

/**
 * @author Silence_w
 */
public class offer04 {

    public static boolean findNumberIn2DArray(int[][] matrix, int target) {
        if (matrix ==  null || matrix.length ==0 || matrix[0].length==0){
            return false;
        }
        int row = matrix.length;
        int columns = matrix[0].length;
        int left = 0,right = columns-1;
        while (left<row&&right>=0){
            int num = matrix[left][right];
            if (num == target){
                return true;
            }else if(num >target){
                right--;
            }else if(num < right){
                left++;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] tem = new int[][]{{1,4}, {2,5}};
        boolean numberIn2DArray = findNumberIn2DArray(tem, 2);
        System.out.println(numberIn2DArray);
    }
}
