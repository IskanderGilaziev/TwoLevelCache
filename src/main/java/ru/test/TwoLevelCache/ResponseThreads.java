package ru.test.TwoLevelCache;

import ru.test.TwoLevelCache.TwoLevelCache.TwoLevelCache;

public class ResponseThreads extends  Thread {
    private TestObjectRandomBuilder testObjectRandomBuilder;
    private  int maxThread;
    private  int maxRequest;
    private final TwoLevelCache<Integer, TestObject> cache;

    public ResponseThreads(TestObjectRandomBuilder testObjectRandomBuilder,
                           TwoLevelCache twoLevelCache, int maxThread, int maxRequest ) {
        this.testObjectRandomBuilder = testObjectRandomBuilder;
        this.maxThread=maxThread;
        this.maxRequest = maxRequest;
        this.cache = twoLevelCache;
    }

    @Override
    public void run() {
        for (int i = 0; i < maxRequest; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Integer key = testObjectRandomBuilder.getRandomKeys();
            synchronized (cache) {
               cache.getObjectCache(key);
            }
        }
    }
}
