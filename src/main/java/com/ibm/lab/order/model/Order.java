package com.ibm.lab.order.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Order {
    private String orderId;
    private String productCode;
    private Long qty;
    private Long amt;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setAmt(Long amt) {
        this.amt = amt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public Long getAmt() {
        return amt;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }
}
