package org.smartloli.kafka.eagle.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niexiaomeng on 2017/7/18.
 */
public class TimeBasedCache<K, V> extends LRUCacheUtils<K, V> {

    private Integer exprePeriod = 60;

    private Map<K, Long> accessTimeMap;

    /**
     * Constructs an empty <tt>LRUCacheUtils</tt> instance with the specified
     * initial capacity, load factor and ordering mode.
     *
     * @param cacheSize the initial capacity
     *
     * @throws IllegalArgumentException if the initial capacity is negative or the load factor is
     *                                  nonpositive
     */
    public TimeBasedCache(int cacheSize, int expireSeconds) {

        super(cacheSize);
        exprePeriod = expireSeconds;
        accessTimeMap = new HashMap<K, Long>(cacheSize);
    }

    public synchronized V get(K key) {
        V result = map.get(key);
        if (result != null) {
            Long currentSeconds = System.currentTimeMillis() / 1000;
            Long accessTime = accessTimeMap.get(key);
            if (accessTime != null && currentSeconds - accessTime <= exprePeriod) {
                return result;
            } else {
                accessTimeMap.remove(key);
                return null;
            }
        }
        return null;

    }

    public synchronized void put(K key, V value) {
        map.put(key, value);
        Long currentSeconds = System.currentTimeMillis() / 1000;
        accessTimeMap.put(key, currentSeconds);
    }
}
