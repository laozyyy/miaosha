package com.example.miaosha.repository;

import com.example.miaosha.model.po.Goods;
import com.example.miaosha.model.vo.GoodsEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class IGoodsDaoTest {
    @Resource
    private IGoodsDao iGoodsDao;

    @Test
    public void TestGoodsDao() {
        Goods goods = iGoodsDao.queryOneByID(123);
        log.info("result: {}", goods);
    }
}