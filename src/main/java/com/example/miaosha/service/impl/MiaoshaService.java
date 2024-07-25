package com.example.miaosha.service.impl;

import com.example.miaosha.model.vo.GoodsEntity;
import com.example.miaosha.service.IMQService;
import com.example.miaosha.service.IMiaoshaCacheService;
import com.example.miaosha.service.IMiaoshaService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Data
@Component
public class MiaoshaService implements IMiaoshaService {

    @Resource
    private IMiaoshaCacheService miaoshaCacheService;
    @Resource
    private IMQService mqService;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 步骤一：准备西红柿
        CompletableFuture<String> f1 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("洗西红柿");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("切它");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "切好的西红柿";
                });

// 步骤二：准备鸡蛋
        CompletableFuture<String> f2 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("洗鸡蛋");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("煎鸡蛋");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "煎好的鸡蛋";
                });
// 步骤三：炒鸡蛋
        CompletableFuture<String> f3 =
                f1.thenCombine(f2, (a, tf) -> {
                    System.out.println("番茄炒蛋中");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "西红柿炒鸡蛋";
                });
        System.out.println("做其他菜中");
        Thread.sleep(3000);
        // 等待任务 3 执行结果
        System.out.println(f3.get());
    }

    /**
     *  滑块锁
     * @param goodsID
     * @return
     */
    @Override
    public GoodsEntity doMiaosha(long goodsID) {
        // 1.尝试减少缓存
        // 注意这里的 <= -1，由于以下代码块可以并发运行，decrStockCount返回的值可能小于-1
        int surplus;
        if ((surplus = miaoshaCacheService.decrStockCount(goodsID)) <= -1) {
            log.error("库存扣减失败");
            return null;
        }
//        // 2.加锁 tps 600~1000
//        try {
//            if (miaoshaCacheService.setLock(goodsID, surplus)) {
//                // 3.从缓存获取商品信息
//                GoodsEntity goodsInfo = miaoshaCacheService.getGoodsInfo(goodsID);
//                // 4.发送mq异步更新数据库
//                mqService.sendUpdateMessage(goodsID);
//                goodsInfo.setSurplusNum(surplus);
//                return goodsInfo;
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            log.error("出错");
//            return null;
//        }
        // 3.从缓存获取商品信息
        GoodsEntity goodsInfo = miaoshaCacheService.getGoodsInfo(goodsID);
        // 4.发送mq异步更新数据库
        mqService.sendUpdateMessage(goodsID);
        goodsInfo.setSurplusNum(surplus);
        return goodsInfo;
    }

    /**
     * 普通锁
     * @param goodsID
     * @return
     */
    @Override
    public GoodsEntity doMiaoshaNormal(long goodsID) {
        // 1.加锁
        try {
//            if (miaoshaCacheService.tryLock(goodsID)) {
            if (miaoshaCacheService.setLock(goodsID)) {
                // 2.尝试减少缓存
                // 由于有锁，以下代码块不同线程串行运行，decrStockCount返回的值不可能小于-1
                if (miaoshaCacheService.decrStockCount(goodsID) == -1) {
                    log.error("库存扣减失败");
                    return null;
                }
                // 3.解锁
                miaoshaCacheService.releaseLock(goodsID);
                // 4.从缓存获取商品信息
                GoodsEntity goodsInfo = miaoshaCacheService.getGoodsInfo(goodsID);
                // 5.发送mq异步更新数据库
                mqService.sendUpdateMessage(goodsID);
                return goodsInfo;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("出错");
            return null;
        }
    }
}
