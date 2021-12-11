package arithmetic.halfOfMonth.day12;

public class offer25 {

    public static class ListNode {
     int val;
     ListNode next;
     ListNode(int x) { val = x; }
 }

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode listNode = new ListNode(0);
            ListNode cru = listNode;
        while(l1 != null && l2 != null){
            if (l1.val > l2.val){
                cru.next = l2;
                l2 = l2.next;
            }else{
                cru.next = l1;
                l1 = l1.next;
            }
            cru = cru.next;
        }
        cru.next = l1!=null?l1:l2;
        return listNode.next;


    }

    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(2);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(4);
        ListNode listNode4 = new ListNode(1);
        ListNode listNode5 = new ListNode(3);
        ListNode listNode6 = new ListNode(4);
        listNode1.next = listNode2;
        listNode2.next = listNode3;
        listNode4.next = listNode5;
        listNode5.next = listNode6;
        mergeTwoLists(listNode1,listNode4);
    }
}
