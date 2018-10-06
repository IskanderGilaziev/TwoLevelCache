package ru.test.TwoLevelCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TestObjectRandomBuilder {

    private Random random = new Random();
    private Map<Integer, TestObject> testObject = new ConcurrentHashMap<>();
    private int maxKey;
    private  Integer key;

    public TestObjectRandomBuilder(int maxKey, int maxNumberTestObject) {
        this.maxKey = maxKey;

        for (int i = 0; i < maxNumberTestObject; i++) {
            key = createKey();
            TestObject MyObj = createNewRandomObject(key) ;
            testObject.put(key, MyObj);
        }
    }

    /*
        output object by a given key
     */
    public TestObject getTestObject(Integer key){
        for (Integer keys: testObject.keySet()){
            if(keys.equals(key)){
                return testObject.get(key);
            }
        }
        return null;
    }

    /*
         key generation for requests in RequestThreads
     */
    public  Integer getKeyForRequest(){
        Integer temp = createKey();
        for(Integer intKey: testObject.keySet()) {
            if (intKey.equals(temp)) {
                return temp;
            }
        }
            return this.getKeyForRequest();
    }
    /*
        generation of random keys for subsequent calls
     */
    public  Integer getRandomKeys(){
        Integer key = random.nextInt(maxKey);
        return key;
    }

    /*
         create new random test-object
     */
    private TestObject createNewRandomObject(Integer key){
        String name = createRandomName();
        String message = createRandomMessage();
        return new TestObject( key ,name,message);
    }

    /*
         creating a key for the subsequent object generation
     */
    private  Integer createKey(){
        Integer key = random.nextInt(maxKey);
        if(key==0){
            key=maxKey;
        }
        return key;
    }

    /*
        create random name test-object
     */
    private   String createRandomName(){
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.
                append(random.nextGaussian()+random.nextGaussian()+random.nextInt(maxKey)).
                toString();
    }
    /*
        create random message test-object
     */
    private   String createRandomMessage(){
        StringBuilder stringBuilder = new StringBuilder();
        return  stringBuilder.
                append(random.nextGaussian()+random.nextInt(maxKey)).
                toString();
    }
}
