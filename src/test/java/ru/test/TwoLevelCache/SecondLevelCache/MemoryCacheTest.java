package ru.test.TwoLevelCache.SecondLevelCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.test.TwoLevelCache.TestObject;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MemoryCache.class)
public class MemoryCacheTest {
    private  long FIRST_REQUEST = 1;
    private Integer key;
    private Integer maxValue;
    private Random random;
    private String[] names;
    private TestObject testObject;
    private  MemoryCache<Integer,TestObject> memoryCache;
    @Before
    public void init(){
      maxValue = 10;
      memoryCache = new MemoryCache<>(maxValue);
      random = new Random();
      names = new String[]{"T-800","John Conor","Barbara","Han Solo"};
        for (int i = 0; i < maxValue; i++) {
            for (int j = 0; j < names.length; j++) {
                key = random.nextInt(maxValue);
                testObject = new TestObject(key,names[j],"Nice,shoot!");
                memoryCache.putInCache(key,testObject);
            }
        }
    }

    @Test
    public  void getObjectTest(){
        Integer randomKey = random.nextInt(maxValue);
        TestObject testObject = memoryCache.getObjectCache(randomKey);
        if(randomKey.equals(key)){
            assertTrue(testObject.equals(memoryCache.getObjectCache(key)));
        }
        assertFalse(testObject.equals(memoryCache.getObjectCache(key)));
    }

    @Test
    public  void deleteObjectTest(){
        Integer randomKey = random.nextInt(maxValue);
        TestObject testObject = memoryCache.getObjectCache(randomKey);
        if(randomKey.equals(key)){
            memoryCache.deleteObjectCache(randomKey);
            assertFalse(testObject.equals(memoryCache.getObjectCache(randomKey)));
        }
        assertTrue(testObject.equals(memoryCache.getObjectCache(randomKey)));
    }

    @Test
    public  void  clearCacheTest(){
        Integer sizeCache = memoryCache.sizeCache();
        if(memoryCache.sizeCache()!=0) {
            memoryCache.clearCache();
        }
        assertTrue(sizeCache.equals(memoryCache.sizeCache()));
    }

    @Test
    public  void containsKey(){
        Integer keyRandom = random.nextInt(maxValue);
        assertEquals(memoryCache.containsEnterKey(keyRandom),true);
    }

}
