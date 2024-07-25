package com.example.miaosha.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lenovo
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoodsEntity {

    private long goodsID;

    private String goodsName;

    private String goodsDesc;

    private int surplusNum;

}
