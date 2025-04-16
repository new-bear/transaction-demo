package com.example.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.exception.DuplicateTransactionException;
import com.example.exception.TransactionNotFoundException;
import com.example.model.Transaction;

/**
 * 交易记录服务类
 * 提供交易记录的业务逻辑处理，包括创建、更新、删除和查询操作
 * 使用 ConcurrentHashMap 实现线程安全的数据存储
 * 集成 Spring Cache 实现数据缓存
 */
@Service
public class TransactionService {
    /**
     * 交易记录存储
     * 使用 ConcurrentHashMap 确保线程安全
     */
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    /**
     * 创建新的交易记录
     * @param transaction 要创建的交易记录
     * @return 创建成功的交易记录
     * @throws DuplicateTransactionException 当交易ID已存在时抛出
     */
    public Transaction createTransaction(Transaction transaction) {
        final String id = transaction.getId();
        // 使用putIfAbsent实现原子操作，避免重复检查
        Transaction existing = transactions.putIfAbsent(id, transaction);
        if (existing != null) {
            throw new DuplicateTransactionException("Transaction ID already exists");
        }

        return transaction;
    }

    /**
     * 更新指定ID的交易记录
     * 更新操作会清除交易记录缓存
     * @param id 要更新的交易记录ID
     * @param transaction 更新的交易记录信息
     * @return 更新后的交易记录
     * @throws TransactionNotFoundException 当指定ID的交易记录不存在时抛出
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public Transaction updateTransaction(String id, Transaction transaction) {
        transaction.setId(id);
        // 使用computeIfPresent实现原子更新，避免重复查询
        Transaction updated = transactions.computeIfPresent(id, (k, v) -> transaction);
        if (updated == null) {
            throw new TransactionNotFoundException("Transaction not found with id: " + id);
        }

        return updated;
    }

    /**
     * 删除指定ID的交易记录
     * 删除操作会清除交易记录缓存
     * @param id 要删除的交易记录ID
     * @throws TransactionNotFoundException 当指定ID的交易记录不存在时抛出
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(String id) {
        if (!transactions.containsKey(id)) {
            throw new TransactionNotFoundException("Transaction not found with id: " + id);
        }

        transactions.remove(id);
    }

    /**
     * 获取所有交易记录
     * 结果会被缓存，提高查询性能
     * @return 所有交易记录的列表
     */
    @Cacheable("transactions")
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions.values());
    }

    /**
     * 获取指定ID的交易记录
     * @param id 交易记录ID
     * @return 指定ID的交易记录
     * @throws TransactionNotFoundException 当指定ID的交易记录不存在时抛出
     */
    public Transaction getTransaction(String id) {
        Transaction transaction = transactions.get(id);
        if (transaction == null) {
            throw new TransactionNotFoundException("Transaction not found with id: " + id);
        }

        return transaction;
    }
}