package com.example.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.example.model.Transaction;
import com.example.service.TransactionService;
import com.example.exception.TransactionNotFoundException;

/**
 * 交易记录控制器
 * 提供交易记录的 REST API，包括创建、查询、更新和删除操作
 * 
 * @RestController 标识这是一个 REST 控制器
 * @RequestMapping 所有接口都以 /api/transactions 为基础路径
 * @RequiredArgsConstructor 自动注入所需的依赖
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    /**
     * 创建新的交易记录
     * @param transaction 要创建的交易记录对象
     * @return 创建成功的交易记录及 201 Created 状态码
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.createTransaction(transaction), HttpStatus.CREATED);
    }

    /**
     * 更新指定 ID 的交易记录
     * @param id 要更新的交易记录 ID
     * @param transaction 更新的交易记录信息
     * @return 更新后的交易记录
     * @throws TransactionNotFoundException 当指定 ID 的交易记录不存在时抛出
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id,
            @Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
    }

    /**
     * 删除指定 ID 的交易记录
     * @param id 要删除的交易记录 ID
     * @return 204 No Content 状态码
     * @throws TransactionNotFoundException 当指定 ID 的交易记录不存在时抛出
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取所有交易记录
     * @return 交易记录列表
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    /**
     * 获取指定 ID 的交易记录
     * @param id 交易记录 ID
     * @return 指定 ID 的交易记录
     * @throws TransactionNotFoundException 当指定 ID 的交易记录不存在时抛出
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String id) {
        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

    /**
     * 处理交易记录未找到异常
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(TransactionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "参数校验失败");
        response.put("details", errors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // 日志记录
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("服务器内部错误: " + ex.getMessage());
    }
}