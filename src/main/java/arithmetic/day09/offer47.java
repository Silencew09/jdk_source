package arithmetic.day09;

/**
 * @author Silence_w
 */
public class offer47 {

    public static int maxValue(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        for(int j = 1; j < n; j++) // 初始化第一行
        {
            grid[0][j] += grid[0][j - 1];
        }
        for(int i = 1; i < m; i++) // 初始化第一列
        {
            grid[i][0] += grid[i - 1][0];
        }
        for(int i = 1; i < m; i++) {
            for(int j = 1; j < n; j++) {
                grid[i][j] += Math.max(grid[i][j - 1], grid[i - 1][j]);
            }
        }
        return grid[m - 1][n - 1];
    }

        public static int maxValue1(int[][] grid) {
            if (grid == null||grid.length==0||grid[0]==null||grid[0].length==0){
                return 0;
            }
            int row = grid.length-1;
            int column = grid[0].length-1;
            int i =row,j=column,max= grid[row][column];

            while (!(i ==0&& j==0)){
                if (i==0){
                    j--;
                    max +=grid[i][j];
                    continue;
                }
                if(j == 0){
                    i--;
                    max += grid[i][j];
                    continue;
                }
                if (grid[i][j-1]>grid[i-1][j]){
                    max+=grid[i][j-1];
                    j--;
                }else{
                    max+=grid[i-1][j];
                    i--;
                }
            }

            return max;

        }


    public static void main(String[] args) {
        int[][]g = new int[][]{{1,3,1},{1,5,1},{4,2,1}};
        int[][]g1 = new int[][]{
                {1,4,8,6,2,2,1,7},
                {4,7,3,1,4,5,5,1},
                {8,8,2,1,1,8,0,1},
                {8,9,2,9,8,0,8,9},
                {5,7,5,7,1,8,5,5},
                {7,0,9,4,5,6,5,6},
                {4,9,9,7,9,1,9,0}};
        maxValue(g1);
    }
}
