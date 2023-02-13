package com.fairy.mq.kafka.listenner;

import com.fairy.base.common.exception.ResultException;
import com.fairy.mq.kafka.handler.RecordHandler;
import com.fairy.mq.kafka.model.po.ConsumerRecordPO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 鹿少年
 * @version 1.0
 * @date 2022/5/29 15:50
 */
@Component
public class KafkaListner {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListner.class);
    @Autowired
    private RecordHandler recordHandler;

    @Autowired
    private ListenerUtil listenerUtil;

    @Value("${consumer.listener.order}")
    private String orderListener;
    @Value("${max.retry.count}")
    private Integer retryCount;

    /**
     * volatile 可以标识多线程对该变量是共享可见的
     */
    private AtomicInteger count = new AtomicInteger(0);

    /**
     * 配置多个消费组
     * TopicPartition  定义分区 partitionOffsets
     * concurrency就是同组下的消费者个数，就是并发消费数，必须小于等于分区总数
     *
     * @param records
     * @param ack
     */
    @KafkaListener(id = "${consumer.listener.order}", groupId = "${kafka.consumer.group-id}", containerFactory = "manualIMListenerContainerFactory", topicPartitions = {@TopicPartition(topic = "${kafka.mq.topics[0].name}", partitions = {"0", "1"})})
    public void fairyGroupTopic(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        try {
            logger.info("消费监听本次拉取数据量：{}", records.size());
            //如果服务出现问题 这个时候应该暂停消费  不要做一些无谓的性能耗损 暂停消费  可以通过配置的形式  启动一个定时任务 拉取配置中心
            //人工干预解决问题后  通过修改配置  然后触发开始消费
            for (ConsumerRecord<String, String> record : records) {
                String value = record.value();
                logger.info("消费者组fairyGroupTopic消费 topic 分区0,1数据：{},topic:{},partition:{},offset:{}", value, record.topic(), record.partition(), record.offset());
            }
            doFilter(records);
            //手动提交ack 移动偏移量
            ack.acknowledge();
        } catch (Throwable e) {
            logger.error("consumer Listener监听消费消息异常:{}", e);
            if (count.getAndIncrement() >= retryCount) {
                listenerUtil.pauseConsumer(orderListener);
            }
            throw ResultException.create(e);
        }

    }

    /**
     * 对数据进行幂等校验 如果数据时没有消费的数据 可以将数据存储记录 以topic-partition-offset做为唯一索引来判断数据是否是已经消费的数据
     *
     * @param records
     */
    private void doFilter(List<ConsumerRecord<String, String>> records) {
        //1:查看消费记录数据库 校验数据是否是已经消费的数据
        for (ConsumerRecord<String, String> record : records) {
            ConsumerRecordPO recordPO = recordHandler.searchRecordFromDB(record);
            if (recordPO == null) {
                //新的数据 记录消费
                //同时将数据写入本地表  以后消费数据直接从本地表进行数据消费处理  对于处理时长比较长的数据 可以这样处理
                recordHandler.saveConsumerRecord(record);

            }

        }
        //模拟异常 没有走到手动ack提交偏移量
//        int i = 1 / 0;
    }

}
