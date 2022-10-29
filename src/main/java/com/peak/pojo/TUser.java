package com.peak.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyf
 * @since 2022-10-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TUser对象", description="")
public class TUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID,手机号码",required = true,example = "17683755044")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "用户昵称",required = true,example = "wyf")
    private String nickname;

    @ApiModelProperty(value = "MD5(MD5(pass明文+固定salt)+salt)",required = true,example = "c8686f44561ee2ceb96553e87bfa6032")
    private String password;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String slat;

    @ApiModelProperty(value = "头像",required = true,example = "没有头像")
    private String head;

    @JsonIgnore
    private Date registerDate;

    @JsonIgnore
    private Date lastLoginDate;

    @ApiModelProperty(value = "登录次数",hidden = true)
    @JsonIgnore
    private Integer loginCount;


}
