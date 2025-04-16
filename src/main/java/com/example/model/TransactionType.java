package com.example.model;

/**
 * 交易类型枚举
 * 用于定义交易的借贷方向
 */
public enum TransactionType {
    /**
     * 借记/支出
     * 表示资金流出的交易
     */
    DEBIT,
    
    /**
     * 贷记/收入
     * 表示资金流入的交易
     */
    CREDIT
}