package arithmetic.day06;

import java.util.*;

/**
 * @author Silence_w
 */
public class offer32_1 {

    public class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode(int x) { val = x; }
 }
    public int[] levelOrder(TreeNode root) {
        List<Integer> objects = new ArrayList<>();
        if (root == null){
            return null;
        }
        Queue<TreeNode> list = new LinkedList<>();
        list.add(root);
        while (!list.isEmpty()){
            TreeNode remove = list.remove();
            objects.add(remove.val);
            if (remove.left!=null){
                list.add(remove.left);
            }
            if (remove.right!=null){
                list.add(remove.right);
            }
        }
        int [] tem = new int[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            tem[i] = objects.get(i);
        }
        return tem;
    }


}
