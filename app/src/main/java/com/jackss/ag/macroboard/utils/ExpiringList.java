package com.jackss.ag.macroboard.utils;

import android.os.SystemClock;

import java.util.*;


/**
 *
 */
public class ExpiringList<T> implements Iterable<T>
{
    private HashMap<T, Long> map = new HashMap<>();

    private int defaultTimeout;


    public ExpiringList(int defaultTimeout)
    {
        this.defaultTimeout = defaultTimeout;
    }


    /**
     * Add an item to the map. If the item is already in it only the timeout is updated.
     *      @param item the item to add
     *      @param timeout the time in seconds the item will remain on the map
     *      @return return true if the item wasn't already in the map
     */
    public boolean add(T item, int timeout)
    {
        if(timeout <= 0) throw new IllegalArgumentException("timeout is minor than 0");

        long expireTime = getAbsoluteTime() + timeout * 1000;
        return map.put(item, expireTime) == null;
    }

    /**
     * Add an item to the map and using default timeout passed in the constructor.
     * @param item the item to add
     * @return return true if the item wasn't already in the map
     */
    public boolean add(T item)
    {
        return add(item, defaultTimeout);
    }

    /** Return true if the item is in the map. update() should be called before this. */
    public boolean contains(T item)
    {
        return map.containsKey(item);
    }

    /** Remove every expired entry, returning a Set containing removed objects. */
    public Set<T> update()
    {
        final long systemTime = getAbsoluteTime();
        Set<T> removed = new HashSet<>();

        for(Iterator<Map.Entry<T, Long>> it = map.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry<T, Long> entry = it.next();
            if(entry.getValue() <= systemTime)
            {
                it.remove();
                removed.add(entry.getKey());
            }
        }

        return removed.size() > 0 ? removed : null;
    }

    /** Remove all entries */
    public void clear()
    {
        map.clear();
    }

    /** Get a {@link Set} of the contained values. update() should be called before. */
    public Set<T> getList()
    {
        return map.keySet();
    }

    @Override
    public Iterator<T> iterator()
    {
        return map.keySet().iterator();
    }

    private long getAbsoluteTime()
    {
        return SystemClock.uptimeMillis();
    }
}
