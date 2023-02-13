package com.fairy.mq.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 鹿少年
 * @version 1.0
 * @date 2022/5/29 18:10
 */
@EnableKafka
@Configuration
public class KafkaProductConfig {

    @Value("${kafka.product.retries}")
    private Integer retries;

    @Value("${kafka.product.acks}")
    private String acks;

    @Value("${kafka.product.bootstrap-server}")
    private String bootstrapServers;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(props);
    }


    private Map productConfig() {
        Map props = new HashMap();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //应答级别
        //acks=0 把消息发送到kafka就认为发送成功
        //acks=1 把消息发送到kafka leader分区，并且写入磁盘就认为发送成功
        //acks=all -1 把消息发送到kafka leader分区，并且leader分区的副本follower对消息进行了同步就任务发送成功
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        //重试次数 默认值Integer.MAX_VALUE
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        //重试间隔设置 默认100
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100L);
        //设置发送消息的本地缓冲区，如果设置了该缓冲区，消息会先发送到本地缓冲区，可以提高消息发送性能 32 * 1024 * 1024L
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        //批量处理的最大大小 单位 byte 默认值 16384
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        //每条消息最大的大小 默认值 1024 * 1024,1M
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1048576);
        //KafkaProducer.send() 和 partitionsFor() 方法的最长阻塞时间 单位 ms 默认值 60 * 1000,
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 60000);
        //客户端ID 默认值空
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "fairy-kafka");
        /*
           发送延时, 当生产端积累的消息达到batch-size或接收到消息linger.ms后,生产者就会将消息提交给kafka
          默认值是0，意思就是消息必须立即被发送，但这样会影响性能一般设置10毫秒左右，就是说这个消息发送完后会进入本地的一个batch，如果10毫秒内，这个batch满了16kb就会随batch一起被发送出去
        如果10毫秒内，batch没满，那么也必须把消息发送出去，不能让消息的发送延迟时间太长
        */
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        //把发送的key从字符串序列化为字节数组
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //把发送消息value从字符串序列化为字节数组
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //生产者幂等性 默认true
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        //事务的一个超时时间 60000
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 60000);
        //消息压缩：none、lz4、gzip、snappy，默认为 none。
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        //自定义分区器
//        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, MyPartitioner.class.getName());
        return props;
    }

    @Bean("producerFactory")
    public ProducerFactory<String, String> producerFactory() {
        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory(productConfig());
        return producerFactory;
    }

    @Bean("kafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory(productConfig());
        KafkaTemplate kafkaTemplate = new KafkaTemplate<>(producerFactory);
        return kafkaTemplate;
    }

    @Bean("transKafkaTemplate")
    public KafkaTemplate<String, String> transKafkaTemplate(@Qualifier("transProducerFactory")ProducerFactory producerFactory ) {
        KafkaTemplate kafkaTemplate = new KafkaTemplate<>(producerFactory);
        return kafkaTemplate;
    }

    /**
     * 事务生产者工厂
     * @return ProducerFactory
     */
    @Bean("transProducerFactory")
    public ProducerFactory<String, String> transProducerFactory() {
        Map props = productConfig();
        //事务配置
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"tx_");
        //事务超时时间 默认 60000ms
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG,60000);

        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory(props);
        //设置事务Id前缀 开启事务
//        producerFactory.setTransactionIdPrefix(String.format("tx-%s-", UUID.randomUUID().toString()));
        return producerFactory;
    }
    /**
     * 以该方式配置事务管理器：就不能以普通方式发送消息，只能通过 kafkaTemplate.executeInTransaction
     * 或在方法上加 @Transactional 注解来发送消息，否则报错
     *
     * @return
     */
    @Bean("kafkaTransactionManager")
    public KafkaTransactionManager kafkaTransactionManager(@Qualifier("transProducerFactory")ProducerFactory producerFactory ) {
        KafkaTransactionManager<String, String> kafkaTransactionManager = new KafkaTransactionManager<>(producerFactory);
        return  kafkaTransactionManager;
    }
}
