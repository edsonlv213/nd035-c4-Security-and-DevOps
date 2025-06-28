package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class OrderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    // ==================== TESTES PARA submit() ====================

    @Test
    @WithMockUser
    @DisplayName("Submeter pedido - sucesso")
    void submit_Success() throws Exception {
        // Given
        User user = createTestUserWithItems();

        // Simular o que o UserOrder.createFromCart() retornaria
        UserOrder mockOrder = new UserOrder();
        mockOrder.setId(1L);
        mockOrder.setUser(user);
        mockOrder.setItems(user.getCart().getItems());
        mockOrder.setTotal(user.getCart().getTotal());

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(orderRepository.save(any(UserOrder.class))).thenReturn(mockOrder);

        // When & Then
        mockMvc.perform(post("/api/order/submit/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.items").isArray());

        verify(userRepository).findByUsername("testuser");
        verify(orderRepository).save(any(UserOrder.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Submeter pedido - usuário não encontrado")
    void submit_UserNotFound() throws Exception {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/order/submit/nonexistent"))
                .andExpect(status().isNotFound());

        verify(userRepository).findByUsername("nonexistent");
        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Submeter pedido - carrinho vazio")
    void submit_EmptyCart() throws Exception {
        // Given
        User user = createTestUserWithEmptyCart();

        UserOrder mockOrder = new UserOrder();
        mockOrder.setId(1L);
        mockOrder.setUser(user);
        mockOrder.setItems(new ArrayList<>());
        mockOrder.setTotal(BigDecimal.ZERO);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(orderRepository.save(any(UserOrder.class))).thenReturn(mockOrder);

        // When & Then
        mockMvc.perform(post("/api/order/submit/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").value(0));

        verify(userRepository).findByUsername("testuser");
        verify(orderRepository).save(any(UserOrder.class));
    }

    // ==================== TESTES PARA getOrdersForUser() ====================

    @Test
    @WithMockUser
    @DisplayName("Obter histórico de pedidos - sucesso")
    void getOrdersForUser_Success() throws Exception {
        // Given
        User user = createTestUser();
        List<UserOrder> orders = createTestOrderHistory(user);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/order/history/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(userRepository).findByUsername("testuser");
        verify(orderRepository).findByUser(user);
    }

    @Test
    @WithMockUser
    @DisplayName("Obter histórico de pedidos - usuário não encontrado")
    void getOrdersForUser_UserNotFound() throws Exception {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/order/history/nonexistent"))
                .andExpect(status().isNotFound());

        verify(userRepository).findByUsername("nonexistent");
        verify(orderRepository, never()).findByUser(any(User.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Obter histórico de pedidos - lista vazia")
    void getOrdersForUser_EmptyHistory() throws Exception {
        // Given
        User user = createTestUser();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/order/history/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userRepository).findByUsername("testuser");
        verify(orderRepository).findByUser(user);
    }

    // ==================== TESTES DE SEGURANÇA ====================

    @Test
    @DisplayName("Submeter pedido sem autenticação - deve retornar 401")
    void submit_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/order/submit/testuser"))
                .andExpect(status().isUnauthorized());

        verify(userRepository, never()).findByUsername(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obter histórico sem autenticação - deve retornar 401")
    void getOrdersForUser_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/order/history/testuser"))
                .andExpect(status().isUnauthorized());

        verify(userRepository, never()).findByUsername(any());
        verify(orderRepository, never()).findByUser(any());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedPassword");
        return user;
    }

    private User createTestUserWithItems() {
        User user = createTestUser();

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(createTestItems());
        cart.setTotal(new BigDecimal("39.98"));
        cart.setUser(user);
        user.setCart(cart);

        return user;
    }

    private User createTestUserWithEmptyCart() {
        User user = createTestUser();

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);
        cart.setUser(user);
        user.setCart(cart);

        return user;
    }

    private List<Item> createTestItems() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Laptop");
        item1.setDescription("Gaming laptop");
        item1.setPrice(new BigDecimal("19.99"));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Mouse");
        item2.setDescription("Gaming mouse");
        item2.setPrice(new BigDecimal("19.99"));

        return Arrays.asList(item1, item2);
    }

    private List<UserOrder> createTestOrderHistory(User user) {
        UserOrder order1 = new UserOrder();
        order1.setId(1L);
        order1.setUser(user);
        order1.setItems(createTestItems());
        order1.setTotal(new BigDecimal("39.98"));

        UserOrder order2 = new UserOrder();
        order2.setId(2L);
        order2.setUser(user);
        order2.setItems(Arrays.asList(createTestItems().get(0)));
        order2.setTotal(new BigDecimal("19.99"));

        return Arrays.asList(order1, order2);
    }
}