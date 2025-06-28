package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class CartControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private ItemRepository itemRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    // ==================== TESTES PARA addToCart() ====================

    @Test
    @WithMockUser
    @DisplayName("Adicionar item ao carrinho - sucesso")
    void addToCart_Success() throws Exception {
        // Given
        ModifyCartRequest request = createValidAddCartRequest();
        User user = createTestUser();
        Item item = createTestItem();
        Cart cart = user.getCart();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // When & Then
        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items").isArray());

        verify(userRepository).findByUsername("testuser");
        verify(itemRepository).findById(1L);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Adicionar item ao carrinho - usuário não encontrado")
    void addToCart_UserNotFound() throws Exception {
        // Given
        ModifyCartRequest request = createValidAddCartRequest();
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        request.setUsername("nonexistent");

        // When & Then
        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userRepository).findByUsername("nonexistent");
        verify(itemRepository, never()).findById(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Adicionar item ao carrinho - item não encontrado")
    void addToCart_ItemNotFound() throws Exception {
        // Given
        ModifyCartRequest request = createValidAddCartRequest();
        User user = createTestUser();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        request.setItemId(999L);

        // When & Then
        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userRepository).findByUsername("testuser");
        verify(itemRepository).findById(999L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Adicionar múltiplos itens ao carrinho")
    void addToCart_MultipleQuantity() throws Exception {
        // Given
        ModifyCartRequest request = createValidAddCartRequest();
        request.setQuantity(3);

        User user = createTestUser();
        Item item = createTestItem();
        Cart cart = user.getCart();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // When & Then
        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userRepository).findByUsername("testuser");
        verify(itemRepository).findById(1L);
        verify(cartRepository).save(any(Cart.class));
    }

    // ==================== TESTES PARA removeFromCart() ====================

    @Test
    @WithMockUser
    @DisplayName("Remover item do carrinho - sucesso")
    void removeFromCart_Success() throws Exception {
        // Given
        ModifyCartRequest request = createValidRemoveCartRequest();
        User user = createTestUser();
        Item item = createTestItem();
        Cart cart = user.getCart();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // When & Then
        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(userRepository).findByUsername("testuser");
        verify(itemRepository).findById(1L);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Remover item do carrinho - usuário não encontrado")
    void removeFromCart_UserNotFound() throws Exception {
        // Given
        ModifyCartRequest request = createValidRemoveCartRequest();
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        request.setUsername("nonexistent");

        // When & Then
        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userRepository).findByUsername("nonexistent");
        verify(itemRepository, never()).findById(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Remover item do carrinho - item não encontrado")
    void removeFromCart_ItemNotFound() throws Exception {
        // Given
        ModifyCartRequest request = createValidRemoveCartRequest();
        User user = createTestUser();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        request.setItemId(999L);

        // When & Then
        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userRepository).findByUsername("testuser");
        verify(itemRepository).findById(999L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Remover múltiplos itens do carrinho")
    void removeFromCart_MultipleQuantity() throws Exception {
        // Given
        ModifyCartRequest request = createValidRemoveCartRequest();
        request.setQuantity(2);

        User user = createTestUser();
        Item item = createTestItem();
        Cart cart = user.getCart();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // When & Then
        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userRepository).findByUsername("testuser");
        verify(itemRepository).findById(1L);
        verify(cartRepository).save(any(Cart.class));
    }

    // ==================== TESTES DE VALIDAÇÃO DE ENTRADA ====================

    @Test
    @WithMockUser
    @DisplayName("Adicionar item com request inválido")
    void addToCart_InvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Remover item com request inválido")
    void removeFromCart_InvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ==================== TESTES DE SEGURANÇA ====================

    @Test
    @DisplayName("Adicionar item sem autenticação - deve retornar 401")
    void addToCart_Unauthorized() throws Exception {
        // Given
        ModifyCartRequest request = createValidAddCartRequest();

        // When & Then
        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    @DisplayName("Remover item sem autenticação - deve retornar 401")
    void removeFromCart_Unauthorized() throws Exception {
        // Given
        ModifyCartRequest request = createValidRemoveCartRequest();

        // When & Then
        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userRepository, never()).findByUsername(any());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private ModifyCartRequest createValidAddCartRequest() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);
        return request;
    }

    private ModifyCartRequest createValidRemoveCartRequest() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);
        return request;
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);
        user.setCart(cart);

        return user;
    }

    private Item createTestItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("19.99"));
        return item;
    }
}