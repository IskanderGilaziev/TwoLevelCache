package ru.test.TwoLevelCache.CacheInterface;

public interface Cache<Key,Value> {
    void putInCache(Key key, Value value);
    void clearCache();
    boolean containsEnterKey(Key key);
    int sizeCache();
}
