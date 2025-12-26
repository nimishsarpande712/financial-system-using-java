package com.jpmorgan.transaction.controller;

import com.jpmorgan.transaction.dto.UserBalanceResponse;
import com.jpmorgan.transaction.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetUserBalance() throws Exception {
        UserBalanceResponse response = UserBalanceResponse.builder()
            .userId(1L)
            .username("testuser")
            .email("test@example.com")
            .balance(new BigDecimal("1000.00"))
            .build();

        when(userService.getUserBalance(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1/balance"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserBalanceResponse user1 = UserBalanceResponse.builder()
            .userId(1L)
            .username("user1")
            .email("user1@example.com")
            .balance(new BigDecimal("1000.00"))
            .build();

        UserBalanceResponse user2 = UserBalanceResponse.builder()
            .userId(2L)
            .username("user2")
            .email("user2@example.com")
            .balance(new BigDecimal("2000.00"))
            .build();

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(1))
            .andExpect(jsonPath("$[1].userId").value(2));
    }
}