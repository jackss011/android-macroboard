package com.jackss.ag.macroboard.utils;

import android.os.SystemClock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


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
        boolean isNewItem = map.put(item, expireTime) == null;
        update();

        return isNewItem;
    }

    /**
     * Add an item to the map and using default timeout
     * @param item the item to add
     * @return return true if the item wasn't already in the map
     */
    public boolean add(T item)
    {
        return add(item, defaultTimeout);
    }

    /** Return true if the item is in the map. Sanitize the map */
    public boolean contains(T item)
    {
        update();
        return map.containsKey(item);
    }

    /** Remove every expired entry */
    public boolean update()
    {
        final long systemTime = getAbsoluteTime();
        boolean updated = false;

        for(Iterator<Map.Entry<T, Long>> it = map.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry<T, Long> entry = it.next();
            if(entry.getValue() <= systemTime)
            {
                it.remove();
                updated = true;
            }
        }

        // for(Map.Entry<T, Long> entry: map.entrySet())
        // {
        //     if(entry.getValue() <= systemTime)
        //     {
        //         map.remove(entry.getKey());
        //         updated = true;
        //     }
        // }

        return updated;
    }

    /** Reset the map */
    public void clear()
    {
        map.clear();
    }

    /** Get a {@link Set} of the contained non-expired values */
    public Set<T> getList()
    {
        update();
        return map.keySet();
    }

    @Override
    public Iterator<T> iterator()
    {
        update();
        return map.keySet().iterator();
    }

    private long getAbsoluteTime()
    {
        return SystemClock.uptimeMillis();
    }
}
