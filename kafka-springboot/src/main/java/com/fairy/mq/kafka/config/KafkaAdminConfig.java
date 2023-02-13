package com.fairy.mq.kafka.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义消息消费异常处理器
 * @author 鹿少年
 * @version 1.0
 * @date 2022/5/29 19:17
 */
@Configuration
public class KafkaAdminConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaAdminConfig.class);

    @Value("${kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;
    /**
     * 创建一个kafka管理类，相当于rabbitMQ的管理类rabbitAdmin,没有此bean无法自定义的使用adminClient创建topic
     * @return
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        //配置Kafka实例的连接地址
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(props);
        return admin;
    }

    /**
     * kafka客户端，在spring中创建这个bean之后可以注入并且创建topic
     * @return
     */
    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(kafkaAdmin().getConfig());
    }

}
