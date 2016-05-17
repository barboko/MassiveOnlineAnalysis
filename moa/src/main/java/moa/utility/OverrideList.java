package moa.utility;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class allows us to keep predictions in the memory, based on a given window
 * @param <T>
 */
public class OverrideList<T extends Object> {
    //region Members
    private int limit;
    protected List<T> data;
    //endregion

    //region Properties
    public int getLimit() {return limit;}
    public int size() { return data.size(); }
    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == limit; }
    //endregion

    //region Constructors
    public OverrideList(int capacity) {
        if((this.limit = capacity) <= 0)
            throw new IllegalArgumentException("capacity must be > 0");
        data = new LinkedList<>();
    }
    //endregion

    //region Methods
    public void add(T obj) {
        boolean full = isFull();
        data.add(0, obj);

        if(full)
            data.remove(size()-1);
    }

    public T get(int index) {
        return data.get(index);
    }

    public Iterator<T> iterator() {
        return data.iterator();
    }
    //endregion
}
