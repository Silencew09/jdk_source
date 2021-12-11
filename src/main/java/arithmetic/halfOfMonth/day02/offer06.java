package arithmetic.halfOfMonth.day02;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Silence_w
 */
public class offer06 {
 public static class ListNode {
     int val;
    ListNode next;
     ListNode(int x) { val = x; }
 }

    class Solution {
        public int[] reversePrint(ListNode head) {

            Stack<Integer> stack = new Stack();
            int i = 0;
            while (head != null) {
                stack.push(head.val);
                head = head.next;
                i++;
            }
            int[] tem = new int[i];
            i = 0;
            while (!stack.empty()) {
                Integer pop = stack.pop();
                tem[i++] = pop;
            }
            return tem;
        }

    }
    public static ListNode reverseList(ListNode head) {
        ListNode curr = head;
        ListNode tem =null;
        while (curr != null){
            ListNode next = curr.next;
            curr.next = tem;
            tem = curr;
            curr =next;
        }
        return tem;
    }
    Map<Node,Node> tem = new HashMap<>();
    public Node copyRandomList(Node head) {
        if (head == null) return null;
        if (!tem.containsKey(head)){
            Node node = new Node(head.val);
            tem.put(head,node);
            node.next = copyRandomList(head.next);
            node.random = copyRandomList(head.random);
        }
        return tem.get(head);

    }
    class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }

    public static void main(String[] args) {
        ListNode listNode = new ListNode(1);
        ListNode listNode1 = new ListNode(2);
        ListNode listNode2 = new ListNode(3);
        ListNode listNode3 = new ListNode(4);
        ListNode listNode4 = new ListNode(5);
        listNode.next = listNode1;
        listNode1.next =listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        reverseList(listNode);
    }


}
