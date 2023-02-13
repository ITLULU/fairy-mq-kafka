package com.fairy.mq.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 鹿少年
 * @date 2022/7/13 14:23
 */
@Configuration
@ConfigurationProperties(prefix = "kafka.mq")
public class TopicConfigurations {
    private List<ConsumerTopic> topics;

    public List<ConsumerTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<ConsumerTopic> topics) {
        this.topics = topics;
    }

    public static class ConsumerTopic {
        String name;
        Integer numPartitions;
        Short replicationFactor;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getNumPartitions() {
            return numPartitions;
        }

        public void setNumPartitions(Integer numPartitions) {
            this.numPartitions = numPartitions;
        }

        public Short getReplicationFactor() {
            return replicationFactor;
        }

        public void setReplicationFactor(Short replicationFactor) {
            this.replicationFactor = replicationFactor;
        }
    }
}
