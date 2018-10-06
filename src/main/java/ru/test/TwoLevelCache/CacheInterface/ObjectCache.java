package ru.test.TwoLevelCache.CacheInterface;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public interface ObjectCache<Key,Value> {
    Value getObjectCache(Key key);
    void deleteObjectCache(Key key);
    long getObjectCallNumber(Key key);
    Set<Key> getUsedKeys();
}
