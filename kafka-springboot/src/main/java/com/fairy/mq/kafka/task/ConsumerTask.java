package com.fairy.mq.kafka.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author hll
 * @version 1.0
 * @date 2022/7/1 15:45
 */
//@Component
public class ConsumerTask {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerTask.class);
    @Autowired
    private KafkaListenerEndpointRegistry registry;


    /**
     * 如果不设置自动启动监听可以通过 定时任务启动
     */
    @Scheduled(cron = "2/10 * * * * ?")
    public void startListener() {
        logger.info("开启监听");
        MessageListenerContainer container = registry.getListenerContainer("group-listener");
     /*   if (!container.isRunning()) {
            container.start();
        }*/
        //恢复
          container.resume();
    }

    //    @Scheduled(cron = "0 08 12 * * ?")
    public void shutdownListener() {
        logger.info("关闭监听");
        //暂停
        MessageListenerContainer container = registry.getListenerContainer("group-listener");
        container.stop();
    }
}
