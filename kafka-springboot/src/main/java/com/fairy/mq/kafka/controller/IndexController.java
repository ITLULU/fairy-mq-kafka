package com.fairy.mq.kafka.controller;

import com.alibaba.fastjson.JSON;
import com.fairy.base.common.result.FairyResult;
import com.fairy.mq.kafka.model.dto.OrderDto;
import com.fairy.mq.kafka.service.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hll
 * @version 1.0
 * @date 2022/7/6 14:30
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private KafkaSender kafkaSender;
    @Value("${kafka.mq.topics[0].name}")
    private String TOPIC_NAME;

    @GetMapping("/send")
    public FairyResult sendMessage() {
        Map<String, Object> headers = new HashMap<>();

        headers.put(KafkaHeaders.TOPIC, TOPIC_NAME);
        headers.put(KafkaHeaders.PARTITION_ID, 1);
        headers.put(KafkaHeaders.MESSAGE_KEY, "key-1111");
        headers.put(KafkaHeaders.RECEIVED_MESSAGE_KEY, "recived-key-1111");
        OrderDto order =  new OrderDto(100,111,12,1000.0);
        GenericMessage message = new GenericMessage(JSON.toJSONString(order), new MessageHeaders(headers));
        kafkaSender.synSendMessage(message);
        return FairyResult.success();
    }
}
