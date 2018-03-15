package com.dongyin.ecommerce.dl.gateway.web.vo;

import com.dongyin.leaf.wsacd.dto.ActOrderDTO;
import com.dongyin.leaf.wsacd.dto.ActOrderDetailsDTO;

import java.util.List;

public class ActOrderVO extends ActOrderDTO{
    private Long orderId;

    private String unitPriceStr;

    private String orderDate;

    private String totalAmount;

    private String orderAwardStr;

    private Long orderAward; //订单奖励

    private String express;

    private String expressNo;

    private String expressCode;

    private Integer orderStatus;

    private List<ActOrderDetailsDTO> actOrderDetailsDTOList;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getUnitPriceStr() {
        return unitPriceStr;
    }

    public void setUnitPriceStr(String unitPriceStr) {
        this.unitPriceStr = unitPriceStr;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderAwardStr() {
        return orderAwardStr;
    }

    public void setOrderAwardStr(String orderAwardStr) {
        this.orderAwardStr = orderAwardStr;
    }

    @Override
    public Long getOrderAward() {
        return orderAward;
    }

    @Override
    public void setOrderAward(Long orderAward) {
        this.orderAward = orderAward;
    }

    @Override
    public String getExpress() {
        return express;
    }

    @Override
    public void setExpress(String express) {
        this.express = express;
    }

    @Override
    public String getExpressNo() {
        return expressNo;
    }

    @Override
    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    @Override
    public String getExpressCode() {
        return expressCode;
    }

    @Override
    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    @Override
    public Integer getOrderStatus() {
        return orderStatus;
    }

    @Override
    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public List<ActOrderDetailsDTO> getActOrderDetailsDTOList() {
        return actOrderDetailsDTOList;
    }

    @Override
    public void setActOrderDetailsDTOList(List<ActOrderDetailsDTO> actOrderDetailsDTOList) {
        this.actOrderDetailsDTOList = actOrderDetailsDTOList;
    }
}
