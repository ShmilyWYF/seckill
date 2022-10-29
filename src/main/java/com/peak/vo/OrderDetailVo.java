package com.peak.vo;

import com.peak.pojo.TOrder;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetailVo {

    private TOrder order;
    private GoodsVo goodsVo;

}
