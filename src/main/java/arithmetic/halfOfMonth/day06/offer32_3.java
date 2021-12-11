package arithmetic.halfOfMonth.day06;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Silence_w
 */
public class offer32_3 {

    public static class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode(int x) { val = x; }
 }
    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();

        if (root == null){
            return null;
        }
        Queue<TreeNode> list = new LinkedList<>();
        list.add(root);
        while (!list.isEmpty()){
            LinkedList<Integer> objects = new LinkedList<>();
                for (int i = list.size(); i >0; i--) {
                    TreeNode remove = list.poll();
                    if (result.size()%2==0){objects.addLast(remove.val);}
                    else{
                        objects.addFirst(remove.val);
                    }
                    if (remove.left!=null){
                        list.add(remove.left);
                    }
                    if (remove.right!=null){
                        list.add(remove.right);
                    }
                }

            result.add(objects);
        }
      return   result ;
    }

    public static void main(String[] args) {
        TreeNode treeNode1 = new TreeNode(3);
        TreeNode treeNode2 = new TreeNode(9);
        TreeNode treeNode3 = new TreeNode(20);
        TreeNode treeNode4 = new TreeNode(15);
        TreeNode treeNode5= new TreeNode(7);
        treeNode1.left = treeNode2;
        treeNode1.right = treeNode3;
        treeNode3.left = treeNode4;
        treeNode3.right = treeNode5;
        List<List<Integer>> lists = levelOrder(treeNode1);

    }


}
