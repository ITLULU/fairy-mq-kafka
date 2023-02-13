package com.fairy.mq.kafka.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 初始处理定义分区 topic
 * @author 鹿少年
 * @date 2022/7/13 14:22
 */
@Configuration
@EnableConfigurationProperties(TopicConfigurations.class)
public class TopicAdministrator {
    @Autowired
    private TopicConfigurations topicConfigurations;

    @Autowired
    private AdminClient adminClient;

    /**
     * 项目启动时进行初始化
     **/
    @PostConstruct
    public void init() {
        initializeBeans(topicConfigurations.getTopics());
    }

    private void initializeBeans(List<TopicConfigurations.ConsumerTopic> topics) {
        if(topics==null){
            return;
        }
        List<NewTopic> topicList = topics.stream().map(topic -> {
            return new NewTopic(topic.name, topic.numPartitions, topic.replicationFactor);
        }).collect(Collectors.toList());
        adminClient.createTopics(topicList);


    }

}
