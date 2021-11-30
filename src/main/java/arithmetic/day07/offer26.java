package arithmetic.day07;

import arithmetic.day06.offer32_1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Silence_w
 */
public class offer26 {

      public class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
  }

    public boolean isSubStructure(TreeNode A, TreeNode B) {
        if (A == null || B == null){
            return false;
        }
        if (isSame(A,B)||isSubStructure(A.left,B)||isSubStructure(A.right,B)){
            return true;
        }else {
            return false;
        }
    }
    public boolean isSame(TreeNode A, TreeNode B){
        if (B == null)return true;
        if (A==null||A.val!=B.val)return false;

        return isSame(A.left,B.left) && isSame(A.right,B.right);
    }
}
