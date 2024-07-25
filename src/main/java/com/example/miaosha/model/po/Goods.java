package com.example.miaosha.model.po;

import lombok.Data;

@Data
public class Goods {
    private int id;

    private long goodsId;

    private String goodsName;

    private String goodsDesc;

    private int count;

    private int surplus;
}
