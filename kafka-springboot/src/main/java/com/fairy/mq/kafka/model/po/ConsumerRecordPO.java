package com.fairy.mq.kafka.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 消费数据记录
 *
 * @author hll
 * @version 1.0
 * @date 2022/7/1 14:01
 */
@TableName("op_consumer_record")
public class ConsumerRecordPO implements Serializable {
    /**
     * 主题
     */
    private String topic;
    /**
     * 分区
     */
    private Integer topicPartition;
    /**
     * 偏移量
     */
    private Long topicOffset;
    /**
     * 消费时间
     */
    @JsonFormat(locale = "zh", timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date consumerDate;
    @JsonFormat(locale = "zh", timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendDate;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getTopicPartition() {
        return topicPartition;
    }

    public void setTopicPartition(Integer topicPartition) {
        this.topicPartition = topicPartition;
    }

    public Long getTopicOffset() {
        return topicOffset;
    }

    public void setTopicOffset(Long topicOffset) {
        this.topicOffset = topicOffset;
    }

    public Date getConsumerDate() {
        return consumerDate;
    }

    public void setConsumerDate(Date consumerDate) {
        this.consumerDate = consumerDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public ConsumerRecordPO() {
        //Empty construct
    }

    public ConsumerRecordPO(String topic, Integer topicPartition, Long topicOffset, Date consumerDate, Date sendDate) {
        this.topic = topic;
        this.topicPartition = topicPartition;
        this.topicOffset = topicOffset;
        this.consumerDate = consumerDate;
        this.sendDate = sendDate;
    }
}
