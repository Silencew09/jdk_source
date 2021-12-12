package arithmetic.day19;

public class offer68_2 {


      public class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode(int x) { val = x; }
 }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
       TreeNode ancestor = root;
       while(true){
           if (ancestor.val>p.val && ancestor.val>q.val){
               ancestor = ancestor.left;
           }else  if (ancestor.val<p.val && ancestor.val<q.val){

               ancestor = ancestor.right;
           }else{
               break;
           }

       }
       return ancestor;

    }
}
