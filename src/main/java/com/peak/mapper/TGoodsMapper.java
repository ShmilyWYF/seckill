package com.peak.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.peak.pojo.TGoods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 *  Mapper 接口
 * @author wyf
 * @since 2022-10-19
 */
/**传统方式
 * public interface TGoodsMapper extends BaseMapper<TGoods> {
 *
 *    List<GoodsVo> findGoodsVo();
 * }
 */
@Mapper
@Repository
public interface TGoodsMapper extends MPJBaseMapper<TGoods> {

}