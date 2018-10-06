package ru.test.TwoLevelCache.CacheInterface;

public interface LevelCache<Key,Value>{
    void updateFirstLevelCache();
    void updateSecondLevelCache();
}
