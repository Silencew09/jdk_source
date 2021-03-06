package interview.sort;

import java.util.Comparator;

/**
 * @author Silence_w
 */
public class MyBubbleSort implements Sort{

    @Override
    public <T extends Comparable<T>> void sort(T[] list) {
        boolean swapped = true;
        for (int i = 1,len = list.length;i < len && swapped;++i){
            swapped = false;
            for (int j = 0 ; j < len - i;++j){
                if (list[j].compareTo(list[j+1])>0){
                    T temp = list[j];
                    list [j] = list[j+1];
                    list[j+1] =temp;
                    swapped = true;
                }
            }
        }
    }

    @Override
    public <T> void sort(T[] list, Comparator<T> comp) {
        boolean swapped = true;
        for (int i = 1, len = list.length;i<len && swapped;++i){
            swapped = false;
            for (int j = 0; j < len-i;++j){
                if (comp.compare(list[j],list[j+1])>0){
                    T temp = list[j];
                    list[j] = list[j+1];
                    list[j+1] = temp;
                    swapped = true;
                }
            }

        }
    }
}
