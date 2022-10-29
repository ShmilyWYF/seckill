package com.peak.vo;

import com.peak.pojo.TGoods;
import com.peak.pojo.TUser;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetailVo implements Serializable {
    private TUser user;
    private TGoods goods;
    private int secKillStatus;
    private int remainSeconds;
}
