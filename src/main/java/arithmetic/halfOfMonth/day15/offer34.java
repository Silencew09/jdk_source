package arithmetic.halfOfMonth.day15;

import java.util.*;

/**
 * @author Silence_w
 */
public class offer34 {
 class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode() {}
      TreeNode(int val) { this.val = val; }
      TreeNode(int val, TreeNode left, TreeNode right) {
          this.val = val;
          this.left = left;
          this.right = right;
      }
  }

    List<List<Integer>> ret = new LinkedList<>();
    Deque<Integer> path = new LinkedList<>();
    public List<List<Integer>> pathSum(TreeNode root, int target) {
        dfs(root,target);
        return ret;
    }

    private void dfs(TreeNode root , int target){
        if (root == null){
            return ;
        }
        path.offerLast(root.val);
        target -=root.val;
        if (root.left == null && root.right == null &&target == 0){
            ret.add(new LinkedList<>(path));
        }
        dfs(root.left,target);
        dfs(root.right,target);
        path.pollLast();
    }

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(date);
        changeTime(date);
        System.out.println(date);
    }

    public static void changeTime(Date date){

        date.setTime(0);
    }
}
