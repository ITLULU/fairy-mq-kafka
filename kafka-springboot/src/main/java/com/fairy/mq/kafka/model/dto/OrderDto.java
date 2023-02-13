package com.fairy.mq.kafka.model.dto;


import java.io.Serializable;

/**
 * @author hll
 * @version 1.0
 * @date 2022/7/6 14:36
 */
public class OrderDto implements Serializable {
    private Integer orderId;
    private Integer productId;
    private Integer productNum;
    private Double orderAmount;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductNum() {
        return productNum;
    }

    public void setProductNum(Integer productNum) {
        this.productNum = productNum;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public OrderDto() {
        //Empty construct
    }

    public OrderDto(Integer orderId, Integer productId, Integer productNum, Double orderAmount) {
        this.orderId = orderId;
        this.productId = productId;
        this.productNum = productNum;
        this.orderAmount = orderAmount;
    }
}
