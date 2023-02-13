package com.fairy.mq.kafka.config.partition;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * @author 鹿少年
 * @version 1.0
 * @date 2022/5/29 19:21
 */
public class MyPartitioner implements Partitioner {
    /**
     * 分区策略核心方法
     * @param topic
     * @param key
     * @param keyBytes
     * @param value
     * @param valueBytes
     * @param cluster
     * @return 分区
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        //具体分区逻辑，这里全部发送到0号分区

        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
