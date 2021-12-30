package arithmetic.endOfMonth.day20;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silence_w
 */
public class offer07 {
      public class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode(int x) { val = x; }
 }

    private Map<Integer,Integer> indexMap;

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        int n = inorder.length;
        indexMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indexMap.put(inorder[i],i);
        }
        return customBuildTree(preorder,0,n-1,0,n-1);
    }

    public TreeNode customBuildTree(int[] preorder ,int preorder_left,int preorder_right,int inorder_left,int inorder_right){
        if (preorder_left > preorder_right){
            return null;
        }

        int preorder_root = preorder_left ;
        int inorder_root = indexMap.get(preorder[preorder_root]);

        TreeNode root  =new TreeNode(preorder[preorder_root]);

        int size_left_subTree = inorder_root -inorder_left;

        root.left = customBuildTree(preorder,preorder_left+1,preorder_left+size_left_subTree,inorder_left,inorder_root-1);
        root.right = customBuildTree(preorder,preorder_left+size_left_subTree+1,preorder_right,inorder_root+1,inorder_right);
        return root;
    }

    public static void main(String[] args) {
        Calendar instance = Calendar.getInstance();
        Date time = instance.getTime();
        instance.add(Calendar.DATE,-1);
        System.out.println(time.getTime());
        System.out.println(instance.getTime().getTime());
    }

}
