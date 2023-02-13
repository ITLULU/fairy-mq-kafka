package com.fairy.mq.kafka.listenner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author hll
 * @version 1.0
 * @date 2022/7/6 15:09
 */
@Component
public class ListenerUtil {

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    public void  pauseConsumer(String listenerId){
        MessageListenerContainer listenerContainer=registry.getListenerContainer(listenerId);
        listenerContainer.pause();
    }

    public void resumeConsumer(String listenerId){
        MessageListenerContainer listenerContainer=registry.getListenerContainer(listenerId);
        listenerContainer.resume();
    }

}
