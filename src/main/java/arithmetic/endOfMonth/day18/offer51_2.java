package arithmetic.endOfMonth.day18;

public class offer51_2 {


    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    public boolean isBalanced(TreeNode root) {
        if (root == null){
            return true;
        }else{
            return Math.abs(hight(root.right) -hight(root.left)) <=1&&isBalanced(root.left)&&isBalanced(root.right);
        }
    }

    public int hight(TreeNode root){
        if (root == null){
            return 0;
        }else{
            return Math.max(hight(root.left),hight(root.right))+1;
        }
    }

}
