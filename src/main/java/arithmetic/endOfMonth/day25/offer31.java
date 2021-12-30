package arithmetic.endOfMonth.day25;

import java.util.Stack;

/**
 * @author Silence_w
 */
public class offer31 {

    public boolean validateStackSequences(int[] pushed, int[] popped) {

        Stack<Integer> stack = new Stack<>();
        int i = 0;
        for (int i1 : pushed) {
            stack.add(i1);
            while (!stack.isEmpty() && stack.peek() == popped[i]){
                stack.pop();
                i++;
            }
        }
        return stack.isEmpty();
    }
}
