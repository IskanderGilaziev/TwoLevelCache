package ru.test.TwoLevelCache.TwoLevelCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.test.TwoLevelCache.TestObject;

import java.util.Random;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TwoLevelCache.class)
public class TwoLevelCacheTest {
    private long numRequest;
    private int levelChange;
    private int maxSizeRam;
    private int maxSizeMemory;
    private TestObject testObject;
    private TwoLevelCache<Integer, TestObject> twoLevelCache;
    private Random random;
    private String[] names;
    private  Integer key;

    @Before
    public  void init(){
        maxSizeMemory = 10;
        maxSizeRam  = 10;
        levelChange = 5;
        twoLevelCache = new TwoLevelCache<>(maxSizeRam,maxSizeMemory,levelChange);
        random = new Random();
        names = new String[]{"T-800","John Conor","Barbara","Han Solo"};
        for (int i = 0; i < maxSizeRam; i++) {
            for (int j = 0; j < names.length; j++) {
                key = random.nextInt(maxSizeRam);
                testObject = new TestObject(key,names[j],"Nice, the last tests:)");
                twoLevelCache.putInCache(key,testObject);
            }
        }
    }

    @Test
    public  void getObjectTest(){
        Integer randomKey = random.nextInt(maxSizeRam);
        TestObject testObject = twoLevelCache.getObjectCache(randomKey);
        if(randomKey.equals(key)){
            assertTrue(testObject.equals(twoLevelCache.getObjectCache(key)));
        }
        assertFalse(testObject.equals(twoLevelCache.getObjectCache(key)));
    }

    @Test
    public  void deleteObjectTest(){
        Integer randomKey = random.nextInt(maxSizeRam);
        TestObject testObject = twoLevelCache.getObjectCache(randomKey);
        if(randomKey.equals(key)){
            twoLevelCache.deleteObjectCache(randomKey);
            assertFalse(testObject.equals(twoLevelCache.getObjectCache(randomKey)));
        }
        assertTrue(testObject.equals(twoLevelCache.getObjectCache(randomKey)));
    }

    @Test
    public  void  clearCacheTest(){
        Integer sizeCache = twoLevelCache.sizeCache();
        if(twoLevelCache.sizeCache()!=0) {
            twoLevelCache.clearCache();
        }
        assertFalse(sizeCache.equals(twoLevelCache.sizeCache()));
    }

}
