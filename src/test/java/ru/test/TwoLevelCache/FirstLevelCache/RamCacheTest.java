package ru.test.TwoLevelCache.FirstLevelCache;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.test.TwoLevelCache.TestObject;
import static org.junit.Assert.*;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RamCache.class)
public class RamCacheTest {
    private TestObject testObject;
    private  RamCache<Integer,TestObject> ramCache;
    private  int FIRST_REQUEST = 1;
    private Integer key;
    private Integer maxValue;
    private Random random;
    private String[] names;



    @Before
    public void init(){
        maxValue=100;
        names = new String[]{"T-800","John Conor","Barbara","Han Solo"};
        random = new Random();
        ramCache = new RamCache<>(maxValue);
        for (int i = 0; i < maxValue; i++) {
            for (int j = 0; j <names.length ; j++) {
                key = random.nextInt(maxValue);
                testObject = new TestObject(key,names[j],"I`ll be back!");
                ramCache.putInCache(key,testObject);
            }
        }

    }

    @Test
    public  void getObjectTest(){
        Integer randomKey = random.nextInt(maxValue);
        TestObject newObject = ramCache.getObjectCache(randomKey);
        if(randomKey.equals(key)){
            assertTrue(newObject.equals(ramCache.getObjectCache(key)));
        }
        assertFalse(newObject.equals(ramCache.getObjectCache(key)));
    }

    @Test
    public  void deleteObjectTest(){
        Integer randomKey = random.nextInt(maxValue);
        TestObject testObject = ramCache.getObjectCache(randomKey);
        if(randomKey.equals(key)){
            ramCache.deleteObjectCache(randomKey);
            assertFalse(testObject.equals(ramCache.getObjectCache(randomKey)));
        }
        assertTrue(testObject.equals(ramCache.getObjectCache(randomKey)));
    }

    @Test
    public  void  clearCacheTest(){
        Integer sizeCahe = ramCache.sizeCache();
        ramCache.clearCache();
        assertFalse(sizeCahe.equals(ramCache.sizeCache()));
    }

    @Test
    public  void containsKey(){
        Integer keyRandom = random.nextInt(maxValue);
        assertEquals(ramCache.containsEnterKey(keyRandom),true);
    }
}
