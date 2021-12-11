package arithmetic.halfOfMonth.day11;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silence_w
 */
public class offer22 {

    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

    public ListNode getKthFromEnd(ListNode head, int k) {
        List<ListNode> tem = new ArrayList<>();
        while (head!=null){
            tem.add(head);
            head = head.next;
        }
        if (tem.size()-k+1<0){
            return head;
        }else{
            return tem.get(tem.size()-k+1) ;
        }



    }
}
