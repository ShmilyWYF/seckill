package com.peak.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peak.pojo.TSeckillOrder;
import com.peak.pojo.TUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2022-10-19
 */
public interface TSeckillOrderService extends IService<TSeckillOrder> {

    /***
     * 获取秒杀结果接口
     * @param user
     * @param goodsId
     * @return
     */
    Long getResult(TUser user, Long goodsId);
}
