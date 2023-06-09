package com.delicious.pojo.entity;

import com.alibaba.fastjson.annotation.JSONType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: ES-furniture
 * @description: 购物车表
 * @author: 王炸！！
 * @create: 2023-03-16 12:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONType(orders={"cartId","userId","cartFurnitureId","cartCount"})
@TableName("t_cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Integer cartId;

    private Integer userId;
    private Integer cartFurnitureId;
    private Integer cartCount;
}
