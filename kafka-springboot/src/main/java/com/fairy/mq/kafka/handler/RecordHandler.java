package com.fairy.mq.kafka.handler;

import com.fairy.base.common.exception.ResultException;
import com.fairy.mq.kafka.model.po.ConsumerRecordPO;
import com.fairy.mq.kafka.service.ConsumerRecordService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author hll
 * @version 1.0
 * @date 2022/7/1 13:58
 */
@Component
public class RecordHandler {

    @Autowired
    private ConsumerRecordService consumerRecordService;

    /**
     * 检查数据库是否存在该条已经消费的记录  对应这些日志记录 可以设置定时任务每隔几天或者几小时取定时清除一下
     *
     * @param record
     */
    public ConsumerRecordPO searchRecordFromDB(ConsumerRecord<String, String> record) {
        //1:查看数据库 查看是否存在对应的消费记录
        ConsumerRecordPO recordPO = consumerRecordService.getConsumerRecord(record.topic(),record.partition(),record.offset());
        return recordPO;
    }

    /**
     * 记录消费信息 插入数据表
     * 设置隔离级别为读已提交
     * @param record
     */
    @Transactional(readOnly = false,rollbackFor = ResultException.class,propagation = Propagation.REQUIRED,isolation= Isolation.READ_COMMITTED)
    public void saveConsumerRecord(ConsumerRecord<String, String> record) {
        ConsumerRecordPO po = new ConsumerRecordPO(record.topic(),record.partition(),record.offset(),new Date(),new Date(record.timestamp()));
        consumerRecordService.save(po);



    }
}
