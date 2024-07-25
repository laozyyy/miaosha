package com.example.miaosha.repository;

import com.example.miaosha.model.po.Goods;
import com.example.miaosha.model.vo.GoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Mapper
public interface IGoodsDao {
    Goods queryOneByID(long goodsID);
}
