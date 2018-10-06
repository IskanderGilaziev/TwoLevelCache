package ru.test.TwoLevelCache.TwoLevelCache;

import org.apache.log4j.Logger;
import ru.test.TwoLevelCache.CacheInterface.Cache;
import ru.test.TwoLevelCache.CacheInterface.LevelCache;
import ru.test.TwoLevelCache.CacheInterface.ObjectCache;
import ru.test.TwoLevelCache.FirstLevelCache.RamCache;
import ru.test.TwoLevelCache.SecondLevelCache.MemoryCache;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The class that implements the two level cache
 * @param <Key> key
 * @param <Value> object
 */
public class TwoLevelCache<Key extends Serializable,Value extends Serializable>
                                        implements LevelCache<Key,Value>,
                                                    Cache<Key,Value>,ObjectCache<Key,Value> {

    /**
     * @param numRequest request number of the object being accessed
     * @param levelChange limit level of the request to the object. Set in appContext.xml
     * @param maxSizeRam maximum first level cache size
     * @param maxSizeMemory  maximum second level cache size
     */

   private static final Logger logger = Logger.getLogger(TwoLevelCache.class);
   private RamCache<Key,Value> firstLevelCache;
   private MemoryCache<Key, Value> secondLevelCache;
   private long numRequest;
   private long levelChange;
   private int maxSizeRam;
   private int maxSizeMemory;

    public TwoLevelCache(int maxSizeRam, int maxSizeMemory, int levelChange) {
        logger.info("Initialization two-level cache.");
        this.maxSizeRam = maxSizeRam;
        this.maxSizeMemory = maxSizeMemory;
        firstLevelCache = new RamCache<>(maxSizeRam);
        secondLevelCache = new MemoryCache<>(maxSizeMemory);
        numRequest = 0;
        this.levelChange = levelChange;
    }

    /**
     *Change the cache level from first to second level.
     * The average number of requests to objects is compared to the number of requests to a specific object.
     * With a small number of requests to an object,
     * it is added to the second level cache and removed from the first level cache.
     */
    @Override
    public void updateFirstLevelCache() {
        long averageValueFirstLvlCache = firstLevelCache.getMiddleRequestFirstLevelCache();
        logger.info("Starting update first level cache");
        for (Key key : firstLevelCache.getUsedKeys()) {
            if (firstLevelCache.getObjectCallNumber(key) < averageValueFirstLvlCache) {
                secondLevelCache.putInCache(key, firstLevelCache.getObjectCache(key));
                firstLevelCache.deleteObjectCache(key);
                logger.info("Second level cache update.Object in first level cache deleted.");

            }
        }
    }

    /**
     * Change the cache level from the second level to the first.
     * The average number of requests to objects is compared to the number of requests to a specific object.
     * With a large number of requests to an object,
     * it is added to the first level cache and removed from the second level cache.
     */
    @Override
    public void updateSecondLevelCache() {
        logger.info("Starting update second level cache");
        long averageValueSecondLvlCache = secondLevelCache.getMiddleRequestSecondLevelCache();
        for(Key key : secondLevelCache.getUsedKeys()){
            if(secondLevelCache.getObjectCallNumber(key) > averageValueSecondLvlCache){
                firstLevelCache.putInCache(key,secondLevelCache.getObjectCache(key));
                secondLevelCache.deleteObjectCache(key);
                logger.info("First level cache update.Object in second level cache deleted.");
            }
            if(maxSizeMemory == secondLevelCache.sizeCache()){
                logger.error("SecondLevelCache is full");
                secondLevelCache.clearCache();
                logger.error("SecondLevelCache clear");
            }
        }
    }

    /**
     * Determining by key the level of the cache and adding an object to it.
     * @param key key
     * @param value object
     */
    @Override
    public synchronized void putInCache(Key key, Value value) {
        logger.info("Start entry in cache");
        if(!secondLevelCache.containsEnterKey(key)){
            firstLevelCache.putInCache(key,value);
            logger.info(" Entry in first level cache key: " + key + " and value: " + value);
        }
        if(!firstLevelCache.containsEnterKey(key)) {
            secondLevelCache.putInCache(key, value);
            logger.info(" Entry in second level cache key: " + key + " and value: " + value);
            return;
        }
    }
    /**
     * Clear two-level cache
     */
    @Override
    public void clearCache() {
        logger.info("Start clearing cache.First level cache are cleared");
        firstLevelCache.clearCache();
        logger.info("Second level cache are cleared");
        secondLevelCache.clearCache();
    }

    /**
     * Determining the cache level. Search for an object by key and return its value.
     * The number of requests to the object is taken into account.
     * When the number of requests is exceeded, the cache level changes.
     * @param key key
     * @return object
     */
    @Override
    public synchronized  Value getObjectCache(Key key) {
        if(firstLevelCache.containsEnterKey(key)){
           numRequest++;
           if(numRequest >= levelChange ){
               updateFirstLevelCache();
               numRequest=0;
           }
            logger.info("The first level cache returns an object.");
            return firstLevelCache.getObjectCache(key);
        }

        if(secondLevelCache.containsEnterKey(key)){
            numRequest++;
            if(numRequest > levelChange){
                updateSecondLevelCache();
                numRequest=0;
            }
            logger.info("The first level cache returns an object.");
            return  secondLevelCache.getObjectCache(key);
        }
        return null;
    }
    /**
     * Removing an object from the two-level cache by key
     * @param key key
     */
    @Override
    public synchronized void deleteObjectCache(Key key) {
        if(firstLevelCache.containsEnterKey(key)){
            firstLevelCache.deleteObjectCache(key);
            logger.info("The second level cache deletes an object.");
        }
        if(secondLevelCache.containsEnterKey(key)){
            secondLevelCache.deleteObjectCache(key);
            logger.info("The first level cache deletes an object.");
        }

    }

    /**
     *Returns the number of the object request from the cache
     * @param key key
     * @return number of request the object
     */
    @Override
    public long getObjectCallNumber(Key key) {
        if(firstLevelCache.containsEnterKey(key)){
            logger.info("The first level cache returns call number  of the object.");
           return firstLevelCache.getObjectCallNumber(key);
        }
        if(secondLevelCache.containsEnterKey(key)){
            logger.info("The first level cache returns call number  of the object.");
            return  secondLevelCache.getObjectCallNumber(key);
        }
        return 0;
    }

    /**
     * Return of used keys
     * @return set of keys
     */
    @Override
    public Set<Key> getUsedKeys() {
        Set<Key> result = new HashSet<>(firstLevelCache.getUsedKeys());
        logger.info("The first level cache returns used keys.");
        return result;
    }

    /**
     * Checking the key for entry in the list of keys
     * @param key key
     * @return true or false
     */
    @Override
    public boolean containsEnterKey(Key key) {
      return firstLevelCache.containsEnterKey(key);
    }

    /**
     * Return the size of the two-level cache
     * @return size first level cache
     */
    @Override
    public int sizeCache() {
        logger.info("Return size cache.");
        return firstLevelCache.sizeCache();
    }
}
