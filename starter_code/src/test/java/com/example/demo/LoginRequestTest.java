package com.example.demo;

import com.example.demo.model.requests.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private LoginRequest loginRequest;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        objectMapper = new ObjectMapper();
    }

    // ==================== TESTES DE CONSTRUTORES ====================

    @Test
    @DisplayName("Deve criar instância com construtor padrão")
    void testDefaultConstructor() {
        // Given & When
        LoginRequest request = new LoginRequest();

        // Then
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    // ==================== TESTES DE GETTERS E SETTERS ====================

    @Test
    @DisplayName("Deve definir e obter username corretamente")
    void testUsernameGetterSetter() {
        // Given
        String expectedUsername = "testuser";

        // When
        loginRequest.setUsername(expectedUsername);

        // Then
        assertEquals(expectedUsername, loginRequest.getUsername());
    }

    @Test
    @DisplayName("Deve definir e obter password corretamente")
    void testPasswordGetterSetter() {
        // Given
        String expectedPassword = "password123";

        // When
        loginRequest.setPassword(expectedPassword);

        // Then
        assertEquals(expectedPassword, loginRequest.getPassword());
    }

    // ==================== TESTES DE VALORES NULOS ====================

    @Test
    @DisplayName("Deve aceitar username nulo")
    void testNullUsername() {
        // When
        loginRequest.setUsername(null);

        // Then
        assertNull(loginRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar password nulo")
    void testNullPassword() {
        // When
        loginRequest.setPassword(null);

        // Then
        assertNull(loginRequest.getPassword());
    }

    // ==================== TESTES DE VALORES VAZIOS ====================

    @Test
    @DisplayName("Deve aceitar username vazio")
    void testEmptyUsername() {
        // When
        loginRequest.setUsername("");

        // Then
        assertEquals("", loginRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar password vazio")
    void testEmptyPassword() {
        // When
        loginRequest.setPassword("");

        // Then
        assertEquals("", loginRequest.getPassword());
    }

    // ==================== TESTES DE SERIALIZAÇÃO JSON ====================

    @Test
    @DisplayName("Deve serializar para JSON corretamente")
    void testJsonSerialization() throws Exception {
        // Given
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // When
        String json = objectMapper.writeValueAsString(loginRequest);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"username\":\"testuser\""));
        assertTrue(json.contains("\"password\":\"password123\""));
    }

    @Test
    @DisplayName("Deve deserializar de JSON corretamente")
    void testJsonDeserialization() throws Exception {
        // Given
        String json = "{\"username\":\"testuser\",\"password\":\"password123\"}";

        // When
        LoginRequest result = objectMapper.readValue(json, LoginRequest.class);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
    }

    @Test
    @DisplayName("Deve deserializar JSON com campos ausentes")
    void testJsonDeserializationWithMissingFields() throws Exception {
        // Given
        String json = "{\"username\":\"testuser\"}";

        // When
        LoginRequest result = objectMapper.readValue(json, LoginRequest.class);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("Deve deserializar JSON vazio")
    void testJsonDeserializationEmptyObject() throws Exception {
        // Given
        String json = "{}";

        // When
        LoginRequest result = objectMapper.readValue(json, LoginRequest.class);

        // Then
        assertNotNull(result);
        assertNull(result.getUsername());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("Deve deserializar JSON com valores nulos")
    void testJsonDeserializationWithNullValues() throws Exception {
        // Given
        String json = "{\"username\":null,\"password\":null}";

        // When
        LoginRequest result = objectMapper.readValue(json, LoginRequest.class);

        // Then
        assertNotNull(result);
        assertNull(result.getUsername());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("Deve serializar valores nulos como null")
    void testJsonSerializationWithNullValues() throws Exception {
        // Given
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);

        // When
        String json = objectMapper.writeValueAsString(loginRequest);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"username\":null"));
        assertTrue(json.contains("\"password\":null"));
    }

    // ==================== TESTES DE CASOS ESPECIAIS ====================

    @Test
    @DisplayName("Deve aceitar username com espaços")
    void testUsernameWithSpaces() {
        // When
        loginRequest.setUsername("test user");

        // Then
        assertEquals("test user", loginRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar password com espaços")
    void testPasswordWithSpaces() {
        // When
        loginRequest.setPassword("my password");

        // Then
        assertEquals("my password", loginRequest.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar username com caracteres especiais")
    void testUsernameWithSpecialCharacters() {
        // When
        loginRequest.setUsername("user@test.com");

        // Then
        assertEquals("user@test.com", loginRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar password com caracteres especiais")
    void testPasswordWithSpecialCharacters() {
        // When
        loginRequest.setPassword("p@ssw0rd!");

        // Then
        assertEquals("p@ssw0rd!", loginRequest.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar strings longas")
    void testLongStrings() {
        // Given
        String longUsername = "a".repeat(100);
        String longPassword = "b".repeat(200);

        // When
        loginRequest.setUsername(longUsername);
        loginRequest.setPassword(longPassword);

        // Then
        assertEquals(longUsername, loginRequest.getUsername());
        assertEquals(longPassword, loginRequest.getPassword());
    }

    @Test
    @DisplayName("Deve aceitar strings com caracteres Unicode")
    void testUnicodeCharacters() {
        // When
        loginRequest.setUsername("usuário");
        loginRequest.setPassword("señä123");

        // Then
        assertEquals("usuário", loginRequest.getUsername());
        assertEquals("señä123", loginRequest.getPassword());
    }

    // ==================== TESTES DE CENÁRIOS DE USO ====================

    @Test
    @DisplayName("Deve criar requisição de login válida")
    void testValidLoginRequest() {
        // When
        loginRequest.setUsername("validuser");
        loginRequest.setPassword("validPassword123");

        // Then
        assertEquals("validuser", loginRequest.getUsername());
        assertEquals("validPassword123", loginRequest.getPassword());
        assertNotNull(loginRequest.getUsername());
        assertNotNull(loginRequest.getPassword());
        assertFalse(loginRequest.getUsername().trim().isEmpty());
        assertFalse(loginRequest.getPassword().trim().isEmpty());
    }

    @Test
    @DisplayName("Deve criar requisição de login com email como username")
    void testEmailAsUsername() {
        // When
        loginRequest.setUsername("user@example.com");
        loginRequest.setPassword("password123");

        // Then
        assertEquals("user@example.com", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
        assertTrue(loginRequest.getUsername().contains("@"));
    }

    @Test
    @DisplayName("Deve aceitar password com apenas números")
    void testNumericPassword() {
        // When
        loginRequest.setUsername("user123");
        loginRequest.setPassword("123456");

        // Then
        assertEquals("user123", loginRequest.getUsername());
        assertEquals("123456", loginRequest.getPassword());
    }

    // ==================== TESTE DE INTEGRIDADE DOS DADOS ====================

    @Test
    @DisplayName("Deve manter integridade dos dados em serialização completa")
    void testSerializationRoundTrip() throws Exception {
        // Given
        LoginRequest original = new LoginRequest();
        original.setUsername("roundtripuser");
        original.setPassword("roundtrippassword");

        // When
        String json = objectMapper.writeValueAsString(original);
        LoginRequest deserialized = objectMapper.readValue(json, LoginRequest.class);

        // Then
        assertEquals(original.getUsername(), deserialized.getUsername());
        assertEquals(original.getPassword(), deserialized.getPassword());
    }

    @Test
    @DisplayName("Deve manter integridade com valores nulos")
    void testSerializationRoundTripWithNulls() throws Exception {
        // Given
        LoginRequest original = new LoginRequest();
        original.setUsername(null);
        original.setPassword(null);

        // When
        String json = objectMapper.writeValueAsString(original);
        LoginRequest deserialized = objectMapper.readValue(json, LoginRequest.class);

        // Then
        assertEquals(original.getUsername(), deserialized.getUsername());
        assertEquals(original.getPassword(), deserialized.getPassword());
        assertNull(deserialized.getUsername());
        assertNull(deserialized.getPassword());
    }

    @Test
    @DisplayName("Deve manter integridade com strings vazias")
    void testSerializationRoundTripWithEmptyStrings() throws Exception {
        // Given
        LoginRequest original = new LoginRequest();
        original.setUsername("");
        original.setPassword("");

        // When
        String json = objectMapper.writeValueAsString(original);
        LoginRequest deserialized = objectMapper.readValue(json, LoginRequest.class);

        // Then
        assertEquals(original.getUsername(), deserialized.getUsername());
        assertEquals(original.getPassword(), deserialized.getPassword());
        assertEquals("", deserialized.getUsername());
        assertEquals("", deserialized.getPassword());
    }

    // ==================== TESTES DE MUTAÇÃO DE ESTADO ====================

    @Test
    @DisplayName("Deve permitir modificação dos campos após criação")
    void testFieldModification() {
        // Given
        loginRequest.setUsername("initialUser");
        loginRequest.setPassword("initialPassword");

        // When
        loginRequest.setUsername("modifiedUser");
        loginRequest.setPassword("modifiedPassword");

        // Then
        assertEquals("modifiedUser", loginRequest.getUsername());
        assertEquals("modifiedPassword", loginRequest.getPassword());
    }

    @Test
    @DisplayName("Deve permitir definir campos como nulo após terem valores")
    void testSetToNullAfterValue() {
        // Given
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpassword");

        // When
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);

        // Then
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }
}