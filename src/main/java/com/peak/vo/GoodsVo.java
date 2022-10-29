package com.peak.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.peak.pojo.TGoods;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 合并类 Goods
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GoodsVo extends TGoods implements Serializable {

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

}
