package com.example.demo;

import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModifyCartRequestTest {

    private ModifyCartRequest modifyCartRequest;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        modifyCartRequest = new ModifyCartRequest();
        objectMapper = new ObjectMapper();
    }

    // ==================== TESTES DE GETTERS E SETTERS ====================

    @Test
    @DisplayName("Deve definir e obter username corretamente")
    void testUsernameGetterSetter() {
        // Given
        String expectedUsername = "testuser";

        // When
        modifyCartRequest.setUsername(expectedUsername);

        // Then
        assertEquals(expectedUsername, modifyCartRequest.getUsername());
    }

    @Test
    @DisplayName("Deve definir e obter itemId corretamente")
    void testItemIdGetterSetter() {
        // Given
        long expectedItemId = 123L;

        // When
        modifyCartRequest.setItemId(expectedItemId);

        // Then
        assertEquals(expectedItemId, modifyCartRequest.getItemId());
    }

    @Test
    @DisplayName("Deve definir e obter quantity corretamente")
    void testQuantityGetterSetter() {
        // Given
        int expectedQuantity = 5;

        // When
        modifyCartRequest.setQuantity(expectedQuantity);

        // Then
        assertEquals(expectedQuantity, modifyCartRequest.getQuantity());
    }

    // ==================== TESTES DE VALORES PADRÃO ====================

    @Test
    @DisplayName("Deve ter valores padrão corretos")
    void testDefaultValues() {
        // Given
        ModifyCartRequest newRequest = new ModifyCartRequest();

        // Then
        assertNull(newRequest.getUsername());
        assertEquals(0L, newRequest.getItemId());
        assertEquals(0, newRequest.getQuantity());
    }

    // ==================== TESTES DE VALORES ESPECIAIS ====================

    @Test
    @DisplayName("Deve aceitar username nulo")
    void testNullUsername() {
        // When
        modifyCartRequest.setUsername(null);

        // Then
        assertNull(modifyCartRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar username vazio")
    void testEmptyUsername() {
        // When
        modifyCartRequest.setUsername("");

        // Then
        assertEquals("", modifyCartRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar itemId zero")
    void testZeroItemId() {
        // When
        modifyCartRequest.setItemId(0L);

        // Then
        assertEquals(0L, modifyCartRequest.getItemId());
    }

    @Test
    @DisplayName("Deve aceitar itemId negativo")
    void testNegativeItemId() {
        // When
        modifyCartRequest.setItemId(-1L);

        // Then
        assertEquals(-1L, modifyCartRequest.getItemId());
    }

    @Test
    @DisplayName("Deve aceitar quantity zero")
    void testZeroQuantity() {
        // When
        modifyCartRequest.setQuantity(0);

        // Then
        assertEquals(0, modifyCartRequest.getQuantity());
    }

    @Test
    @DisplayName("Deve aceitar quantity negativo")
    void testNegativeQuantity() {
        // When
        modifyCartRequest.setQuantity(-1);

        // Then
        assertEquals(-1, modifyCartRequest.getQuantity());
    }

    @Test
    @DisplayName("Deve aceitar itemId máximo")
    void testMaxItemId() {
        // When
        modifyCartRequest.setItemId(Long.MAX_VALUE);

        // Then
        assertEquals(Long.MAX_VALUE, modifyCartRequest.getItemId());
    }

    @Test
    @DisplayName("Deve aceitar quantity máximo")
    void testMaxQuantity() {
        // When
        modifyCartRequest.setQuantity(Integer.MAX_VALUE);

        // Then
        assertEquals(Integer.MAX_VALUE, modifyCartRequest.getQuantity());
    }

    // ==================== TESTES DE SERIALIZAÇÃO JSON ====================

    @Test
    @DisplayName("Deve serializar para JSON corretamente")
    void testJsonSerialization() throws Exception {
        // Given
        modifyCartRequest.setUsername("testuser");
        modifyCartRequest.setItemId(123L);
        modifyCartRequest.setQuantity(5);

        // When
        String json = objectMapper.writeValueAsString(modifyCartRequest);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"username\":\"testuser\""));
        assertTrue(json.contains("\"itemId\":123"));
        assertTrue(json.contains("\"quantity\":5"));
    }

    @Test
    @DisplayName("Deve deserializar de JSON corretamente")
    void testJsonDeserialization() throws Exception {
        // Given
        String json = "{\"username\":\"testuser\",\"itemId\":123,\"quantity\":5}";

        // When
        ModifyCartRequest result = objectMapper.readValue(json, ModifyCartRequest.class);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(123L, result.getItemId());
        assertEquals(5, result.getQuantity());
    }

    @Test
    @DisplayName("Deve deserializar JSON com campos ausentes")
    void testJsonDeserializationWithMissingFields() throws Exception {
        // Given
        String json = "{\"username\":\"testuser\"}";

        // When
        ModifyCartRequest result = objectMapper.readValue(json, ModifyCartRequest.class);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(0L, result.getItemId());
        assertEquals(0, result.getQuantity());
    }

    @Test
    @DisplayName("Deve deserializar JSON vazio")
    void testJsonDeserializationEmptyObject() throws Exception {
        // Given
        String json = "{}";

        // When
        ModifyCartRequest result = objectMapper.readValue(json, ModifyCartRequest.class);

        // Then
        assertNotNull(result);
        assertNull(result.getUsername());
        assertEquals(0L, result.getItemId());
        assertEquals(0, result.getQuantity());
    }

    @Test
    @DisplayName("Deve deserializar JSON com valores nulos")
    void testJsonDeserializationWithNullValues() throws Exception {
        // Given
        String json = "{\"username\":null,\"itemId\":0,\"quantity\":0}";

        // When
        ModifyCartRequest result = objectMapper.readValue(json, ModifyCartRequest.class);

        // Then
        assertNotNull(result);
        assertNull(result.getUsername());
        assertEquals(0L, result.getItemId());
        assertEquals(0, result.getQuantity());
    }

    @Test
    @DisplayName("Deve serializar valores extremos corretamente")
    void testJsonSerializationExtremeValues() throws Exception {
        // Given
        modifyCartRequest.setUsername("user@test.com");
        modifyCartRequest.setItemId(Long.MAX_VALUE);
        modifyCartRequest.setQuantity(Integer.MAX_VALUE);

        // When
        String json = objectMapper.writeValueAsString(modifyCartRequest);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"username\":\"user@test.com\""));
        assertTrue(json.contains("\"itemId\":" + Long.MAX_VALUE));
        assertTrue(json.contains("\"quantity\":" + Integer.MAX_VALUE));
    }

    // ==================== TESTES DE CASOS ESPECIAIS ====================

    @Test
    @DisplayName("Deve aceitar username com espaços")
    void testUsernameWithSpaces() {
        // When
        modifyCartRequest.setUsername("test user");

        // Then
        assertEquals("test user", modifyCartRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar username com caracteres especiais")
    void testUsernameWithSpecialCharacters() {
        // When
        modifyCartRequest.setUsername("user@test.com");

        // Then
        assertEquals("user@test.com", modifyCartRequest.getUsername());
    }

    @Test
    @DisplayName("Deve aceitar username longo")
    void testLongUsername() {
        // Given
        String longUsername = "a".repeat(255);

        // When
        modifyCartRequest.setUsername(longUsername);

        // Then
        assertEquals(longUsername, modifyCartRequest.getUsername());
    }

    // ==================== TESTE DE CENÁRIO COMPLETO ====================

    @Test
    @DisplayName("Deve criar requisição completa e válida")
    void testCompleteValidRequest() {
        // When
        modifyCartRequest.setUsername("validuser");
        modifyCartRequest.setItemId(456L);
        modifyCartRequest.setQuantity(3);

        // Then
        assertEquals("validuser", modifyCartRequest.getUsername());
        assertEquals(456L, modifyCartRequest.getItemId());
        assertEquals(3, modifyCartRequest.getQuantity());
    }

    @Test
    @DisplayName("Deve criar requisição para adicionar item")
    void testAddItemRequest() {
        // When
        modifyCartRequest.setUsername("shopper");
        modifyCartRequest.setItemId(789L);
        modifyCartRequest.setQuantity(2);

        // Then
        assertEquals("shopper", modifyCartRequest.getUsername());
        assertEquals(789L, modifyCartRequest.getItemId());
        assertEquals(2, modifyCartRequest.getQuantity());
        assertTrue(modifyCartRequest.getQuantity() > 0); // Adicionar itens
    }

    @Test
    @DisplayName("Deve criar requisição para remover item")
    void testRemoveItemRequest() {
        // When
        modifyCartRequest.setUsername("shopper");
        modifyCartRequest.setItemId(789L);
        modifyCartRequest.setQuantity(1);

        // Then
        assertEquals("shopper", modifyCartRequest.getUsername());
        assertEquals(789L, modifyCartRequest.getItemId());
        assertEquals(1, modifyCartRequest.getQuantity());
        assertTrue(modifyCartRequest.getQuantity() > 0); // Remover quantidade específica
    }

    // ==================== TESTE DE INTEGRIDADE DOS DADOS ====================

    @Test
    @DisplayName("Deve manter integridade dos dados em serialização completa")
    void testSerializationRoundTrip() throws Exception {
        // Given
        ModifyCartRequest original = new ModifyCartRequest();
        original.setUsername("roundtripuser");
        original.setItemId(999L);
        original.setQuantity(10);

        // When
        String json = objectMapper.writeValueAsString(original);
        ModifyCartRequest deserialized = objectMapper.readValue(json, ModifyCartRequest.class);

        // Then
        assertEquals(original.getUsername(), deserialized.getUsername());
        assertEquals(original.getItemId(), deserialized.getItemId());
        assertEquals(original.getQuantity(), deserialized.getQuantity());
    }

    @Test
    @DisplayName("Deve manter integridade com valores limite")
    void testSerializationRoundTripLimitValues() throws Exception {
        // Given
        ModifyCartRequest original = new ModifyCartRequest();
        original.setUsername(null);
        original.setItemId(0L);
        original.setQuantity(0);

        // When
        String json = objectMapper.writeValueAsString(original);
        ModifyCartRequest deserialized = objectMapper.readValue(json, ModifyCartRequest.class);

        // Then
        assertEquals(original.getUsername(), deserialized.getUsername());
        assertEquals(original.getItemId(), deserialized.getItemId());
        assertEquals(original.getQuantity(), deserialized.getQuantity());
    }
}