package arithmetic.day01;

import sun.reflect.generics.tree.Tree;

import java.util.Stack;
import java.util.TreeMap;

/**
 * @author Silence_w
 */
public class offer30 {
  static   class MinStack {

        /** initialize your data structure here. */

        Stack<Integer> t ,m;
        public MinStack() {
            t = new Stack();
            m = new Stack();
        }

        public void push(int x) {
            if (t.empty()||m.empty()){
                t.push(x);
                m.push(x);
                return;
            }
            t.push(x);
            if (m.peek()>x){
                m.push(x);
            }else{
                m.push(m.peek());
            }
        }

        public void pop() {
            t.pop();
            m.pop();
        }

        public int top() {
            return t.peek();
        }

        public int min() {
            return m.peek();
        }
    }

    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(2147483646);
        minStack.push(2147483646);
        minStack.push(2147483647);
        minStack.top();
        minStack.pop();
        minStack.min();
        minStack.pop();
        minStack.min();
        minStack.pop();
        minStack.push(2147483647);
        minStack.top();
        minStack.min();
        minStack.push(-2147483648);
        minStack.top();
        minStack.min();
        minStack.pop();
        minStack.min();



    }
}
