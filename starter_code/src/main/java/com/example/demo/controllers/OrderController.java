
package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		logger.info("Order submission request initiated for username: {}", username);

		try {
			User user = userRepository.findByUsername(username);
			if(user == null) {
				logger.warn("Order submission failed for username: {} - User not found", username);
				return ResponseEntity.notFound().build();
			}

			if(user.getCart() == null) {
				logger.warn("Order submission failed for username: {} - User cart is null", username);
				return ResponseEntity.badRequest().build();
			}

			if(user.getCart().getItems() == null || user.getCart().getItems().isEmpty()) {
				logger.warn("Order submission failed for username: {} - Cart is empty", username);
				return ResponseEntity.badRequest().build();
			}

			logger.debug("Creating order for username: {} with {} items, total: {}",
					username,
					user.getCart().getItems().size(),
					user.getCart().getTotal());

			UserOrder order = UserOrder.createFromCart(user.getCart());
			order.setUser(user);

			logger.debug("Saving order to database for username: {}", username);
			UserOrder savedOrder = orderRepository.save(order);

			logger.info("Order submission successful for username: {} - Order created with ID: {}, total: {}, items count: {}",
					username,
					savedOrder.getId(),
					savedOrder.getTotal(),
					savedOrder.getItems().size());

			return ResponseEntity.ok(savedOrder);

		} catch (Exception e) {
			logger.error("Order submission failed for username: {} - Unexpected error occurred: {}",
					username, e.getMessage(), e);
			return ResponseEntity.status(500).build();
		}
	}

	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		logger.info("Order history request initiated for username: {}", username);

		try {
			User user = userRepository.findByUsername(username);
			if(user == null) {
				logger.warn("Order history request failed for username: {} - User not found", username);
				return ResponseEntity.notFound().build();
			}

			logger.debug("Retrieving order history for username: {}", username);
			List<UserOrder> orders = orderRepository.findByUser(user);

			if(orders == null || orders.isEmpty()) {
				logger.info("Order history request completed for username: {} - No orders found", username);
			} else {
				logger.info("Order history request successful for username: {} - Retrieved {} orders",
						username, orders.size());
			}

			return ResponseEntity.ok(orders);

		} catch (Exception e) {
			logger.error("Order history request failed for username: {} - Unexpected error occurred: {}",
					username, e.getMessage(), e);
			return ResponseEntity.status(500).build();
		}
	}
}