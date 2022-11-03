package com.peak.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.*;
import com.peak.mapper.TOrderMapper;
import com.peak.service.TGoodsService;
import com.peak.service.TOrderService;
import com.peak.service.TSeckillGoodsService;
import com.peak.service.TSeckillOrderService;
import com.peak.utils.MD5util;
import com.peak.utils.UUIDUtil;
import com.peak.vo.GoodsVo;
import com.peak.vo.OrderDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wyf
 * @since 2022-10-19
 */
@Service
@Slf4j
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {

    @Autowired
    private TSeckillGoodsService seckillGoodsService;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private TSeckillOrderService seckillOrderService;
    @Autowired
    private TGoodsService goodsService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**  <pre> 前端业务调用
     *     -@Override
     *     public TOrder seckill(TUser user, GoodsVo goods) {
     *         ValueOperations valueOperations = redisTemplate.opsForValue();
     *
     *         //根据秒杀商品id查询秒杀商品
     *         TSeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<TSeckillGoods>().eq("goods_id", goods.getId()));
     *         if (seckillGoods.getStockCount() < 1) {
     *             //判断是否还有库存
     *             valueOperations.set("isStockEmpty:" + user.getId(), "0");
     *             throw new GlobalException(HttpEnum.ERROR_6002);
     *         }
     *         //将DB库存数量设置为redis库存预减数量(先减50次产生50个队列，设置数据库数量为0，50个队列生产50个订单)
     *         seckillGoodsService.update(new UpdateWrapper<TSeckillGoods>().setSql("stock_count = " + "stock_count-1").eq("goods_id", goods.getId()).gt("stock_count", 0));
     *
     *         TSeckillOrder isSeckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
     *         if (isSeckillOrder != null) {
     *             valueOperations.set("isStockEmpty:" + user.getId(), "0");
     *             throw new GlobalException(HttpEnum.ERROR_6003);
     *         }
     *         //生成订单
     *         TOrder order = new TOrder();
     *         order.setUserId(user.getId());
     *         order.setGoodsId(goods.getId());
     *         order.setDeliveryAddrId(0L);
     *         order.setGoodsName(goods.getGoodsName());
     *         order.setGoodsCount(1);
     *         order.setGoodsPrice(seckillGoods.getSeckillPrice());
     *         order.setOrderChannel(1);
     *         order.setStatus(0);
     *         order.setCreateDate(new Date());
     *         orderMapper.insert(order);
     *         //生成秒杀订单
     *         TSeckillOrder seckillOrder = new TSeckillOrder();
     *         seckillOrder.setUserId(user.getId());
     *         seckillOrder.setOrderId(order.getId());
     *         seckillOrder.setGoodsId(goods.getId());
     *         seckillOrderService.save(seckillOrder);
     *         redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);
     *         return order;
     *     }
     *     </pre>
     */
    //前端后台业务模拟调用
    @Override
    public TOrder seckill(SysUserinfo sysUserinfo, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //根据秒杀商品id查询秒杀商品
        TSeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<TSeckillGoods>().eq("goods_id", goods.getId()));
        if (seckillGoods.getStockCount() < 1) {
            //判断是否还有库存
            valueOperations.set("isStockEmpty:" + sysUserinfo.getUserId(), "0");
            throw new GlobalException(HttpEnum.ERROR_6002);
        }
        //将DB库存数量设置为redis库存预减数量(先减50次产生50个队列，设置数据库数量为0，50个队列生产50个订单)
        seckillGoodsService.update(new UpdateWrapper<TSeckillGoods>().setSql("stock_count = " + "stock_count-1").eq("goods_id", goods.getId()).gt("stock_count", 0));

        TSeckillOrder isSeckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + sysUserinfo.getUserId() + ":" + goods.getId());
        if (isSeckillOrder != null) {
            valueOperations.set("isStockEmpty:" + sysUserinfo.getUserId(), "0");
            throw new GlobalException(HttpEnum.ERROR_6003);
        }
        //生成订单
        TOrder order = new TOrder();
        order.setUserId(sysUserinfo.getUserId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        TSeckillOrder seckillOrder = new TSeckillOrder();
        seckillOrder.setUserId(sysUserinfo.getUserId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:" + sysUserinfo.getUserId() + ":" + goods.getId(), seckillOrder);
        return order;
    }


    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(HttpEnum.ERROR_400);
        }
        TOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new GlobalException(HttpEnum.NotFound_400);
        }
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setOrder(order);
        detailVo.setGoodsVo(goodsVo);
        return detailVo;
    }

    /**
     * <pre>
     * 真实业务前端调用
     * -@Override
     * public String createPath(TUser user, Long goodsId) {
     *     String str = MD5util.md5(UUIDUtil.getUUID() + "123456");
     *     redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
     * return str;
     *     }
     * </pre>
     */
    //前端控制台业务模拟调用
    @Override
    public String createPath(SysUserinfo user, Long goodsId) {
        String str = MD5util.md5(UUIDUtil.getUUID() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getUserId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }


    @Override
    public boolean checkPath(TUser user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }


}
