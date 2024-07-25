package com.example.miaosha.service;

import com.example.miaosha.model.vo.GoodsEntity;

public interface IMiaoshaCacheService {
    boolean setLock(long goodID, int surplus);

    boolean tryLock(long goodID);

    boolean setLock(long goodID);

    int decrStockCount(long goodID);

    GoodsEntity getGoodsInfo(long goodID);

    void assembleStockCount(long goodID);

    boolean releaseLock(long goodsID);
}
