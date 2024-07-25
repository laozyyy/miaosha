package com.example.miaosha.service.impl;

import com.example.miaosha.model.po.Goods;
import com.example.miaosha.model.vo.GoodsEntity;
import com.example.miaosha.repository.IGoodsDao;
import com.example.miaosha.service.IMiaoshaCacheService;
import com.example.miaosha.service.IRedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MiaoshaCacheService implements IMiaoshaCacheService {

    @Resource
    private IRedisService redisService;
    @Resource
    private IGoodsDao goodsDao;

    @Override
    public boolean setLock(long goodsID, int surplus) {
        String key = String.format("goods_lock_%d_%d", goodsID, surplus);
        return redisService.setNx(key, 8000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock(long goodsID) {
        long startTime = System.currentTimeMillis();
        while (!setLock(goodsID)) {
            // 检查超时
            if (System.currentTimeMillis() - startTime > 5000) {
                return false; // 超时返回false
            }
            try {
                long waitTime = 10;
                Thread.sleep(waitTime); // 等待一段时间再尝试获取锁
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean setLock(long goodsID) {
        String key = String.format("goods_lock_%d", goodsID);
        return redisService.setNx(key, 8000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean releaseLock(long goodsID) {
        String key = String.format("goods_lock_%d", goodsID);
        return redisService.delKey(key);
    }

    @Override
    public int decrStockCount(long goodID) {
        try {
            String key = String.format("goods_stock_count_%d", goodID);
            int surplus = redisService.decr(key);
            if (surplus <= -1) {
                redisService.setValue(key, 0);
            }
            return surplus;
        } catch (Exception e) {
            log.error("未装配goodsID：{}", goodID);
            return -1;
        }
    }

    @Override
    public GoodsEntity getGoodsInfo(long goodID) {
        String key = String.format("goods_info_%d", goodID);
        GoodsEntity goodsEntity = redisService.getValue(key);
        if (goodsEntity == null) {
            Goods goods = goodsDao.queryOneByID(goodID);
            goodsEntity = GoodsEntity.builder().
                    goodsID(goods.getGoodsId()).
                    goodsName(goods.getGoodsName()).
                    goodsDesc(goods.getGoodsDesc()).
                    build();
            redisService.setValue(key, goodsEntity);
        }
        return goodsEntity;
    }

    @Override
    public void assembleStockCount(long goodID) {
        String key = String.format("goods_stock_count_%d", goodID);
        int surplus = goodsDao.queryOneByID(goodID).getSurplus();
        log.info("余量:{}", surplus);
        redisService.setValue(key, surplus);
    }


}
