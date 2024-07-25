package com.example.miaosha.service.impl;

import com.example.miaosha.model.vo.GoodsEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class MiaoshaCacheServiceTest {
    @Resource
    MiaoshaCacheService miaoshaCacheService;

    @Test
    void setLock() {
        boolean b = miaoshaCacheService.setLock(123L, 10);
        log.error("{}", b);
    }

    @Test
    void assembleStockCount() {
        miaoshaCacheService.assembleStockCount(123l);
    }

    @Test
    void decrStockCount() {
        int b = miaoshaCacheService.decrStockCount(123l);
        log.error("{}", b);
    }

    @Test
    void getGoodsInfo() {
        GoodsEntity b = miaoshaCacheService.getGoodsInfo(123l);
        log.error("{}", b);
    }

}