package com.example.exception;

/**
 * 重复交易异常
 * 当尝试创建已存在的交易记录时抛出此异常
 */
public class DuplicateTransactionException extends RuntimeException {
    /**
     * 构造重复交易异常
     * @param message 异常信息
     */
    public DuplicateTransactionException(String message) {
        super(message);
    }
}