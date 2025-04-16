package com.example.controller;

import com.example.exception.TransactionNotFoundException;
import com.example.model.Transaction;
import com.example.model.TransactionType;
import com.example.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TransactionController的单元测试类
 * 使用@WebMvcTest进行API测试，模拟HTTP请求和响应
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction testTransaction;

    /**
     * 测试前初始化
     * 创建测试用的交易记录数据
     */
    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId("test-id");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setType(TransactionType.CREDIT);
        testTransaction.setDescription("Test transaction");
    }

    /**
     * 测试创建交易记录接口
     * 验证：
     * 1. 返回状态码为201 Created
     * 2. 响应体包含正确的交易记录信息
     */
    @Test
    void createTransaction_ShouldReturnCreatedTransaction() throws Exception {
        when(transactionService.createTransaction(any(Transaction.class)))
                .thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.type").value("CREDIT"));
    }

    /**
     * 测试创建交易记录时的参数验证
     * 验证：当提供无效数据时返回400 Bad Request
     */
    @Test
    void createTransaction_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        testTransaction.setAmount(null); // 设置无效数据

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试更新交易记录接口
     * 验证：成功更新后返回200 OK和更新后的数据
     */
    @Test
    void updateTransaction_ShouldReturnUpdatedTransaction() throws Exception {
        when(transactionService.updateTransaction(eq("test-id"), any(Transaction.class)))
                .thenReturn(testTransaction);

        mockMvc.perform(put("/api/transactions/test-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"));
    }

    /**
     * 测试更新不存在的交易记录
     * 验证：返回404 Not Found状态码
     */
    @Test
    void updateTransaction_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        when(transactionService.updateTransaction(eq("non-existent-id"), any(Transaction.class)))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        mockMvc.perform(put("/api/transactions/non-existent-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isNotFound());
    }

    /**
     * 测试删除交易记录接口
     * 验证：成功删除后返回204 No Content
     */
    @Test
    void deleteTransaction_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/transactions/test-id"))
                .andExpect(status().isNoContent());
    }

    /**
     * 测试删除不存在的交易记录
     * 验证：返回404 Not Found状态码
     */
    @Test
    void deleteTransaction_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        doThrow(new TransactionNotFoundException("Transaction not found"))
                .when(transactionService).deleteTransaction("non-existent-id");

        mockMvc.perform(delete("/api/transactions/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    /**
     * 测试获取所有交易记录接口
     * 验证：
     * 1. 返回状态码为200 OK
     * 2. 响应体包含正确的交易记录列表
     */
    @Test
    void getAllTransactions_ShouldReturnTransactionList() throws Exception {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("test-id"))
                .andExpect(jsonPath("$[0].amount").value(100.00));
    }

    /**
     * 测试获取单个交易记录接口
     * 验证：返回指定ID的交易记录
     */
    @Test
    void getTransaction_ShouldReturnTransaction() throws Exception {
        when(transactionService.getTransaction("test-id")).thenReturn(testTransaction);

        mockMvc.perform(get("/api/transactions/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"));
    }

    /**
     * 测试获取不存在的交易记录
     * 验证：返回404 Not Found状态码
     */
    @Test
    void getTransaction_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        when(transactionService.getTransaction("non-existent-id"))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        mockMvc.perform(get("/api/transactions/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    /**
     * 测试全局异常处理
     * 验证：
     * 1. 返回500 Internal Server Error状态码
     * 2. 响应体包含错误信息
     */
    @Test
    void handleGeneralException_ShouldReturnInternalServerError() throws Exception {
        when(transactionService.getTransaction(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/transactions/test-id"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("服务器内部错误")));
    }
}