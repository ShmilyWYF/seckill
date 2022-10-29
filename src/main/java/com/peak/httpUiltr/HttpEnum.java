package com.peak.httpUiltr;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public enum HttpEnum {

        /**
         * 请求处理正常
         */
        OK_200(200, "请求成功","success"),

        /**
         * 请求处理正常
         */
        CREATED_201(201, "创建成功","success"),

        /**
         * 请求处理异常，请稍后再试
         */
        ERROR_400(400, "非法请求","warning"),

        /**
         * 请求账户请求异常，请稍后再试
         */
        ERROR_401(401, "登录异常","warning"),


        /**
         * 创建失败
         */
        ERROR_402(401, "创建失败","warning"),

        /**
         * 访问内容不存在
         */
        NotFound_400(404, "访问内容不存在","warning"),

        /**
         * 系统内部错误
         */
        ERROR_500(500, "系统内部错误","error"),


        /**
         * 更新失败
         */
        ERROR_502(502, "更新失败","warning"),


        /**
         * 更新失败
         */
        ERROR_503(503, "请求失败","warning"),

        /**
         * 参数校验失败
         */
        ERROR_600(600, "参数校验失败","warning"),

        /**
         * 参数校验失败
         */
        ERROR_6001(6001, "用户不存在","warning"),


        /**
         * 库存不足
         */
        ERROR_6002(6002, "库存不足","warning"),


        /**
         * 参数校验失败
         */
        ERROR_6003(6003, "下单失败","warning");



        private int code;
        private String desc;
        private String type;

        public String desc() {
            return desc;
        }

        public int code() {
            return code;
        }

        public String type(){
                return type;
        }


}

