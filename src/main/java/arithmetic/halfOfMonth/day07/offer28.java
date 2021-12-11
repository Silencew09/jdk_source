package arithmetic.halfOfMonth.day07;

/**
 * @author Silence_w
 */
public class offer28 {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    public boolean isSymmetric(TreeNode root) {
        if (root == null){
            return true;
        }
        return isSame(root.left,root.right);
    }
    public boolean isSame(TreeNode left,TreeNode right){
        if (left == null && right == null
        ){return true;}

        if (left ==null||right==null||left.val != right.val)return false;

        return isSame(left.left,right.right)&&isSame(left.right,right.left);
    }
}
