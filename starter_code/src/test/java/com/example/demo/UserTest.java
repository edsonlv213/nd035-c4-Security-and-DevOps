package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.Cart;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Entity Tests")
public class UserTest {

    private User user;
    
    @Mock
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    // Testes para Getters e Setters - ID
    @Test
    @DisplayName("Deve definir e obter ID corretamente")
    void testSetAndGetId() {
        // Given
        long expectedId = 1L;
        
        // When
        user.setId(expectedId);
        
        // Then
        assertEquals(expectedId, user.getId());
    }

    @Test
    @DisplayName("Deve ter ID padrão como zero")
    void testDefaultId() {
        // Given
        User newUser = new User();
        
        // Then
        assertEquals(0L, newUser.getId());
    }

    // Testes para Username
    @Test
    @DisplayName("Deve definir e obter username corretamente")
    void testSetAndGetUsername() {
        // Given
        String expectedUsername = "testuser";
        
        // When
        user.setUsername(expectedUsername);
        
        // Then
        assertEquals(expectedUsername, user.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar username nulo")
    void testSetUsernameNull() {
        // When
        user.setUsername(null);
        
        // Then
        assertNull(user.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar username vazio")
    void testSetUsernameEmpty() {
        // Given
        String emptyUsername = "";
        
        // When
        user.setUsername(emptyUsername);
        
        // Then
        assertEquals(emptyUsername, user.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar username com espaços")
    void testSetUsernameWithSpaces() {
        // Given
        String usernameWithSpaces = "user name";
        
        // When
        user.setUsername(usernameWithSpaces);
        
        // Then
        assertEquals(usernameWithSpaces, user.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar username longo")
    void testSetLongUsername() {
        // Given
        String longUsername = "a".repeat(100);
        
        // When
        user.setUsername(longUsername);
        
        // Then
        assertEquals(longUsername, user.getUsername());
    }

    // Testes para Password
    @Test
    @DisplayName("Deve definir e obter password corretamente")
    void testSetAndGetPassword() {
        // Given
        String expectedPassword = "securePassword123";
        
        // When
        user.setPassword(expectedPassword);
        
        // Then
        assertEquals(expectedPassword, user.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar password nulo")
    void testSetPasswordNull() {
        // When
        user.setPassword(null);
        
        // Then
        assertNull(user.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar password vazio")
    void testSetPasswordEmpty() {
        // Given
        String emptyPassword = "";
        
        // When
        user.setPassword(emptyPassword);
        
        // Then
        assertEquals(emptyPassword, user.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar password com caracteres especiais")
    void testSetPasswordWithSpecialCharacters() {
        // Given
        String passwordWithSpecialChars = "P@ssw0rd!#$%";
        
        // When
        user.setPassword(passwordWithSpecialChars);
        
        // Then
        assertEquals(passwordWithSpecialChars, user.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar password longo")
    void testSetLongPassword() {
        // Given
        String longPassword = "a".repeat(200);
        
        // When
        user.setPassword(longPassword);
        
        // Then
        assertEquals(longPassword, user.getPassword());
    }

    // Testes para Cart
    @Test
    @DisplayName("Deve definir e obter cart corretamente")
    void testSetAndGetCart() {
        // When
        user.setCart(mockCart);
        
        // Then
        assertEquals(mockCart, user.getCart());
    }

    @Test
    @DisplayName("Deve aceitar cart nulo")
    void testSetCartNull() {
        // When
        user.setCart(null);
        
        // Then
        assertNull(user.getCart());
    }

    @Test
    @DisplayName("Deve substituir cart existente")
    void testReplaceExistingCart() {
        // Given
        Cart firstCart = mock(Cart.class);
        Cart secondCart = mock(Cart.class);
        user.setCart(firstCart);
        
        // When
        user.setCart(secondCart);
        
        // Then
        assertEquals(secondCart, user.getCart());
        assertNotEquals(firstCart, user.getCart());
    }

    // Testes de Estado Inicial
    @Test
    @DisplayName("Deve ter estado inicial correto")
    void testInitialState() {
        // Given
        User newUser = new User();
        
        // Then
        assertEquals(0L, newUser.getId());
        assertNull(newUser.getUsername());
        assertNull(newUser.getPassword());
        assertNull(newUser.getCart());
    }

    // Testes de Cenários Completos
    @Test
    @DisplayName("Deve criar usuário completo corretamente")
    void testCompleteUserCreation() {
        // Given
        long expectedId = 123L;
        String expectedUsername = "johndoe";
        String expectedPassword = "hashedPassword123";
        
        // When
        user.setId(expectedId);
        user.setUsername(expectedUsername);
        user.setPassword(expectedPassword);
        user.setCart(mockCart);
        
        // Then
        assertEquals(expectedId, user.getId());
        assertEquals(expectedUsername, user.getUsername());
        assertEquals(expectedPassword, user.getPassword());
        assertEquals(mockCart, user.getCart());
    }

    @Test
    @DisplayName("Deve permitir modificação de todos os campos")
    void testFieldModification() {
        // Given - Estado inicial
        user.setId(1L);
        user.setUsername("initialUser");
        user.setPassword("initialPassword");
        user.setCart(mockCart);
        
        // When - Modificação
        Cart newCart = mock(Cart.class);
        user.setId(2L);
        user.setUsername("modifiedUser");
        user.setPassword("modifiedPassword");
        user.setCart(newCart);
        
        // Then
        assertEquals(2L, user.getId());
        assertEquals("modifiedUser", user.getUsername());
        assertEquals("modifiedPassword", user.getPassword());
        assertEquals(newCart, user.getCart());
    }

    // Testes de Validação de Comportamento
    @Test
    @DisplayName("Deve manter independência entre instâncias")
    void testInstanceIndependence() {
        // Given
        User user1 = new User();
        User user2 = new User();
        
        // When
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("password1");
        
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPassword("password2");
        
        // Then
        assertNotEquals(user1.getId(), user2.getId());
        assertNotEquals(user1.getUsername(), user2.getUsername());
        assertNotEquals(user1.getPassword(), user2.getPassword());
    }

    // Testes para Casos Limites
    @Test
    @DisplayName("Deve lidar com valores extremos de ID")
    void testExtremeIdValues() {
        // Test com valor máximo
        user.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, user.getId());
        
        // Test com valor mínimo
        user.setId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, user.getId());
        
        // Test com zero
        user.setId(0L);
        assertEquals(0L, user.getId());
    }

    @Test
    @DisplayName("Deve aceitar strings Unicode em username")
    void testUnicodeUsername() {
        // Given
        String unicodeUsername = "usuário_测试_🚀";
        
        // When
        user.setUsername(unicodeUsername);
        
        // Then
        assertEquals(unicodeUsername, user.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar strings Unicode em password")
    void testUnicodePassword() {
        // Given
        String unicodePassword = "senha_密码_🔒";
        
        // When
        user.setPassword(unicodePassword);
        
        // Then
        assertEquals(unicodePassword, user.getPassword());
    }

    // Teste de Encadeamento de Métodos (Builder Pattern Style)
    @Test
    @DisplayName("Deve permitir configuração fluente")
    void testFluentConfiguration() {
        // Given
        long id = 1L;
        String username = "testuser";
        String password = "testpass";
        
        // When - Simulando configuração fluente
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setCart(mockCart);
        
        // Then
        assertAll("Configuração fluente",
            () -> assertEquals(id, user.getId()),
            () -> assertEquals(username, user.getUsername()),
            () -> assertEquals(password, user.getPassword()),
            () -> assertEquals(mockCart, user.getCart())
        );
    }

    // Teste de Imutabilidade após definição
    @Test
    @DisplayName("Deve permitir redefinição de valores")
    void testValueReassignment() {
        // Given
        user.setUsername("originalUsername");
        user.setPassword("originalPassword");
        
        // When
        user.setUsername("newUsername");
        user.setPassword("newPassword");
        
        // Then
        assertEquals("newUsername", user.getUsername());
        assertEquals("newPassword", user.getPassword());
    }
}