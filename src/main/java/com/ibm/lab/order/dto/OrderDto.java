package com.ibm.lab.order.dto;

public class OrderDto {
	private String productCode;
	private Long amt;
	private Long qty;
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public Long getAmt() {
		return amt;
	}
	public void setAmt(Long price) {
		this.amt = price;
	}
	public Long getQty() {
		return qty;
	}
	public void setQty(Long qty) {
		this.qty = qty;
	}
}
