package com.peak.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peak.accesslimit.AccessLimit;
import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.SysUserinfo;
import com.peak.pojo.TOrder;
import com.peak.pojo.TSeckillOrder;
import com.peak.pojo.TUser;
import com.peak.rabbitmq.MQsender;
import com.peak.service.TGoodsService;
import com.peak.service.TOrderService;
import com.peak.service.TSeckillOrderService;
import com.peak.vo.GoodsVo;
import com.peak.vo.SeckillMessage;
import io.netty.util.internal.ThreadExecutorMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private TGoodsService goodsService;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    @Resource
    private TSeckillOrderService seckillOrderService;

    @Resource
    private MQsender mQsender;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource   //引入redisLock集成
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private TOrderService orderService;

    /**
     * 根据秒杀商品ID查询商品库存
     * 根据用户id查询是否重复抢购
     *
     * @param user    用户实体类
     * @param goodsId 商品id
     * @return
     */
//    @AccessLimit(second = 5,maxCount = 5)
//    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
//    public ResponseResult<String> doSeckill(@PathVariable("path") String path, TUser user, Long goodsId) {
//
//        if (user == null) {
//            throw new GlobalException(HttpEnum.ERROR_400, "用户不能为空");
//        }
//
//        //检查请求地址是否有效
//        boolean check = orderService.checkPath(user, goodsId, path);
//        if (!check) {
//            throw new GlobalException(HttpEnum.ERROR_400);
//        }
//        //判断是否重复抢购
//        TSeckillOrder seckillorder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
//        if (seckillorder != null) {
//            throw new GlobalException(HttpEnum.ERROR_503, "每个商品只能购买一件");
//        }
//        //内存标记，减少redis的访问
//        if (EmptyStockMap.get(goodsId)) {
//            throw new GlobalException(HttpEnum.ERROR_400, "当前火热请稍后再试");
//        }
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        // redislock预减库存 原始使用   缓存查找是否有锁------->
//        try {
//            Boolean redislock = redisTemplate.opsForValue().setIfAbsent("seckillredislock:" + user.getId() + ":" + goodsId, "value", 30, TimeUnit.SECONDS);
//            if (redislock) {  //上锁占坑
//                //预减库存
//                Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//                if (stock < 0) {
//                    //当stock-1时 设置内存标识
//                    EmptyStockMap.put(goodsId, true);
//                    valueOperations.increment("seckillGoods:" + goodsId);
//                    throw new GlobalException(HttpEnum.ERROR_503, "抢购失败，库存不足");
//                }
//                //处理完，释放锁
//                redisTemplate.delete("seckillredislock:" + user.getId() + ":" + goodsId);
//            } else {
//                //锁被占用 等待30ms
//                Thread.sleep(30);
//                //重新加入列队
//                doSeckill(path, user, goodsId);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        /**redislock锁插件使用 ----->获取锁
//        Lock lock = redisLockRegistry.obtain("seckillredislock" + user.getId() + ":" + goodsId);
//        try {
//            lock.lock();    //无锁则继续运行 其他线程等待锁释放
//            Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//            System.out.println("stock----------------->" + stock);
//            if (stock < 0) {
//                System.out.println("my for 0");
//                //当stock-1时 设置内存标识
//                EmptyStockMap.put(goodsId, true);
//                valueOperations.increment("seckillGoods:" + goodsId);
//                return new ResponseResult<>(HttpEnum.ERROR_6002);
//            }
//            SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
//            mQsender.seckill_queue(JSON.toJSONString(seckillMessage));
//            return ResponseResult.ok(HttpEnum.OK_200, "排队中");
//        } finally {
//            lock.unlock();
//        }*/
//        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
//        mQsender.seckill_queue(JSON.toJSONString(seckillMessage));
//        return ResponseResult.ok(HttpEnum.OK_200, "排队中");
//
//    }

    @Resource
    private HttpServletRequest request;
    //前端控制台模拟调用
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    public ResponseResult<String> doSeckill(@PathVariable("path") String path, @CookieValue(defaultValue = "null") String token) {

        String Id = request.getParameter("goodsId");
        Long goodsId = Long.valueOf(Id);

        if(Objects.equals(token, "null")){
            return ResponseResult.failed(HttpEnum.ERROR_600,"token不存在,或者已过期");
        }
        SysUserinfo sysUserinfo = (SysUserinfo) redisTemplate.opsForValue().get("sysUser:" + "token" + ":" + token);

        if (sysUserinfo == null) {
            throw new GlobalException(HttpEnum.ERROR_400, "用户不能为空");
        }

        //判断是否重复抢购
        TSeckillOrder seckillorder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + sysUserinfo.getUserId() + ":" + goodsId);
        if (seckillorder != null) {
            throw new GlobalException(HttpEnum.ERROR_503, "每个商品只能购买一件");
        }
        //内存标记，减少redis的访问
        if (EmptyStockMap.get(goodsId)) {
            return ResponseResult.failed(HttpEnum.ERROR_400, "当前火热请稍后再试");
        }
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // redislock预减库存 原始使用   缓存查找是否有锁------->
        try {
            Boolean redislock = redisTemplate.opsForValue().setIfAbsent("seckillredislock:" + sysUserinfo.getUserId() + ":" + goodsId, "value", 30, TimeUnit.SECONDS);
            if (redislock) {  //上锁占坑
                //预减库存
                Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
                if (stock < 0) {
                    //当stock-1时 设置内存标识
                    EmptyStockMap.put(goodsId, true);
                    valueOperations.increment("seckillGoods:" + goodsId);
                    throw new GlobalException(HttpEnum.ERROR_503, "抢购失败，库存不足");
                }
                //处理完，释放锁
                redisTemplate.delete("seckillredislock:" + sysUserinfo.getUserId() + ":" + goodsId);
            } else {
                //锁被占用 等待30ms
                Thread.sleep(30);
                //重新加入列队
                doSeckill(path, token);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /**redislock锁插件使用 ----->获取锁
        Lock lock = redisLockRegistry.obtain("seckillredislock" + user.getId() + ":" + goodsId);
        try {
            lock.lock();    //无锁则继续运行 其他线程等待锁释放
            Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
            System.out.println("stock----------------->" + stock);
            if (stock < 0) {
                System.out.println("my for 0");
                //当stock-1时 设置内存标识
                EmptyStockMap.put(goodsId, true);
                valueOperations.increment("seckillGoods:" + goodsId);
                return new ResponseResult<>(HttpEnum.ERROR_6002);
            }
            SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
            mQsender.seckill_queue(JSON.toJSONString(seckillMessage));
            return ResponseResult.ok(HttpEnum.OK_200, "排队中");
        } finally {
            lock.unlock();
        }*/
        SeckillMessage seckillMessage = new SeckillMessage(sysUserinfo, goodsId);
        mQsender.seckill_queue(JSON.toJSONString(seckillMessage));
        return ResponseResult.ok(HttpEnum.OK_200, "排队中");

    }

    /**
     * 获取秒杀结果
     *
     * @param user
     * @param goodsId
     * @return orderId:成功  -1:秒杀失败   0:排队中
     */
    @AccessLimit(second = 5,maxCount = 5)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    public ResponseResult<Object> getResult(TUser user, Long goodsId) {
        if (user == null) {
            return ResponseResult.failed(HttpEnum.ERROR_6001);
        }
        Long OrderId = seckillOrderService.getResult(user, goodsId);
        return ResponseResult.ok(OrderId);
    }

    /***
     * <pre>
     *     前端业务调用
     *     -@AccessLimit(second = 5, maxCount = 5)
     *     -@RequestMapping(value = "/path", method = RequestMethod.GET)
     *     public ResponseResult<String> getPath(TUser user, Long goodsId) {
     *         String str = orderService.createPath(user, goodsId);
     *         return ResponseResult.ok(str, HttpEnum.OK_200);
     *     }
     * </pre>
     */
    //前端控制台业务模拟调用
    @RequestMapping(value = "/path/{id}", method = RequestMethod.GET)
    public ResponseResult<String> getPath(@CookieValue(defaultValue = "null") String token, @PathVariable("id") String goodsId) {
        if(Objects.equals(token, "null")){
            return ResponseResult.failed(HttpEnum.ERROR_600,"token不存在,或者已过期");
        }
        SysUserinfo sysUserinfo = (SysUserinfo) redisTemplate.opsForValue().get("sysUser:" + "token" + ":" + token);
        String str = orderService.createPath(sysUserinfo, Long.valueOf(goodsId));
        return ResponseResult.ok(str, HttpEnum.OK_200);
    }




    /**
     * 初始化操作
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();

        if (CollectionUtils.isEmpty(list)) {
            throw new GlobalException(HttpEnum.ERROR_500, "初始化获取秒杀商品表失败");
        }

        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });


    }


}
