package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Cart Entity Tests")
public class CartTest {

    private Cart cart;

    @Mock
    private Item mockItem1;

    @Mock
    private Item mockItem2;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        cart = new Cart();

        // Configurar mocks para os itens
        when(mockItem1.getPrice()).thenReturn(new BigDecimal("10.99"));
        when(mockItem2.getPrice()).thenReturn(new BigDecimal("5.50"));
    }

    // Testes para Getters e Setters
    @Test
    @DisplayName("Deve definir e obter ID corretamente")
    void testSetAndGetId() {
        Long expectedId = 1L;
        cart.setId(expectedId);
        assertEquals(expectedId, cart.getId());
    }

    @Test
    @DisplayName("Deve adicionar item quando lista já existe")
    void testAddItem_WhenItemsListExists() {
        // Given
        List<Item> existingItems = new ArrayList<>();
        existingItems.add(mockItem2);
        cart.setItems(existingItems);

        // When
        cart.addItem(mockItem1);

        // Then
        assertEquals(2, cart.getItems().size());
        assertTrue(cart.getItems().contains(mockItem1));
        assertTrue(cart.getItems().contains(mockItem2));
    }

    @Test
    @DisplayName("Deve inicializar total como zero quando total é null")
    void testAddItem_WhenTotalIsNull() {
        // Given
        assertNull(cart.getTotal());

        // When
        cart.addItem(mockItem1);

        // Then
        assertEquals(new BigDecimal("10.99"), cart.getTotal());
    }

    @Test
    @DisplayName("Deve somar preço do item ao total existente")
    void testAddItem_WhenTotalExists() {
        // Given
        cart.setTotal(new BigDecimal("15.00"));

        // When
        cart.addItem(mockItem1);

        // Then
        assertEquals(new BigDecimal("25.99"), cart.getTotal());
    }

    @Test
    @DisplayName("Deve adicionar múltiplos itens e calcular total corretamente")
    void testAddItem_MultipleItems() {
        // When
        cart.addItem(mockItem1);
        cart.addItem(mockItem2);

        // Then
        assertEquals(2, cart.getItems().size());
        assertEquals(new BigDecimal("16.49"), cart.getTotal());
    }

    // Testes para removeItem()
    @Test
    @DisplayName("Deve remover item da lista existente")
    void testRemoveItem_WhenItemExists() {
        // Given
        cart.addItem(mockItem1);
        cart.addItem(mockItem2);

        // When
        cart.removeItem(mockItem1);

        // Then
        assertEquals(1, cart.getItems().size());
        assertFalse(cart.getItems().contains(mockItem1));
        assertTrue(cart.getItems().contains(mockItem2));
        assertEquals(new BigDecimal("5.50"), cart.getTotal());
    }

    @Test
    @DisplayName("Deve inicializar lista vazia quando lista é null")
    void testRemoveItem_WhenItemsListIsNull() {
        // Given
        assertNull(cart.getItems());

        // When
        cart.removeItem(mockItem1);

        // Then
        assertNotNull(cart.getItems());
        assertEquals(0, cart.getItems().size());
    }


    @Test
    @DisplayName("Deve remover item mesmo quando não existe na lista")
    void testRemoveItem_WhenItemNotInList() {
        // Given
        cart.addItem(mockItem2);
        BigDecimal originalTotal = cart.getTotal();

        // When
        cart.removeItem(mockItem1);

        // Then
        assertEquals(1, cart.getItems().size());
        assertTrue(cart.getItems().contains(mockItem2));
        assertEquals(originalTotal.subtract(mockItem1.getPrice()), cart.getTotal());
    }

    // Testes de cenários de borda
    @Test
    @DisplayName("Deve funcionar com preços zero")
    void testAddAndRemoveItem_WithZeroPrice() {
        // Given
        Item zeroItem = mock(Item.class);
        when(zeroItem.getPrice()).thenReturn(BigDecimal.ZERO);

        // When
        cart.addItem(zeroItem);

        // Then
        assertEquals(BigDecimal.ZERO, cart.getTotal());

        // When
        cart.removeItem(zeroItem);

        // Then
        assertEquals(BigDecimal.ZERO, cart.getTotal());
    }

    @Test
    @DisplayName("Deve funcionar com preços negativos")
    void testAddAndRemoveItem_WithNegativePrice() {
        // Given
        Item negativeItem = mock(Item.class);
        when(negativeItem.getPrice()).thenReturn(new BigDecimal("-5.00"));

        // When
        cart.addItem(negativeItem);

        // Then
        assertEquals(new BigDecimal("-5.00"), cart.getTotal());

        // When
        cart.removeItem(negativeItem);

        // Then
        assertEquals(BigDecimal.ZERO, cart.getTotal());
    }

    @Test
    @DisplayName("Deve manter precisão decimal correta")
    void testDecimalPrecision() {
        // Given
        Item preciseItem = mock(Item.class);
        when(preciseItem.getPrice()).thenReturn(new BigDecimal("9.999"));

        // When
        cart.addItem(preciseItem);
        cart.addItem(preciseItem);

        // Then
        assertEquals(new BigDecimal("19.998"), cart.getTotal());
    }

    // Testes de estado inicial
    @Test
    @DisplayName("Deve ter estado inicial correto")
    void testInitialState() {
        Cart newCart = new Cart();

        assertNull(newCart.getId());
        assertNull(newCart.getItems());
        assertNull(newCart.getUser());
        assertNull(newCart.getTotal());
    }

    // Teste de integração completo
    @Test
    @DisplayName("Deve funcionar em cenário completo de uso")
    void testCompleteScenario() {
        // Given
        Item item1 = mock(Item.class);
        Item item2 = mock(Item.class);
        Item item3 = mock(Item.class);

        when(item1.getPrice()).thenReturn(new BigDecimal("10.00"));
        when(item2.getPrice()).thenReturn(new BigDecimal("15.50"));
        when(item3.getPrice()).thenReturn(new BigDecimal("7.25"));

        // When - Adicionar itens
        cart.addItem(item1);
        cart.addItem(item2);
        cart.addItem(item3);

        // Then - Verificar estado após adições
        assertEquals(3, cart.getItems().size());
        assertEquals(new BigDecimal("32.75"), cart.getTotal());

        // When - Remover um item
        cart.removeItem(item2);

        // Then - Verificar estado após remoção
        assertEquals(2, cart.getItems().size());
        assertEquals(new BigDecimal("17.25"), cart.getTotal());
        assertFalse(cart.getItems().contains(item2));
    }
}