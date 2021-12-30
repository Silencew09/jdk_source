package arithmetic.endOfMonth.day18;

public class offer51_1 {

     public class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
  }

    public int maxDepth(TreeNode root) {
         if (root == null) {
             return 0;
         }else {
           return  dfs(root,1);
         }
    }

    private int dfs(TreeNode root,int num){
         if (root == null){
             return num;
         }
         num ++;
        int left =dfs(root.left,num);
        int right =dfs(root.right,num);
        int max = Math.max(left, right);
        return max;
    }

}
