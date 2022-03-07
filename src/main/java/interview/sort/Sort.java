package interview.sort;

import java.util.Comparator;

/**
 * @author Silence_w
 */
public interface Sort {
    public <T extends Comparable<T>> void sort(T[] list);

    public <T> void sort(T[] list, Comparator<T> comp);
}
