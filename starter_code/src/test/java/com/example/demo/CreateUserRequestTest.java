package com.example.demo;

import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateUserRequestTest {

    private CreateUserRequest createUserRequest;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest();
        objectMapper = new ObjectMapper();
    }

    // ==================== TESTES DE GETTERS E SETTERS ====================

    @Test
    @DisplayName("Deve definir e obter username corretamente")
    void testUsernameGetterSetter() {
        // Given
        String expectedUsername = "testuser";

        // When
        createUserRequest.setUsername(expectedUsername);

        // Then
        assertEquals(expectedUsername, createUserRequest.getUsername());
    }

    @Test
    @DisplayName("Deve definir e obter password corretamente")
    void testPasswordGetterSetter() {
        // Given
        String expectedPassword = "password123";

        // When
        createUserRequest.setPassword(expectedPassword);

        // Then
        assertEquals(expectedPassword, createUserRequest.getPassword());
    }

    @Test
    @DisplayName("Deve definir e obter confirmPassword corretamente")
    void testConfirmPasswordGetterSetter() {
        // Given
        String expectedConfirmPassword = "password123";

        // When
        createUserRequest.setConfirmPassword(expectedConfirmPassword);

        // Then
        assertEquals(expectedConfirmPassword, createUserRequest.getConfirmPassword());
    }

    // ==================== TESTES DE VALORES NULOS ====================

    @Test
    @DisplayName("Deve aceitar username nulo")
    void testNullUsername() {
        // When
        createUserRequest.setUsername(null);

        // Then
        assertNull(createUserRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar password nulo")
    void testNullPassword() {
        // When
        createUserRequest.setPassword(null);

        // Then
        assertNull(createUserRequest.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar confirmPassword nulo")
    void testNullConfirmPassword() {
        // When
        createUserRequest.setConfirmPassword(null);

        // Then
        assertNull(createUserRequest.getConfirmPassword());
    }

    // ==================== TESTES DE VALORES VAZIOS ====================

    @Test
    @DisplayName("Deve aceitar username vazio")
    void testEmptyUsername() {
        // When
        createUserRequest.setUsername("");

        // Then
        assertEquals("", createUserRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar password vazio")
    void testEmptyPassword() {
        // When
        createUserRequest.setPassword("");

        // Then
        assertEquals("", createUserRequest.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar confirmPassword vazio")
    void testEmptyConfirmPassword() {
        // When
        createUserRequest.setConfirmPassword("");

        // Then
        assertEquals("", createUserRequest.getConfirmPassword());
    }

    // ==================== TESTES DE SERIALIZAÇÃO JSON ====================

    @Test
    @DisplayName("Deve serializar para JSON corretamente")
    void testJsonSerialization() throws Exception {
        // Given
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("password123");
        createUserRequest.setConfirmPassword("password123");

        // When
        String json = objectMapper.writeValueAsString(createUserRequest);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"username\":\"testuser\""));
        assertTrue(json.contains("\"password\":\"password123\""));
        assertTrue(json.contains("\"confirmPassword\":\"password123\""));
    }

    @Test
    @DisplayName("Deve deserializar de JSON corretamente")
    void testJsonDeserialization() throws Exception {
        // Given
        String json = "{\"username\":\"testuser\",\"password\":\"password123\",\"confirmPassword\":\"password123\"}";

        // When
        CreateUserRequest result = objectMapper.readValue(json, CreateUserRequest.class);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertEquals("password123", result.getConfirmPassword());
    }

    @Test
    @DisplayName("Deve deserializar JSON com campos opcionais ausentes")
    void testJsonDeserializationWithMissingFields() throws Exception {
        // Given
        String json = "{\"username\":\"testuser\"}";

        // When
        CreateUserRequest result = objectMapper.readValue(json, CreateUserRequest.class);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword());
        assertNull(result.getConfirmPassword());
    }

    @Test
    @DisplayName("Deve deserializar JSON vazio")
    void testJsonDeserializationEmptyObject() throws Exception {
        // Given
        String json = "{}";

        // When
        CreateUserRequest result = objectMapper.readValue(json, CreateUserRequest.class);

        // Then
        assertNotNull(result);
        assertNull(result.getUsername());
        assertNull(result.getPassword());
        assertNull(result.getConfirmPassword());
    }

    // ==================== TESTES DE CASOS ESPECIAIS ====================

    @Test
    @DisplayName("Deve aceitar strings com espaços")
    void testStringsWithSpaces() {
        // When
        createUserRequest.setUsername("test user");
        createUserRequest.setPassword("my password");
        createUserRequest.setConfirmPassword("my password");

        // Then
        assertEquals("test user", createUserRequest.getUsername());
        assertEquals("my password", createUserRequest.getPassword());
        assertEquals("my password", createUserRequest.getConfirmPassword());
    }

    @Test
    @DisplayName("Deve aceitar strings com caracteres especiais")
    void testStringsWithSpecialCharacters() {
        // When
        createUserRequest.setUsername("user@test.com");
        createUserRequest.setPassword("p@ssw0rd!");
        createUserRequest.setConfirmPassword("p@ssw0rd!");

        // Then
        assertEquals("user@test.com", createUserRequest.getUsername());
        assertEquals("p@ssw0rd!", createUserRequest.getPassword());
        assertEquals("p@ssw0rd!", createUserRequest.getConfirmPassword());
    }

    @Test
    @DisplayName("Deve aceitar strings longas")
    void testLongStrings() {
        // Given
        String longString = "a".repeat(255);

        // When
        createUserRequest.setUsername(longString);
        createUserRequest.setPassword(longString);
        createUserRequest.setConfirmPassword(longString);

        // Then
        assertEquals(longString, createUserRequest.getUsername());
        assertEquals(longString, createUserRequest.getPassword());
        assertEquals(longString, createUserRequest.getConfirmPassword());
    }

    // ==================== TESTE DE ESTADO INICIAL ====================

    @Test
    @DisplayName("Deve ter valores nulos por padrão")
    void testDefaultValues() {
        // Given
        CreateUserRequest newRequest = new CreateUserRequest();

        // Then
        assertNull(newRequest.getUsername());
        assertNull(newRequest.getPassword());
        assertNull(newRequest.getConfirmPassword());
    }

    // ==================== TESTE DE CENÁRIO COMPLETO ====================

    @Test
    @DisplayName("Deve criar requisição completa e válida")
    void testCompleteValidRequest() {
        // When
        createUserRequest.setUsername("validuser");
        createUserRequest.setPassword("validPassword123");
        createUserRequest.setConfirmPassword("validPassword123");

        // Then
        assertEquals("validuser", createUserRequest.getUsername());
        assertEquals("validPassword123", createUserRequest.getPassword());
        assertEquals("validPassword123", createUserRequest.getConfirmPassword());

        // Verificar que as senhas coincidem (lógica de negócio comum)
        assertEquals(createUserRequest.getPassword(), createUserRequest.getConfirmPassword());
    }

    @Test
    @DisplayName("Deve permitir senhas diferentes")
    void testDifferentPasswords() {
        // When
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("password1");
        createUserRequest.setConfirmPassword("password2");

        // Then
        assertEquals("testuser", createUserRequest.getUsername());
        assertEquals("password1", createUserRequest.getPassword());
        assertEquals("password2", createUserRequest.getConfirmPassword());

        // Verificar que as senhas são diferentes
        assertNotEquals(createUserRequest.getPassword(), createUserRequest.getConfirmPassword());
    }
}