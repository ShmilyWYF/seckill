package com.peak.controller;


import com.peak.httpUiltr.ResponseResult;
import com.peak.service.TOrderService;
import com.peak.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wyf
 * @since 2022-10-19
 */
@RestController
@RequestMapping("/order")
public class TOrderController {

    @Autowired
    private TOrderService orderService;

    /**
     * 可能需要校验用户  这个用户的订单详情
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    public ResponseResult<OrderDetailVo> detail(Long orderId){
       OrderDetailVo orderDetailVo = orderService.detail(orderId);
       return ResponseResult.ok(orderDetailVo);
    }
}
