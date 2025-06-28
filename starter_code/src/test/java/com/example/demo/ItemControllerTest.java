package com.example.demo;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class ItemControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ItemRepository itemRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    // ==================== TESTES PARA getItems() ====================

    @Test
    @WithMockUser
    @DisplayName("Listar todos os itens - sucesso")
    void getItems_Success() throws Exception {
        // Given
        List<Item> items = Arrays.asList(
                createTestItem(1L, "Laptop", "High-performance laptop", new BigDecimal("999.99")),
                createTestItem(2L, "Mouse", "Wireless mouse", new BigDecimal("29.99")),
                createTestItem(3L, "Keyboard", "Mechanical keyboard", new BigDecimal("79.99"))
        );

        when(itemRepository.findAll()).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/api/item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(999.99))
                .andExpect(jsonPath("$[0].description").value("High-performance laptop"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Mouse"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Keyboard"));

        verify(itemRepository).findAll();
    }

    @Test
    @WithMockUser
    @DisplayName("Listar todos os itens - lista vazia")
    void getItems_EmptyList() throws Exception {
        // Given
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemRepository).findAll();
    }

    @Test
    @WithMockUser
    @DisplayName("Listar todos os itens - item com preço zero")
    void getItems_WithZeroPrice() throws Exception {
        // Given
        List<Item> items = Arrays.asList(
                createTestItem(1L, "Free Item", "Free sample", BigDecimal.ZERO)
        );

        when(itemRepository.findAll()).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/api/item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].price").value(0));

        verify(itemRepository).findAll();
    }

    // ==================== TESTES PARA getItemById() ====================

    @Test
    @WithMockUser
    @DisplayName("Buscar item por ID - sucesso")
    void getItemById_Success() throws Exception {
        // Given
        Item item = createTestItem(1L, "Laptop", "High-performance laptop", new BigDecimal("999.99"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // When & Then
        mockMvc.perform(get("/api/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.description").value("High-performance laptop"));

        verify(itemRepository).findById(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar item por ID - não encontrado")
    void getItemById_NotFound() throws Exception {
        // Given
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/item/999"))
                .andExpect(status().isNotFound());

        verify(itemRepository).findById(999L);
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar item por ID - ID zero")
    void getItemById_ZeroId() throws Exception {
        // Given
        when(itemRepository.findById(0L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/item/0"))
                .andExpect(status().isNotFound());

        verify(itemRepository).findById(0L);
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar item por ID - ID negativo")
    void getItemById_NegativeId() throws Exception {
        // Given
        when(itemRepository.findById(-1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/item/-1"))
                .andExpect(status().isNotFound());

        verify(itemRepository).findById(-1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar item por ID - ID inválido")
    void getItemById_InvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/item/abc"))
                .andExpect(status().isBadRequest());

        verify(itemRepository, never()).findById(anyLong());
    }

    // ==================== TESTES PARA getItemsByName() ====================

    @Test
    @WithMockUser
    @DisplayName("Buscar itens por nome - sucesso")
    void getItemsByName_Success() throws Exception {
        // Given
        List<Item> items = Arrays.asList(
                createTestItem(1L, "Laptop", "Gaming laptop", new BigDecimal("1299.99")),
                createTestItem(2L, "Laptop", "Business laptop", new BigDecimal("899.99"))
        );

        when(itemRepository.findByName("Laptop")).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/api/item/name/Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Laptop"))
                .andExpect(jsonPath("$[0].description").value("Gaming laptop"))
                .andExpect(jsonPath("$[1].description").value("Business laptop"));

        verify(itemRepository).findByName("Laptop");
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar itens por nome - item único")
    void getItemsByName_SingleItem() throws Exception {
        // Given
        List<Item> items = Arrays.asList(
                createTestItem(1L, "Mouse", "Wireless mouse", new BigDecimal("29.99"))
        );

        when(itemRepository.findByName("Mouse")).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/api/item/name/Mouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Mouse"));

        verify(itemRepository).findByName("Mouse");
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar itens por nome - não encontrado")
    void getItemsByName_NotFound() throws Exception {
        // Given
        when(itemRepository.findByName("NonExistentItem")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/item/name/NonExistentItem"))
                .andExpect(status().isNotFound());

        verify(itemRepository).findByName("NonExistentItem");
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar itens por nome - lista nula")
    void getItemsByName_NullList() throws Exception {
        // Given
        when(itemRepository.findByName("NullItem")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/item/name/NullItem"))
                .andExpect(status().isNotFound());

        verify(itemRepository).findByName("NullItem");
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar itens por nome - nome vazio")
    void getItemsByName_EmptyName() throws Exception {
        // Given
        when(itemRepository.findByName("")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/item/name/"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar itens por nome - nome com espaços")
    void getItemsByName_NameWithSpaces() throws Exception {
        // Given
        List<Item> items = Arrays.asList(
                createTestItem(1L, "Gaming Mouse", "High-DPI gaming mouse", new BigDecimal("59.99"))
        );

        when(itemRepository.findByName("Gaming Mouse")).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/api/item/name/Gaming Mouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Gaming Mouse"));

        verify(itemRepository).findByName("Gaming Mouse");
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar itens por nome - nome com caracteres especiais")
    void getItemsByName_NameWithSpecialCharacters() throws Exception {
        // Given
        List<Item> items = Arrays.asList(
                createTestItem(1L, "USB-C Cable", "High-speed USB-C cable", new BigDecimal("19.99"))
        );

        when(itemRepository.findByName("USB-C Cable")).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/api/item/name/USB-C Cable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("USB-C Cable"));

        verify(itemRepository).findByName("USB-C Cable");
    }

    @Test
    @WithMockUser
    @DisplayName("Buscar itens por nome - case sensitive")
    void getItemsByName_CaseSensitive() throws Exception {
        // Given
        when(itemRepository.findByName("laptop")).thenReturn(Collections.emptyList());
        when(itemRepository.findByName("LAPTOP")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/item/name/laptop"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/item/name/LAPTOP"))
                .andExpect(status().isNotFound());

        verify(itemRepository).findByName("laptop");
        verify(itemRepository).findByName("LAPTOP");
    }

    // ==================== TESTES DE SEGURANÇA ====================

    @Test
    @DisplayName("Acessar endpoint sem autenticação - deve retornar 401")
    void getItems_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/item"))
                .andExpect(status().isUnauthorized());

        verify(itemRepository, never()).findAll();
    }

    @Test
    @DisplayName("Buscar item por ID sem autenticação - deve retornar 401")
    void getItemById_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/item/1"))
                .andExpect(status().isUnauthorized());

        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Buscar itens por nome sem autenticação - deve retornar 401")
    void getItemsByName_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/item/name/Laptop"))
                .andExpect(status().isUnauthorized());

        verify(itemRepository, never()).findByName(anyString());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Item createTestItem(Long id, String name, String description, BigDecimal price) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        return item;
    }
}