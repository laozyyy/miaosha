package com.example.miaosha.controller;

import com.example.miaosha.model.request.MiaoshaRequest;
import com.example.miaosha.model.response.MiaoshaResponse;
import com.example.miaosha.model.vo.GoodsEntity;
import com.example.miaosha.service.IMiaoshaCacheService;
import com.example.miaosha.service.IMiaoshaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h2>秒杀服务</h2><br/>
 * 1. 高可用性：在短时大量并发请求下仍能以较高效率工作<br/>
 * 2. 最终一致性：避免出现非极端情况下的不一致问题（超卖，少卖）<br/>
 * 3. 高性能：高效处理并发请求，防止请求时间过长<br/>
 */
@Slf4j
@RestController
public class MiaoshaController {
    @Resource
    private IMiaoshaService miaoshaService;
    @Resource
    private IMiaoshaCacheService miaoshaCacheService;
    @Resource
    private IMiaoshaCacheService miaoshaCacheService2;

    /**
     * 线程数200，循环100次，TPS为3000~3600左右<br/>库存充足情况下无秒杀失败<br/>库存不充足情况下在单机房集群无并发问题
     * @param req
     * @return
     */
    @PostMapping("/miaosha")
    public MiaoshaResponse Miaosha(@RequestBody MiaoshaRequest req) {
        try {
            GoodsEntity goodsEntity = miaoshaService.doMiaosha(req.getGoodsID());
            if (goodsEntity != null) {
                log.info("秒杀，成功 goodId:{}", req.getGoodsID());
                return MiaoshaResponse.builder().goodsEntity(goodsEntity).message("秒杀成功").status("200").build();
            }
            return MiaoshaResponse.builder().goodsEntity(null).message("秒杀失败").status("200").build();
        } catch (Exception e) {
            log.error("秒杀，失败 goodId:{}", req.getGoodsID(), e);
            return MiaoshaResponse.builder().goodsEntity(null).message("秒杀失败-服务内部错误").status("500").build();
        }
    }

    /**
     * 线程数200，循环100次，TPS为120~160左右<br/>库存充足情况下秒杀失败率5%左右<br/>库存不充足情况下在单机房集群无并发问题
     * @param req
     * @return
     */
    @PostMapping("/miaosha/normal")
    public MiaoshaResponse MiaoshaNormal(@RequestBody MiaoshaRequest req) {
        try {
            GoodsEntity goodsEntity = miaoshaService.doMiaoshaNormal(req.getGoodsID());
            if (goodsEntity != null) {
                log.info("秒杀normal，成功 goodId:{}", req.getGoodsID());
                return MiaoshaResponse.builder().goodsEntity(goodsEntity).message("秒杀成功").status("200").build();
            }
            return MiaoshaResponse.builder().goodsEntity(null).message("秒杀失败").status("200").build();
        } catch (Exception e) {
            log.error("秒杀，失败 goodId:{}", req.getGoodsID(), e);
            return MiaoshaResponse.builder().goodsEntity(null).message("秒杀失败-服务内部错误").status("500").build();
        }
    }

    /**
     * 库存装配到缓存
     * @param req
     * @return
     */
    @PostMapping("/assemble")
    public String Assemble(@RequestBody MiaoshaRequest req) {
        miaoshaCacheService.assembleStockCount(req.getGoodsID());
        return "ok";
    }

}
