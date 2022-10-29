package com.peak.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.TOrder;
import com.peak.pojo.TUser;
import com.peak.vo.GoodsVo;
import com.peak.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2022-10-19
 */
public interface TOrderService extends IService<TOrder> {

    TOrder seckill(TUser user, GoodsVo goodsVo);

    OrderDetailVo detail(Long orderId);

    String createPath(TUser user, Long goodsId);

    boolean checkPath(TUser user, Long goodsId, String path);

}
