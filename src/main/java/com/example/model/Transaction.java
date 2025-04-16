package com.example.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 * 用于表示系统中的交易信息，包含交易金额、类型、描述等属性
 * 使用 @Data 注解自动生成 getter、setter、equals、hashCode 和 toString 方法
 */
@Data
public class Transaction {
    /**
     * 交易记录唯一标识符
     * 不能为空
     */
    @NotNull(message = "Id cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Id must be alphanumeric with hyphens only")
    private String id;
    
    /**
     * 交易金额
     * 必须为正数且不能为空
     */
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
    private BigDecimal amount;
    
    /**
     * 交易类型
     * 不能为空
     */
    @NotNull(message = "Type cannot be null")
    private TransactionType type;
    
    /**
     * 交易描述
     * 可选字段，用于记录额外信息
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * 交易发生时间
     * 在创建交易记录时自动设置为当前时间
     */
    private LocalDateTime timestamp;

    /**
     * 交易分类
     * 可选字段，用于对交易进行分类
     */
    private String category;

    /**
     * 默认构造函数
     * 初始化交易时间戳（使用当前时间）
     */
    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }
}