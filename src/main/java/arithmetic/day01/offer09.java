package arithmetic.day01;

import java.util.Stack;

/**
 * @author Silence_w
 */
public class offer09 {
   static class CQueue {

        Stack t1 ;
        Stack t2 ;

        public CQueue() {
            t1 = new Stack();
            t2 = new Stack();
        }

        public void appendTail(int value) {
            appendTailForStack(value);
        }

        public int deleteHead() {
          return   deleteHeadForStack();
        }

        private void appendTailForStack(int value){
            t1.push(value);
        }

        private int deleteHeadForStack(){

            if(t1.empty()){
                return -1;
            }
            while(!t1.empty()){
                t2.push(t1.pop());
            }
            int pop = (int) t2.pop();

            while(!t2.empty()){
                t1.push(t2.pop());
            }
            return pop;
        }


    }

    public static void main(String[] args) {
        CQueue cQueue = new CQueue();
        cQueue.appendTail(3);
        int i = cQueue.deleteHead();
        System.out.println(i);

    }

}
