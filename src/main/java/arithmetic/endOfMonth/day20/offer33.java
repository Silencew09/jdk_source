package arithmetic.endOfMonth.day20;

import java.util.Stack;

/**
 * @author Silence_w
 */
public class offer33 {

    //单调栈
    public boolean verifyPostorder(int[] postorder) {
        Stack<Integer>  stack = new Stack<>();
        int root = Integer.MAX_VALUE;
        for (int i = 0; i < postorder.length; i++) {
            if (postorder[i] >root)
            {return false;}
            while (!stack.empty() && stack.peek() > postorder[i]){
                root = stack.pop();
            }
            stack.add(postorder[i]);
        }
        return true;
    }
}
