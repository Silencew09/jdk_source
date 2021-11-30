package arithmetic.day07;

/**
 * @author Silence_w
 */
public class offer27 {

      public class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
  }
    public TreeNode mirrorTree(TreeNode root) {
        if(root == null)
        {
            return null;
        }
        TreeNode leftNode = mirrorTree(root.left);
        TreeNode rightNode = mirrorTree(root.right);
        root.right = leftNode;
        root.left = rightNode;
        return root;
    }

}
