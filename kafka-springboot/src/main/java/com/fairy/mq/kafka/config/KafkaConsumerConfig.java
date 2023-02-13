package com.fairy.mq.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 鹿少年
 * @version 1.0
 * @date 2022/5/29 17:37
 */
@Configuration
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaConsumerConfig {

    @Autowired
    private KafkaConsumerProperties consumerProperties;

    public Map<String, Object> consumerConfigs() {
        Map props = new HashMap<>();
        //消费者组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getGroupId());
        //设置是否自动维护offset 默认为true
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        //自动提交的频率 单位 ms 默认值5000
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getBootstrapServers());
        //session超时，超过这个时间consumer没有发送心跳,就会触发rebalance操作 默认45000
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 120000);
        //请求超时 默认值30000
//        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);

        //当kafka中没有初始offset或offset超出范围时将自动重置offset
        //earliest:重置为分区中最小的offset
        //latest:重置为分区中最新的offset(消费分区中新产生的数据)
        //none:只要有一个分区不存在已提交的offset,就抛出异常
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        //批量消费时间间隔  默认值 300000
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumerProperties.getMaxPollInterval());
        //批量消费最大数量 默认值500
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerProperties.getMaxPollRecords());

        //Key 反序列化类
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //Value 反序列化类
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //设置Consumer拦截器
//        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, MyConsumerInterceptor.class.getName());

        return props;
    }


    @Bean("consumerFactory")
    public ConsumerFactory consumerFactory() {
        DefaultKafkaConsumerFactory<String, String> factory = new DefaultKafkaConsumerFactory(consumerConfigs());
        return factory;
    }


    /**
     * 手动提交  MANUAL_IMMEDIATE 手动消费一批数据后开始提交 逐步提交
     *
     * @return
     */
    @Bean("manualIMListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer> manualIMListenerContainerFactor(ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        //设置并发量，小于或等于Topic的分区数 设置消费者组中的线程数量
        factory.setConcurrency(consumerProperties.getConcurrency());
        //必须 设置为批量监听
        factory.setBatchListener(true);
        //消费者监听器自启 设置自动启动 项目启动后 消费组监听器就开始监听消费数据
        factory.setAutoStartup(true);
        //消费一次提交一次  MANUAL 表示批量提交一次
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    /**
     * 批量消费后 一次提交偏移量
     *
     * @return
     */
    @Bean("manualListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> manualListenerContainerFactory(/*ConcurrentKafkaListenerContainerFactoryConfigurer configurer,*/ ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        //设置并发量，小于或等于Topic的分区数 设置消费者组中的线程数量
        factory.setConcurrency(consumerProperties.getConcurrency());
        factory.getContainerProperties().setPollTimeout(2000);

        //必须 设置为批量监听
        factory.setBatchListener(true);
        factory.setAutoStartup(true);

        //消费一次提交一次  MANUAL 表示批量提交一次
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
//        configurer.configure(factory,consumerFactory);
        return factory;
    }

    /**
     * record 模式 消费一条数据就提交 不可设置ack 不可设置批量消费
     *
     * @param consumerFactory
     * @return
     */
/*
    @Bean("recordListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer> recordListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ConsumerFactory consumerFactory) {

        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setAutoStartup(true);
        factory.getContainerProperties().setPollTimeout(1500);
        //配置手动提交offset
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
*/

    /**
     * TIME     当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后，距离上次提交时间大于TIME时提交
     *
     * @param consumerFactory
     * @return
     */
/*    @Bean("timeListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer> timeListenerContainerFactory(ConsumerFactory consumerFactory) {

        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setAutoStartup(true);
        //批量消费
        factory.setBatchListener(true);

        factory.getContainerProperties().setPollTimeout(30000);
        factory.getContainerProperties().setAckTime(30000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.TIME);
        return factory;
    }*/

    /**
     * COUNT    当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后，被处理record数量大于等于COUNT时提交
     *
     * @param consumerFactory
     * @return
     */
    @Bean("countListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> countListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setAutoStartup(true);
        factory.setBatchListener(true);

        factory.getContainerProperties().setPollTimeout(1500);
        factory.getContainerProperties().setAckCount(5);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.COUNT);
        return factory;
    }


    /**
     * 每一批消息在消费后的 TIME时间后提交offset或者消费到count记录后提交偏移量
     *
     * @param consumerFactory
     * @return
     */
/*    @Bean("timeCountListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> timeCountListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setPollTimeout(2000);
        factory.getContainerProperties().setAckCount(5);
        factory.setAutoStartup(true);
        factory.setBatchListener(true);

        factory.getContainerProperties().setAckTime(10000);


        //配置手动提交offset
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.COUNT_TIME);
        return factory;
    }*/

    /**
     * 自定义topic
     */
 /*   @Bean("fairy-topic")
    public NewTopic topic() {
        return new NewTopic("fairy-topic", 4, (short) 3);
    }*/

}
