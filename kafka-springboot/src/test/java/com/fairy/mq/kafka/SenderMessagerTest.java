package com.fairy.mq.kafka;

/**
 * @author hll
 * @version 1.0
 * @date 2022/7/6 16:25
 */

import com.alibaba.fastjson.JSON;
import com.fairy.mq.kafka.model.dto.OrderDto;
import com.fairy.mq.kafka.service.KafkaSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KafkaApp.class)
public class SenderMessagerTest {

    @Autowired
    private KafkaSender kafkaSender;

    @Test
    public void sendTrans() {
        OrderDto dto = new OrderDto();
        dto.setOrderAmount(111.0);
        dto.setOrderId(1121);
        dto.setProductId(1212);
        kafkaSender.doTransactionSend3("my-replicated-topic", 0, "1212", JSON.toJSONString(dto));
    }
}
