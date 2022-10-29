package com.peak.service.impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.MPJWrappers;
import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.pojo.TGoods;
import com.peak.mapper.TGoodsMapper;
import com.peak.pojo.TSeckillGoods;
import com.peak.service.TGoodsService;
import com.peak.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 * @author wyf
 * @since 2022-10-19
 * <h3>不用join插件继承ServiceImpl</h3>
 * <p>public class TGoodsServiceImpl extends ServiceImpl<TGoodsMapper, TGoods> implements TGoodsService {}</p>
 */
@Service
public class TGoodsServiceImpl extends MPJBaseServiceImpl<TGoodsMapper, TGoods> implements TGoodsService {

    @Autowired
    private TGoodsMapper goodsMapper;

    /**
     * <h2>传统方式</h2>
     * <p>return goodsMapper.findGoodsVo(); 查询结果的类</p>
     * <h2>↓join插件方法解释</h2>
     * <pre>
     * selectAll()：查询指定实体类的全部字段
     * select()：查询指定的字段，支持可变长参数同时查询多个字段，但是在同一个select中只能查询相同表的字段，所以如果查询多张表的字段需要分开写
     * selectAs()：字段别名查询，用于数据库字段与接收结果的dto中属性名称不一致时转换
     * leftJoin()：左连接，其中第一个参数是参与联表的表对应的实体类，第二个参数是这张表联表的ON字段，第三个参数是参与联表的ON的另一个实体类属性
     * </pre>
     * <p> 示范
     * <pre>
     * new MPJLambdaWrapper<Order>()
     *       .selectAll(Order.class)
     *       .select(Product::getUnitPrice)
     *       .selectAs(User::getName,OrderDto::getUserName)
     *       .selectAs(Product::getName,OrderDto::getProductName)
     *       .leftJoin(User.class, User::getId, Order::getUserId)
     *       .leftJoin(Product.class, Product::getId, Order::getProductId)
     *       .eq(Order::getStatus,3));
     * </pre>
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.selectJoinList(GoodsVo.class, MPJWrappers
                .lambdaJoin()
                .selectAll(TGoods.class)
                .select(TSeckillGoods::getSeckillPrice, TSeckillGoods::getStockCount)
                .leftJoin(TSeckillGoods.class, TSeckillGoods::getGoodsId, TGoods::getId)
        );
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long id) {
        if(id==null){
            throw new GlobalException(HttpEnum.NotFound_400);
        }
        return goodsMapper.selectJoinOne(GoodsVo.class,MPJWrappers.lambdaJoin()
                .selectAll(TGoods.class)
                .select(TSeckillGoods::getSeckillPrice,TSeckillGoods::getStockCount,TSeckillGoods::getStartDate,TSeckillGoods::getEndDate)
                .leftJoin(TSeckillGoods.class, TSeckillGoods::getGoodsId,TGoods::getId)
                .eq(TGoods::getId,id)
        );
    }

}


