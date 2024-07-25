package com.example.miaosha.model.response;

import com.example.miaosha.model.vo.GoodsEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MiaoshaResponse {
    private String status;
    private String message;
    private GoodsEntity goodsEntity;
}
