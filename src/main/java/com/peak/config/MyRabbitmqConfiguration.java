package com.peak.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MyRabbitmqConfiguration {

    private static final String SECKILL = "seckill_exchange";

    private static final String QUEQU = "seckill_queue";

    private static final String QUEQUKEY = "#.seckill.#";

    private static final String REGISTERER = "registered_exchange";

    private static final String REGISTERER_QUEQU = "registered_queue";


    /**
     * topic交换机
     */
    @Bean
    public TopicExchange seckilltopicExchange() {
        return new TopicExchange(SECKILL);
    }

    @Bean
    public Queue seckilltopicQueue(){
        return new Queue(QUEQU);
    }

    @Bean
    public Binding seckillBinding(){
        return BindingBuilder.bind(seckilltopicQueue()).to(seckilltopicExchange()).with(QUEQUKEY);
    }

    @Bean
    public DirectExchange registeredExchange(){
        return new DirectExchange(REGISTERER);
    }

    @Bean
    public Queue  registereQueue(){
        return new Queue(REGISTERER_QUEQU);
    }

    @Bean
    public Binding   registereBinding(){
        return BindingBuilder.bind(registereQueue()).to(registeredExchange()).with("registered_queue");
    }


}

