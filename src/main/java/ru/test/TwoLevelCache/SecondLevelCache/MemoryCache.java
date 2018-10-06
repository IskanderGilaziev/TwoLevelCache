package ru.test.TwoLevelCache.SecondLevelCache;

import org.apache.log4j.Logger;
import ru.test.TwoLevelCache.CacheInterface.Cache;
import ru.test.TwoLevelCache.CacheInterface.ObjectCache;

import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class that implements the second level cache
 * @param <Key> key
 * @param <Value> object
 */
public class MemoryCache<Key extends Serializable, Value extends Serializable>
        implements Cache<Key,Value>,ObjectCache<Key,Value> {

    /**
     * @param maxSizeCache Maximum second level cache size
     */
    private static final Logger logger = Logger.getLogger(MemoryCache.class);
    private ConcurrentHashMap<Key,String> secondLevelMap;
    private ConcurrentHashMap<Key,Long> numberOfRequestObjectInMemory;
    private  int maxSizeMemory;
    private  long FIRST_REQUEST = 1;

    public MemoryCache(int maxSizeMemory ) {
        logger.info("Initialization memory.");
        this.maxSizeMemory = maxSizeMemory;
        secondLevelMap = new ConcurrentHashMap<>(maxSizeMemory);
        numberOfRequestObjectInMemory = new ConcurrentHashMap<>();
        File memoryTempFile = new File("D://memoryTempFile//");
        if(memoryTempFile.exists()){
            memoryTempFile.mkdirs();
        }
    }

    /**
     * Method counts average number of requests to the object
     * @return sum result
     */

    public long getMiddleRequestSecondLevelCache(){
        long middle = 0;
        Set<Key> resultKeysTemp = new HashSet<>(numberOfRequestObjectInMemory.keySet());
        for(Key key : resultKeysTemp){
            middle += getObjectCallNumber(key);
        }
        long result = middle/2;
        if(result==0){
            return result = secondLevelMap.size()/2;
        }
        return  result;
    }

    /**
     * Write object to file system. Object serialization.
     * @param key key
     * @param value object
     */
    @Override
    public void putInCache( Key key, Value value) {
        Random random = new Random();
        String filePathToObject = "D://memoryTempFile//"+random.nextInt(maxSizeMemory)+".txt";
        secondLevelMap.put(key,filePathToObject);
        logger.info("Key writing in memory, filepath object: "+ filePathToObject);
        numberOfRequestObjectInMemory.put(key,FIRST_REQUEST);
        logger.info("The first request and  key: "+ key+ " writing.");
        try(ObjectOutputStream objectOutput =
                    new ObjectOutputStream(new FileOutputStream(secondLevelMap.get(key)))){
            logger.info("Start serialization.");
            objectOutput.writeObject(value);
            objectOutput.flush();
            logger.info("End serialization.");
            logger.info("Object writing in memory.");
        } catch (FileNotFoundException e) {
            logger.error("File not found: "+ e.getMessage());
        } catch (IOException e) {
            logger.error("Can't write a file: " + e.getMessage());
        }
        logger.info("Key an Value are put in memory");
    }

    /**
     * Clear first level cache
     */
    @Override
    public void clearCache() {
        logger.info("Started cache clearing.");
        for(Key keys: secondLevelMap.keySet()){
            File deleteFiles = new File(secondLevelMap.get(keys));
            deleteFiles.delete();
            logger.info("MemoryCache clear.");
        }
    }

    /**
     * Search for an object in the file system. Object deserialization.
     * @param key key
     * @return deserialization object
     */

    @Override
    public Value getObjectCache(Key key) {
            logger.info("Started find object in memory");
            String filePathToObject = secondLevelMap.get(key);
            logger.info("Start deserialization.");
            try (ObjectInputStream objectInput =
                         new ObjectInputStream(new FileInputStream(filePathToObject))) {
                Object readObject = objectInput.readObject();
                logger.info("End deserialization.");
                long tempResult = numberOfRequestObjectInMemory.get(key) + 1;
                numberOfRequestObjectInMemory.put(key, tempResult);
                logger.info("Put in memory next request: " + tempResult + " the object: " + secondLevelMap.get(key));
                logger.info("Reading object from memory.");
                return (Value) readObject;

            } catch (FileNotFoundException e) {
                logger.error("File not found: " + e.getMessage());
            } catch (IOException e) {
                logger.error("Can't write a file: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                logger.error("Class not found: " + e.getMessage());
            }
        return null;
    }

    /**
     * Removing an object from the second level cache by key
     * @param key key
     */
    @Override
    public void deleteObjectCache(Key key) {
        logger.info("Start delete object.");
            File removeFile = new File(secondLevelMap.remove(key));
            removeFile.delete();
            numberOfRequestObjectInMemory.remove(key);
            logger.info("Object deleted.");
    }

    /**
     * Returns the number of the object request from the cache
     * @param key key
     * @return number of request the object
     */
    @Override
    public long getObjectCallNumber(Key key) {
        logger.info("Returns number of request the object");
        return numberOfRequestObjectInMemory.get(key);
    }

    /**
     * Return of used keys
     * @return set of keys
     */
    @Override
    public Set<Key> getUsedKeys() {
        return numberOfRequestObjectInMemory.keySet();
    }

    /**
     * Checking the key for entry in the list of keys
     * @param key key
     * @return true or false
     */
    @Override
    public boolean containsEnterKey(Key key) {
        logger.info("Return contains key(memory).");
        return secondLevelMap.containsKey(key);
    }

    /**
     * Return the size of the second level cache
     * @return size first level cache
     */
    @Override
    public int sizeCache() {
        logger.info("Return memory size.");
       return secondLevelMap.size();
    }
}
