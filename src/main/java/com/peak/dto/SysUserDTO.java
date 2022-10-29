package com.peak.dto;

import com.peak.pojo.SysUser;;
import com.peak.validator.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUserDTO implements Serializable {

    @NotNull
    private String userNick;

    @NotNull
    private String userAvatar;

    @NotNull
    @Length(min = 6, max = 15)
    @IsMobile
    private String username;

    @NotNull
    @Length(min = 8, max = 15)
    private String password;
}
