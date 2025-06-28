package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    // ==================== TESTES PARA createUser() ====================

    @Test
    @DisplayName("Criar usuário com sucesso")
    void createUser_Success() throws Exception {
        // Given
        CreateUserRequest request = createValidUserRequest();
        Cart cart = new Cart();
        cart.setId(1L);

        when(bCryptPasswordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Usar doAnswer para simular o comportamento do banco que gera o ID
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L); // Simula o ID gerado pelo banco
            return user;
        }).when(userRepository).save(any(User.class));

        // When & Then
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.id").value(1));

        verify(userRepository).save(any(User.class));
        verify(cartRepository).save(any(Cart.class));
        verify(bCryptPasswordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Criar usuário - senha nula")
    void createUser_NullPassword() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword(null);
        request.setConfirmPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar usuário - confirmPassword nula")
    void createUser_NullConfirmPassword() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setConfirmPassword(null);

        // When & Then
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar usuário - senhas diferentes")
    void createUser_PasswordMismatch() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setConfirmPassword("differentPassword");

        // When & Then
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar usuário - senha muito curta")
    void createUser_PasswordTooShort() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("abc12"); // menos de 7 caracteres
        request.setConfirmPassword("abc12");

        // When & Then
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar usuário - senha sem dígito")
    void createUser_PasswordWithoutDigit() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("passwordonly"); // sem dígito
        request.setConfirmPassword("passwordonly");

        // When & Then
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar usuário - senha sem letra")
    void createUser_PasswordWithoutLetter() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("1234567"); // sem letra
        request.setConfirmPassword("1234567");

        // When & Then
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }

    // ==================== TESTES PARA findById() ====================

    @Test
    @WithMockUser
    @DisplayName("Buscar usuário por ID - sucesso")
    void findById_Success() throws Exception {
        // Given
        User user = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/api/user/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userRepository).findById(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar usuário por ID - não encontrado")
    void findById_NotFound() throws Exception {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/user/id/999"))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(999L);
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar usuário por ID - ID inválido")
    void findById_InvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/id/abc"))
                .andExpect(status().isBadRequest());
    }

    // ==================== TESTES PARA findByUserName() ====================

    @Test
    @WithMockUser
    @DisplayName("Buscar usuário por username - sucesso")
    void findByUserName_Success() throws Exception {
        // Given
        User user = createTestUser();
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/api/user/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.id").value(1));

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar usuário por username - não encontrado")
    void findByUserName_NotFound() throws Exception {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/user/nonexistent"))
                .andExpect(status().isNotFound());

        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar usuário por username - string vazia")
    void findByUserName_EmptyString() throws Exception {
        // Given
        when(userRepository.findByUsername("")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/user/"))
                .andExpect(status().isNotFound());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private CreateUserRequest createValidUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        return request;
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedPassword");

        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        return user;
    }
}