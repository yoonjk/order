package com.ibm.lab.order.repository;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ibm.lab.order.model.Order;
import com.ibm.lab.order.model.OrderItem;
import com.ibm.lab.order.model.OrderStatus;

@Repository
public class OrderRepository {
	private static final Logger logger = LoggerFactory.getLogger(OrderRepository.class);
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	/**
	 * username 
	 * @return
	 */
	public List<OrderItem> findAll(String username) {
		List<OrderItem> orders = sqlSessionTemplate.selectList("order.findAll", username);
		logger.info("order findAll:{}", orders);
		
		return orders;
	}
	
	/**
	 * 다건 조회 
	 * username 
	 * @return
	 */
	public List<Order> findByUsername(String username) {
		List<Order> orders = sqlSessionTemplate.selectList("order.findByUsername", username);
		logger.info("order findByUsername list:{}", orders);
		
		return orders;
	}
	
	/**
	 * 다건 조회 
	 * productCode  
	 * @return
	 */
	public List<Order> findByProductCode(String productCode) {
		List<Order> orders = sqlSessionTemplate.selectList("order.findByProductCode", productCode);
		logger.info("order findByProductCode List:{}", orders);
		
		return orders;
	}	
	
	/**
	 * order 저장 
	 * orderItem  
	 * @return
	 */
	public int saveOrder(OrderItem orderItem) {
		int rc = sqlSessionTemplate.insert("order.saveOrder", orderItem);
		logger.info("order saveOrder rc:{}", rc);
		
		return rc;
	}		
	
	/**
	 * order 상태 저장 
	 * orderStatus  
	 * @return
	 */
	public int updateOrderStatus(OrderStatus orderStatus) {
		int rc = sqlSessionTemplate.update("order.updateOrderStatus", orderStatus);
		logger.info("order updateOrderStatus rc:{}", rc);
		
		return rc;
	}
	
	/**
	 * order 삭제  
	 * orderStatus  
	 * @return
	 */
	public int deleteOrder(OrderItem orderItem) {
		int rc = sqlSessionTemplate.delete("order.deleteOrder", orderItem);
		logger.info("Order deleteOrder rc:{}", rc);
		
		return rc;
	}	
	
	/**
	 * deleteAll 
	 * username  
	 * @return
	 */
	public int deleteAll(String username) {
		int rc = sqlSessionTemplate.delete("order.deleteAll", username);
		logger.info("order deleteAll rc:{}", rc);
		
		return rc;
	}	
}
