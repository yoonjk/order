package com.ibm.lab.order.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.lab.order.adapter.ParticipantLink;
import com.ibm.lab.order.adapter.ParticipationRequest;
import com.ibm.lab.order.adapter.RestAdapter;
import com.ibm.lab.order.model.Order;
import com.ibm.lab.order.model.OrderEvent;
import com.ibm.lab.order.model.OrderItem;
import com.ibm.lab.order.model.OrderStatus;
import com.ibm.lab.order.repository.OrderRepository;

@Service
public class OrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	@Autowired
	private RestAdapter restAdapter;
	
	@Value("${stock.url}")
	private String stockUrl;
	
	@Value("${payment.url}")
	private String paymentUrl;
	
	@Value("${academy.username}")
	private String username;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	public void setRestAdapter(RestAdapter restAdapter) {
		this.restAdapter = restAdapter;
	}
	
	/**
	 * 주문처리  
	 * @param order
	 */
	@Transactional
	public void placeOrder(Order order) {
		logger.info("Starting Order ...");
		

        // 결제 요청
        ParticipationRequest paymentParticipationRequest = payOrder(order);
        logger.info("1. payOrder");
		// REST Resources 생성
        // 재고 차감 요청
        ParticipationRequest stockParticipationRequest = reduceStock(order);
        logger.info("2. reduceStock");
        
        // 주문서 작성 
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getOrderId());
        orderItem.setUsername(username);
        orderItem.setStatus(OrderEvent.CREATE.toString());
        orderItem.setProductCode(order.getProductCode());
        orderItem.setQty(order.getQty());
        orderItem.setAmt(order.getAmt());
        
        
        // 주문상태 
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(order.getOrderId());
        orderStatus.setUsername(username);
        orderStatus.setStatus(OrderEvent.REJECT.toString());
        
        orderRepository.saveOrder(orderItem);
        // 1. TCC - Try
        
        List<ParticipantLink> participantLinks  = null;
        
        try {
        	participantLinks = restAdapter.doTry(Arrays.asList(stockParticipationRequest, paymentParticipationRequest));
        } catch(Exception e) {
            e.printStackTrace();
        	orderRepository.updateOrderStatus(orderStatus);
        	return;
        }
        // Exception Path
        // TCC_TRY는 모두 성공했지만 
        // 내부_오류로_인해_TCC_Confirm_하지_않는_경우
        if(order.getProductCode().equals("prd-0002")) {
            throw new RuntimeException("Error Before Confirm...");
        }
        
        // Exception Path
        // TCC_TRY는_모두_성공했지만_
        // 내부_오류_후_명시적으로_Cancel_하는_경우
        if(order.getProductCode().equals("prd-0004")) {
        	orderStatus.setStatus(OrderEvent.REJECT.toString());
            orderRepository.updateOrderStatus(orderStatus);
            restAdapter.cancelAll(participantLinks);
            throw new RuntimeException("Error Before Confirm...");
        }
        

        // 2. TCC - Confirm
        restAdapter.confirmAll(participantLinks);
        orderStatus.setStatus(OrderEvent.APPROVAL.toString());
        logger.info("orderStatus:{}",  orderStatus);
        orderRepository.updateOrderStatus(orderStatus);
        logger.info(" =============== End of order");
	}
	
	/**
	 * 주문 Id로 삭제 
	 * @param orderId
	 */
	public void deleteOrder(String orderId) {
		OrderItem orderItem = new OrderItem();
	
		orderItem.setOrderId(orderId);
		orderItem.setUsername(username);
		
		orderRepository.deleteOrder(orderItem);
	}
	
	/**
	 * 해당 사용자의 모든 주문 삭제 
	 */
	public void deleteAll() {		
		orderRepository.deleteAll(username);
	}	
	
	public List<OrderItem> findAll() {
		return orderRepository.findAll(username);
	}
	
	/**
	 * 재고 차감 
	 * @param order
	 * @return
	 */
	private ParticipationRequest reduceStock(final Order order) {
		final String requestURL = stockUrl;
		Map<String, Object> requestBody = new HashMap<>();
		
		requestBody.put("orderId",  order.getOrderId());
		requestBody.put("adjustmentType",  "REDUCE");
		requestBody.put("productCode",  order.getProductCode());
		requestBody.put("qty",  order.getQty());
		
		return new ParticipationRequest(requestURL, requestBody);
	}
	
	/**
	 * 결제  처리 
	 * @param order
	 * @return
	 */
	private ParticipationRequest payOrder(final Order order) {
		final String requestURL = paymentUrl;
		Map<String, Object> requestBody = new HashMap<>();
		
		requestBody.put("orderId",  order.getOrderId());
		requestBody.put("amt",  order.getAmt());
		
		return new ParticipationRequest(requestURL, requestBody);
	}
	
	private void waitCurrentThread(int seconds) throws InterruptedException {
		Thread.currentThread().sleep(TimeUnit.SECONDS.toMillis(seconds));
	}
}
