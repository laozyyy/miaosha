package com.example.miaosha.service;

import com.example.miaosha.model.vo.GoodsEntity;

public interface IMiaoshaService {
    GoodsEntity doMiaosha(long goodID);

    GoodsEntity doMiaoshaNormal(long goodsID);
}
