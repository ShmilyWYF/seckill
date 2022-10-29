package com.peak.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peak.mapper.TSeckillOrderMapper;
import com.peak.pojo.TSeckillOrder;
import com.peak.pojo.TUser;
import com.peak.service.TSeckillOrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyf
 * @since 2022-10-19
 */
@Service
public class TSeckillOrderServiceImpl extends ServiceImpl<TSeckillOrderMapper, TSeckillOrder> implements TSeckillOrderService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public Long getResult(TUser user, Long goodsId) {
        TSeckillOrder seckillOrder =(TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            return seckillOrder.getOrderId();
        }else if(redisTemplate.hasKey("isStockEmpty:"+user.getId())){
            return -1L;
        }else {
            return 0L;
        }
    }
}
