package com.peak.service;

import com.github.yulichang.base.MPJBaseService;
import com.peak.pojo.TGoods;
import com.peak.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2022-10-19
 */
public interface TGoodsService extends MPJBaseService<TGoods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long id);

}
