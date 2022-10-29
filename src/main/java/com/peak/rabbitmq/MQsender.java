package com.peak.rabbitmq;

import com.peak.dto.SysUserDTO;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;



@Service
@Slf4j
public class MQsender {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void seckill_queue(String message){
        rabbitTemplate.convertAndSend("seckill_exchange","seckill.abc",message);
    }

    public String registered_queue(SysUserDTO userDTO) {
        return (String) rabbitTemplate.convertSendAndReceive("registered_exchange", "registered_queue", userDTO);
    }
}
