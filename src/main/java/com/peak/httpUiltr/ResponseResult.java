package com.peak.httpUiltr;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.io.Serializable;

/**
 * <b><code>ResponseResult</code></b>
 * <p>
 * Description响应信息主体
 * </p>
 * <b>Creation Time:</b> 2022/10/18 17:55.
 *
 * @author wyf
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    private String type;

    private T data;

    private T token;

    public ResponseResult(int code, String msg, String type) {
        this.code = code;
        this.msg = msg;
        this.type = type;
    }

    public ResponseResult(HttpEnum httpEnum) {
        this.code = httpEnum.code();
        this.msg = httpEnum.desc();
        this.type = httpEnum.type();
    }

    /**
     * 默认成功
     * 不带任何参
     * data=null
     * 默认200枚举
     */
    public static <T> ResponseResult<T> ok() {
        return restResult(null, HttpEnum.OK_200.code(), HttpEnum.OK_200.desc(),HttpEnum.OK_200.type());
    }
    /**
     * data参，枚举类型参，自定义数据，默认200枚举
     */
    public static <T> ResponseResult<T> ok(T data,HttpEnum Enum) {
        return restResult(data, Enum.code(), Enum.desc(),Enum.type());
    }

    /**
     * data参，枚举类型参，自定义数据，默认200枚举
     */
    public static <T> ResponseResult<T> ok(T data) {
        return restResult(data, HttpEnum.OK_200.code(), HttpEnum.OK_200.desc(),HttpEnum.OK_200.type());
    }

    /**
     *  data=null，枚举类型参，msg参，自定义返回消息
     */
    public static <T> ResponseResult<T> ok(HttpEnum Enum,String msg) {
        return restResult( Enum.code(),msg,Enum.type());
    }

    /**
     * data参，枚举类型参，消息参，自定义data-消息
     */
    public static <T> ResponseResult<T> ok(T data,HttpEnum Enum, String msg) {
        return restResult(data, Enum.code(),msg,Enum.type());
    }
    /**
     * data参，枚举类型参，消息参，token参，自定义data-消息-token;
     */
    public static <T> ResponseResult<T> ok(T data,HttpEnum Enum, String msg,T token) {
        return restResult(data, Enum.code(), msg,Enum.type(),token);
    }
    /**
     * data参，枚举类型参，消息参，token参，自定义data-token;
     */
    public static <T> ResponseResult<T> ok(T data,HttpEnum Enum,T token) {
        return restResult(data, Enum.code(),Enum.desc(),Enum.type(),token);
    }
    /**
     * data=null，枚举类型参，消息参，token参，自定义msg-token;
     */
    public static <T> ResponseResult<T> ok(HttpEnum Enum, String msg,T token) {
        return restResult(Enum.code(), msg,Enum.type(),token);
    }
    /**
     * data=null，枚举类型参，token参，自定义token;
     */
    public static <T> ResponseResult<T> ok(HttpEnum Enum,T token) {
        return restResult(Enum.code(), Enum.desc(),Enum.type(),token);
    }

    /**
     * 默认失败
     * 不带任何参
     * data=null
     * 默认500枚举
     */
    public static <T> ResponseResult<T> failed() {
        return restResult(null, HttpEnum.ERROR_500.code(),HttpEnum.ERROR_500.desc(),HttpEnum.ERROR_500.type());
    }
    public static <T> ResponseResult<T> failed(HttpEnum Enum) {
        return restResult(null,Enum.code(),Enum.desc(),Enum.type());
    }
    /**
     * data参，枚举类型参，自定义data;
     */
    public static <T> ResponseResult<T> failed(T data,HttpEnum Enum) {
        return restResult(data,Enum.code(),Enum.desc(),Enum.type());
    }
    /**
     * data=null，消息参，枚举类型参，自定义msg;
     */
    public static <T> ResponseResult<T> failed(HttpEnum Enum,String msg) {
        return restResult(Enum.code(),Enum.desc()+msg,Enum.type());
    }
    /**
     * data参，消息参 自定义data-msg
     */
    public static <T> ResponseResult<T> failed(T data,HttpEnum Enum, String msg) {
        return restResult(data, Enum.code(), msg, Enum.type());
    }
    /**
     * data=null，状态参，消息参，枚举类型参，自定义返回
     */
    public static <T> ResponseResult<T> failed(int code, String msg,String type) {
        return restResult(code, msg, type);
    }


    private static <T> ResponseResult<T> restResult(T data, int code, String msg,String type) {
        ResponseResult<T> apiResult = new ResponseResult<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setType(type);
        return apiResult;
    }


    private static <T> ResponseResult<T> restResult(int code, String msg,String type) {
        ResponseResult<T> apiResult = new ResponseResult<>(code,msg,type);
        return apiResult;
    }


    private static <T> ResponseResult<T> restResult(T data, int code, String msg,String type,T token) {
        ResponseResult<T> apiResult = new ResponseResult<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setType(type);
        apiResult.setToken(token);
        return apiResult;
    }
    private static <T> ResponseResult<T> restResult(int code, String msg,String type,T token) {
        ResponseResult<T> apiResult = new ResponseResult<>();
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        apiResult.setType(type);
        apiResult.setToken(token);
        return apiResult;
    }

}