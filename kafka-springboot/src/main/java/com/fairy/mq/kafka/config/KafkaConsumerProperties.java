package com.fairy.mq.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 鹿少年
 * @version 1.0
 * @date 2022/5/29 17:38
 */
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {

    private String topic;
    private Integer concurrency;
    private String bootstrapServers;
    private boolean autoCommit;
    private String groupId;
    private Integer maxPollInterval;
    private Integer maxPollRecords;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getMaxPollInterval() {
        return maxPollInterval;
    }

    public void setMaxPollInterval(Integer maxPollInterval) {
        this.maxPollInterval = maxPollInterval;
    }

    public Integer getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(Integer maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }
}
