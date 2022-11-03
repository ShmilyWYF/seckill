package com.peak.rabbitmq;

import com.alibaba.fastjson2.JSON;
import com.peak.dto.SysUserDTO;
import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.mapper.SysUserMapper;
import com.peak.mapper.SysUserinfoMapper;
import com.peak.pojo.*;
import com.peak.service.SysUserService;
import com.peak.service.SysUserinfoService;
import com.peak.service.TGoodsService;
import com.peak.service.TOrderService;
import com.peak.utils.MD5util;
import com.peak.utils.UUIDUtil;
import com.peak.vo.GoodsVo;
import com.peak.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Date;

@Service
@Slf4j
public class MQReceiver {

    @Resource
    private TGoodsService goodsService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private TOrderService orderService;

    @RabbitListener(queues = "seckill_queue")
    public void seckillReceiver(String message) {
        log.info("接收秒杀信息：" + message);
        SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);
        Long goodId = seckillMessage.getGoodId();
        SysUserinfo user = seckillMessage.getSysUserinfo();
        //根据商品id拿到商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodId);
        if (goodsVo == null) {
            throw new GlobalException(HttpEnum.ERROR_503, "商品不存在");
        }
        orderService.seckill(user, goodsVo);
    }

    @Resource
    private SysUserService sysUserService;

    @RabbitListener(queues = "registered_queue")
    public String registeredReceiver(SysUserDTO userDTO) {
        String uuid = UUIDUtil.getUUID();
        SysUser sysUser = new SysUser();
        sysUser.setUsername(userDTO.getUsername());
        sysUser.setPassword(MD5util.inputPassToDBPass(userDTO.getPassword(), uuid));
        sysUser.setSlat(uuid);
        SysUserinfo sysUserinfo = new SysUserinfo();
        sysUserinfo.setUserNick(userDTO.getUsername());
        sysUserinfo.setUserAvatar(userDTO.getUserAvatar());
        sysUserinfo.setRegisterDate(new Date());
        return sysUserService.adduser(sysUser, sysUserinfo);
    }

}
