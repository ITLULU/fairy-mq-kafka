package com.fairy.mq.kafka.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fairy.mq.kafka.model.po.ConsumerRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author hll
 * @version 1.0
 * @date 2022/7/6 15:29
 */
@Mapper
public interface ConsumerRecordMapper extends BaseMapper<ConsumerRecordPO> {

    /**
     * 查询消费记录
     * @param topic 主题
     * @param partition 分区
     * @param offset offset
     * @return  ConsumerRecordPO
     */
    @Select("select * from op_consumer_record where topic =#{topic} and topic_partition=#{partition} and topic_offset= #{offset}")
    ConsumerRecordPO getConsumerRecord(String topic, int partition, long offset);
}
