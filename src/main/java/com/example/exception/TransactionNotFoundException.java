package com.example.exception;

/**
 * 交易记录未找到异常
 * 当查询、更新或删除不存在的交易记录时抛出此异常
 */
public class TransactionNotFoundException extends RuntimeException {
    /**
     * 构造交易记录未找到异常
     * @param message 异常信息
     */
    public TransactionNotFoundException(String message) {
        super(message);
    }
}