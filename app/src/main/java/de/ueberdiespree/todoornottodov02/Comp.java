package de.ueberdiespree.todoornottodov02;

import java.util.Comparator;


/**
 * Created by Ulrike on 18.09.2015.
 */
public class Comp implements Comparable<Items>, Comparator<Items> {

    public int compare(Items lhs, Items rhs) {
        return lhs.getDone().compareTo(rhs.getDone());
    }

    @Override
    public int compareTo(Items another) {
        return 0;
    }
}