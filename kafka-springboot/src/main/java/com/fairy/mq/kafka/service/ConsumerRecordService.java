package com.fairy.mq.kafka.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fairy.mq.kafka.model.po.ConsumerRecordPO;

/**
 * @author hll
 * @version 1.0
 * @date 2022/7/6 15:28
 */
public interface ConsumerRecordService extends IService<ConsumerRecordPO> {
    /**
     * 查询是否是已经消费的数据  即已经存储在消费记录表里面
     * 需要用事务  保证 消费记录 和数据存储两个保证原子操作
     * @param topic 主题
     * @param partition 分区
     * @param offset 偏移量
     * @return 记录
     */
    ConsumerRecordPO getConsumerRecord(String topic, int partition, long offset);
}
