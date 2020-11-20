package com.ibm.lab.order.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ibm.lab.order.dto.OrderDto;
import com.ibm.lab.order.dto.ProductDto;
import com.ibm.lab.order.model.Order;
import com.ibm.lab.order.model.OrderItem;
import com.ibm.lab.order.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	private final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
    private OrderService orderService;
	
    @Autowired
    RestTemplate restTemplate;
    
	@Value("${product.url}")
	private String productUrl;
	
	
    
    @Bean
    public RestTemplate getRestTemplate() {
    	return new RestTemplate();
    }
    
    @GetMapping
    public ResponseEntity<List<OrderItem>> getOrders() {
    	
    	List orders = orderService.findAll();
    	
    	return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/products/{productCode}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String productCode) {
    	ProductDto product = (ProductDto) restTemplate.exchange(productUrl+"/"+productCode,
                HttpMethod.GET, null, new ParameterizedTypeReference<ProductDto>() {}).getBody();
        
    	return new ResponseEntity<>(product, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody final OrderDto orderDto) {
        // OrderId : Unique ID
    	final String orderId = UUID.randomUUID().toString().toUpperCase();
        
        try {
    	ProductDto product = (ProductDto) restTemplate.exchange(productUrl+"/"+orderDto.getProductCode(),
                HttpMethod.GET, null, new ParameterizedTypeReference<ProductDto>() {}).getBody();

    	if (product == null) {
    		return new ResponseEntity<>("해당 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST); 
    	}
        } catch(Exception e) {
        	e.printStackTrace();
        	return new ResponseEntity<>("해당 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST); 
        }
    	
        Order order = new Order();
        
        order.setOrderId(orderId);
        order.setProductCode(orderDto.getProductCode());
        order.setAmt(orderDto.getAmt());
        order.setQty(orderDto.getQty());

        // call service
        logger.info("call orderService");
        orderService.placeOrder(order);

        return new ResponseEntity<>("정상처리되었습니다", HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) {
    	
    	orderService.deleteOrder(orderId);
    	
    	return new ResponseEntity<>("정상 처리되었습니다.", HttpStatus.OK);
    }
    @DeleteMapping
    public ResponseEntity<String> deleteAll() {
    	
    	orderService.deleteAll();
    	
    	return new ResponseEntity<>("정상 처리되었습니다.", HttpStatus.OK);
    } 

    @GetMapping("/ready")
    public Response ready() throws Exception {
        return Response.ok("OK\n").build();
    }
}
