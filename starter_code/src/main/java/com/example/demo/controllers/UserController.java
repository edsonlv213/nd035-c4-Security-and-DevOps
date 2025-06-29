
package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;


	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		String username = createUserRequest.getUsername();

		logger.info("CreateUser request initiated for username: {}", username);

		// Validação: verificar se password e confirmPassword são iguais
		if (createUserRequest.getPassword() == null || createUserRequest.getConfirmPassword() == null) {
			logger.warn("CreateUser request failed for username: {} - Password or confirmPassword is null", username);
			return ResponseEntity.badRequest().build();
		}

		if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			logger.warn("CreateUser request failed for username: {} - Password and confirmPassword do not match", username);
			return ResponseEntity.badRequest().build();
		}

		// Validação adicional: verificar se a senha tem pelo menos 7 caracteres
		if (createUserRequest.getPassword().length() < 7) {
			logger.warn("CreateUser request failed for username: {} - Password length is less than 7 characters", username);
			return ResponseEntity.badRequest().build();
		}

		// Validação: verificar se a senha contém pelo menos um dígito e uma letra
		String password = createUserRequest.getPassword();
		boolean hasDigit = password.matches(".*\\d.*");
		boolean hasLetter = password.matches(".*[a-zA-Z].*");

		if (!hasDigit || !hasLetter) {
			logger.warn("CreateUser request failed for username: {} - Password does not meet complexity requirements (must contain at least one digit and one letter)", username);
			return ResponseEntity.badRequest().build();
		}

		try {
			// Verificar se o usuário já existe
			User existingUser = userRepository.findByUsername(username);
			if (existingUser != null) {
				logger.warn("CreateUser request failed for username: {} - Username already exists", username);
				return ResponseEntity.badRequest().build();
			}

			User user = new User();
			user.setUsername(username);
			user.setPassword(bCryptPasswordEncoder.encode(password));

			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);

			logger.info("CreateUser request successful for username: {} - User created successfully with ID: {}", username, user.getId());
			return ResponseEntity.ok(user);

		} catch (Exception e) {
			logger.error("CreateUser request failed for username: {} - Unexpected error occurred: {}", username, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}