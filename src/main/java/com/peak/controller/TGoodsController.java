package com.peak.controller;

import com.alibaba.fastjson.JSON;
import com.peak.accesslimit.AccessLimit;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.TGoods;
import com.peak.pojo.TUser;
import com.peak.service.TGoodsService;
import com.peak.vo.DetailVo;
import com.peak.vo.GoodsVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wyf
 * @since 2022-10-19
 */
@RestController
@RequestMapping("/goods")
public class TGoodsController <T> {

    @Autowired
    private TGoodsService goodsService;

    /**
     * 查询Goods表所有数据
     * @param model map返回类型
     * @return  所有商品信息
     */
    @RequestMapping(value = "/toList",method = RequestMethod.GET)
    public ResponseResult<List<GoodsVo>> toList(){
        return ResponseResult.ok(goodsService.findGoodsVo());
    }

    /**
     * 商品详情页-根据商品id获取商品信息
     * @param id
     * @return 商品的  秒杀状态，秒杀倒计时
     */
    @RequestMapping(value = "/toList/{id}",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult<List<DetailVo>> toListID(TUser user, @PathVariable @ApiParam(value = "商品ID",example = "1",required = true) T id){
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId((long) Integer.parseInt( (String) id));
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        //秒杀还未开始
        if(nowDate.before(startDate)){
            //目前时间在秒杀开始日期之前 来早了
            //倒计时
            remainSeconds = ((int)((startDate.getTime()-nowDate.getTime())/1000));
        }else if(nowDate.after(endDate)){
            //目前时间在秒杀结束日期之后 来晚了
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀中
            secKillStatus = 1;
            remainSeconds = 0;
        }

        List<DetailVo> list=new ArrayList<>();
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoods(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        list.add(detailVo);
        return ResponseResult.ok(list);
    }

}
