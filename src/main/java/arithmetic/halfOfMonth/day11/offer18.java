package arithmetic.halfOfMonth.day11;

/**
 * @author Silence_w
 */
public class offer18 {

     public class ListNode {
      int val;
      ListNode next;
      ListNode(int x) { val = x; }
  }

    public ListNode deleteNode(ListNode head, int val) {
         if (head == null){
             return head;
         }
         if (head.val == val){
             return head.next;
         }


        ListNode pre = head;
         ListNode tem = head;
         while (pre!=null){
             if(pre.val == val){
                 tem.next = tem.next.next;
                 return head;
             }
             tem = pre;
             pre = pre.next;
         }
        return head;
    }
}
