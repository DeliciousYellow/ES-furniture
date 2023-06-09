package com.delicious.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.delicious.pojo.entity.Order;

import java.util.HashMap;

/**
 * @program: ES-furniture
 * @description:
 * @author: 王炸！！
 * @create: 2023-03-16 13:10
 **/
public interface OrderService extends IService<Order> {

    HashMap<String, Object> GetOrderAllPage(Integer page, Integer pageSize);

}
