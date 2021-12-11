package arithmetic.halfOfMonth.day12;

import java.util.HashSet;
import java.util.Set;

public class offer52 {

    public  class ListNode {
     int val;
     ListNode next;
     ListNode(int x) { val = x; }
 }

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        Set<ListNode> nodeSet = new HashSet<>();
        while(headA!=null){
            nodeSet.add(headA);
            headA = headA.next;
        }
        while(headB!=null){
            if (!nodeSet.add(headB)){
                return headB;
            }
        }

        return null;
    }
    public ListNode getIntersectionNode2(ListNode headA, ListNode headB) {
       if  (headA == null || headB == null ) {
           return null;
       }
    ListNode  a = headA;
    ListNode  b = headB;
    while(a!=b){
        a = a == null ? headB : a.next;
        b = b == null ? headA : b.next;
    }

    return a;


    }

    }


