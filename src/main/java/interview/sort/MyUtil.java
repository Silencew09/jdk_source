package interview.sort;

import java.io.FileInputStream;
import java.util.Comparator;

/**
 * @author Silence_w
 */
public class MyUtil {

    public static <T extends Comparable<T>> int binarySearch (T[] list,T key){
        return binarySearch(list,0,list.length -1 ,key);

    }

    public static <T extends Comparable<T>> int binarySearch(T[] x, int low, int high, T key) {
        if (low <= high){
            int mid = low + ((high - low) >>1);
            if (key.compareTo(x[mid]) == 0){
                return mid;
            }else if (key.compareTo(x[mid])< 0 ){
                return binarySearch(x,low,mid-1,key);
            }else{
                return binarySearch(x,mid+1,high,key);
            }
        }
        return -1;

    }

    public static <T extends Comparable<T>> int binarySearch(T[] x , T key , Comparator<T> comp){
        int low = 0;
        int hight = x.length - 1 ;
        while (low <= hight){
            int mid = (low + hight) >> 2;
            int cmp =comp.compare(x[mid],key);
            if (cmp < 0 ){
                low = mid +1;
            }else if (cmp > 0){
                hight = mid -1;
            }else{
                return mid;
            }
        }
        return -1;
    }
}
