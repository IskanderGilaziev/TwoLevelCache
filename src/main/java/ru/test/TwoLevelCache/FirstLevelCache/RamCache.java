package ru.test.TwoLevelCache.FirstLevelCache;

import org.apache.log4j.Logger;

import ru.test.TwoLevelCache.CacheInterface.Cache;
import ru.test.TwoLevelCache.CacheInterface.ObjectCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 The class that implements the first level cache
 */
public class RamCache<Key, Value> implements Cache<Key, Value>, ObjectCache<Key,Value> {

    /**
     *
     * @param maxSizeCache Maximum first level cache size
     *
     */

    private static final Logger logger = Logger.getLogger(RamCache.class);
    private Map<Key, Value> firstLevelMap;
    private Map<Key,Integer> numberOfRequestObjectInRam;
    private  int FIRST_REQUEST = 1;
    private  int maxSizeCache;

    public RamCache(int maxSizeCache) {
        this.maxSizeCache = maxSizeCache;
        logger.info("Initialization RamCache.");
        firstLevelMap = new ConcurrentHashMap<>(maxSizeCache);
        numberOfRequestObjectInRam = new ConcurrentHashMap<>();

    }

    /**
     * Method counts average number of requests to the object
     * @return sum result
     */
    public long getMiddleRequestFirstLevelCache(){
        int sumMiddle = 0;
       Set<Key> resultKeysTemp = new HashSet<>(numberOfRequestObjectInRam.keySet());
        for(Key key : resultKeysTemp){
            sumMiddle += getObjectCallNumber(key);
        }
        int result = sumMiddle/2;
        if(result==0){
            return result = firstLevelMap.size()/2;
        }
        return  result;
    }

    /**
     * Entering the key and object values ​​in the first level cache
     * @param key key
     * @param value object
     */
    @Override
    public synchronized void putInCache( Key key, Value value) {
        numberOfRequestObjectInRam.put(key,FIRST_REQUEST);
        numberOfRequestObjectInRam.get(key);
        logger.info("The first request and  key: "+ key+ " writing.");
        firstLevelMap.put(key,value);
        logger.info("Key and Value are put in first level cache");

    }

    /**
     * Clear first level cache
     */
    @Override
    public void clearCache() {
        firstLevelMap.clear();
        logger.info("RamCache clear.");
        numberOfRequestObjectInRam.clear();
        logger.info("Number of calls  Object in ram - clear.");
    }

    /**
     * The method returns the requested object by key
     * @param key key
     * @return object
     */

    @Override
    public synchronized Value getObjectCache(Key key) {
        logger.info("Started find object in RamCache");
        int newRequest = numberOfRequestObjectInRam.get(key);
        numberOfRequestObjectInRam.put(key,++newRequest);
        logger.info("Put in Ram next request: "+ newRequest + " the object: "+ firstLevelMap.get(key));
        logger.info("Return object");
        return firstLevelMap.get(key);
    }

    /**
     * Removing an object from the first level cache by key
     * @param key key
     */
    @Override
    public synchronized void deleteObjectCache(Key key) {
        logger.info("Start delete object.");
            firstLevelMap.remove(key);
            numberOfRequestObjectInRam.remove(key);
            logger.info("Object deleted.");
    }

    /**
     * Checking the key for entry in the list of keys
     * @param key key
     * @return true or false
     */

    @Override
    public boolean containsEnterKey(Key key) {
        logger.info("Return contains key (RAM).");
       return firstLevelMap.containsKey(key);
    }

    /**
     * Return the size of the first level cache
     * @return size first level cache
     */
    @Override
    public int sizeCache() {
        logger.info("Return ram size.");
        return firstLevelMap.size();
    }

    /**
     * Return of used keys
     * @return set of keys
     */
    @Override
    public Set<Key> getUsedKeys(){
        return numberOfRequestObjectInRam.keySet();
    }

    /**
     *Returns the number of the object request from the cache
     * @param key key
     * @return number of request the object
     */
    @Override
    public long getObjectCallNumber(Key key) {
        logger.info("Returns number of request the object");
        return numberOfRequestObjectInRam.get(key);
    }
}
