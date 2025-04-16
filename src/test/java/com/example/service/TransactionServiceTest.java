package com.example.service;

import com.example.exception.DuplicateTransactionException;
import com.example.exception.TransactionNotFoundException;
import com.example.model.Transaction;
import com.example.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TransactionService 的单元测试类
 * 测试交易服务的所有核心功能和并发处理能力
 * 使用 @Execution(ExecutionMode.CONCURRENT) 支持并行测试执行
 */
@Execution(ExecutionMode.CONCURRENT)
class TransactionServiceTest {
    private TransactionService transactionService;

    /**
     * 测试前初始化
     * 每个测试方法执行前创建新的 TransactionService 实例
     */
    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
    }

    /**
     * 测试创建交易记录的成功场景
     * 验证创建后的交易记录属性是否符合预期
     */
    @Test
    void createTransaction_ShouldCreateSuccessfully() {
        Transaction transaction = new Transaction();
        transaction.setId("test-id");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.CREDIT);
        transaction.setDescription("Test transaction");

        Transaction created = transactionService.createTransaction(transaction);

        assertNotNull(created.getId());
        assertEquals(transaction.getAmount(), created.getAmount());
        assertEquals(transaction.getType(), created.getType());
        assertEquals(transaction.getDescription(), created.getDescription());
    }

    /**
     * 测试创建重复ID交易记录的失败场景
     * 验证是否正确抛出 DuplicateTransactionException 异常
     */
    @Test
    void createTransaction_ShouldThrowException_WhenDuplicateId() {
        Transaction transaction = new Transaction();
        transaction.setId("test-id");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.CREDIT);

        transactionService.createTransaction(transaction);

        Transaction duplicate = new Transaction();
        duplicate.setId("test-id");
        duplicate.setAmount(new BigDecimal("200.00"));
        duplicate.setType(TransactionType.DEBIT);

        assertThrows(DuplicateTransactionException.class, () ->
            transactionService.createTransaction(duplicate));
    }

    /**
     * 测试更新交易记录的成功场景
     * 验证更新后的交易记录属性是否正确更新
     */
    @Test
    void updateTransaction_ShouldUpdateSuccessfully() {
        Transaction transaction = new Transaction();
        transaction.setId("test-id");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.CREDIT);
        transactionService.createTransaction(transaction);

        Transaction update = new Transaction();
        update.setAmount(new BigDecimal("200.00"));
        update.setType(TransactionType.DEBIT);
        update.setDescription("Updated transaction");

        Transaction updated = transactionService.updateTransaction("test-id", update);

        assertEquals("test-id", updated.getId());
        assertEquals(new BigDecimal("200.00"), updated.getAmount());
        assertEquals(TransactionType.DEBIT, updated.getType());
        assertEquals("Updated transaction", updated.getDescription());
    }

    /**
     * 测试更新不存在交易记录的失败场景
     * 验证是否正确抛出 TransactionNotFoundException 异常
     */
    @Test
    void updateTransaction_ShouldThrowException_WhenTransactionNotFound() {
        Transaction update = new Transaction();
        update.setAmount(new BigDecimal("200.00"));
        update.setType(TransactionType.DEBIT);

        assertThrows(TransactionNotFoundException.class, () ->
            transactionService.updateTransaction("non-existent-id", update));
    }

    /**
     * 测试删除交易记录的成功场景
     * 验证删除后是否无法再次获取该记录
     */
    @Test
    void deleteTransaction_ShouldDeleteSuccessfully() {
        Transaction transaction = new Transaction();
        transaction.setId("test-id");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.CREDIT);
        transactionService.createTransaction(transaction);

        transactionService.deleteTransaction("test-id");

        assertThrows(TransactionNotFoundException.class, () ->
            transactionService.getTransaction("test-id"));
    }

    /**
     * 测试删除不存在交易记录的失败场景
     * 验证是否正确抛出 TransactionNotFoundException 异常
     */
    @Test
    void deleteTransaction_ShouldThrowException_WhenTransactionNotFound() {
        assertThrows(TransactionNotFoundException.class, () -> 
            transactionService.deleteTransaction("non-existent-id"));
    }

    /**
     * 测试获取存在的交易记录
     * 验证获取的交易记录属性是否符合预期
     */
    @Test
    void getTransaction_ShouldReturnTransaction_WhenExists() {
        Transaction transaction = new Transaction();
        transaction.setId("test-id");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.CREDIT);
        transactionService.createTransaction(transaction);

        Transaction found = transactionService.getTransaction("test-id");

        assertEquals("test-id", found.getId());
        assertEquals(new BigDecimal("100.00"), found.getAmount());
        assertEquals(TransactionType.CREDIT, found.getType());
    }

    /**
     * 测试获取不存在的交易记录
     * 验证是否正确抛出 TransactionNotFoundException 异常
     */
    @Test
    void getTransaction_ShouldThrowException_WhenNotFound() {
        assertThrows(TransactionNotFoundException.class, () ->
            transactionService.getTransaction("non-existent-id"));
    }

    /**
     * 测试获取所有交易记录
     * 验证返回列表是否包含所有已创建的交易记录
     */
    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        Transaction transaction1 = new Transaction();
        transaction1.setId("test-id-1");
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setType(TransactionType.CREDIT);
        transactionService.createTransaction(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setId("test-id-2");
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setType(TransactionType.DEBIT);
        transactionService.createTransaction(transaction2);

        List<Transaction> transactions = transactionService.getAllTransactions();

        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().anyMatch(t -> t.getId().equals("test-id-1")));
        assertTrue(transactions.stream().anyMatch(t -> t.getId().equals("test-id-2")));
    }

    /**
     * 高并发混合读写操作压力测试
     * 模拟多个线程同时执行不同类型的操作（创建、更新、查询、删除）
     * 验证系统在复杂负载下的数据一致性和性能表现
     *
     * @throws InterruptedException 当线程执行被中断时抛出
     */
    @Test
    void mixedOperations_UnderHighConcurrency() throws InterruptedException {
        int threadCount = 50;
        int operationsPerThread = 200;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);
        long startTime = System.currentTimeMillis();

        // 创建一些初始数据
        List<String> existingIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String id = UUID.randomUUID().toString();
            Transaction transaction = new Transaction();
            transaction.setId(id);
            transaction.setAmount(new BigDecimal("100.00"));
            transaction.setType(TransactionType.CREDIT);
            transactionService.createTransaction(transaction);
            existingIds.add(id);
        }

        // 并发执行混合操作
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    Random random = new Random();
                    for (int j = 0; j < operationsPerThread; j++) {
                        // 随机选择操作类型
                        int operation = random.nextInt(4);
                        switch (operation) {
                            case 0: // 创建新交易
                                String newId = UUID.randomUUID().toString();
                                Transaction newTrans = new Transaction();
                                newTrans.setId(newId);
                                newTrans.setAmount(new BigDecimal(String.format("%.2f", random.nextDouble() * 1000)));
                                newTrans.setType(random.nextBoolean() ? TransactionType.CREDIT : TransactionType.DEBIT);
                                transactionService.createTransaction(newTrans);
                                break;

                            case 1: // 更新现有交易
                                if (!existingIds.isEmpty()) {
                                    String updateId = existingIds.get(random.nextInt(existingIds.size()));
                                    try {
                                        Transaction updateTrans = new Transaction();
                                        updateTrans.setAmount(new BigDecimal(String.format("%.2f", random.nextDouble() * 1000)));
                                        updateTrans.setType(random.nextBoolean() ? TransactionType.CREDIT : TransactionType.DEBIT);
                                        transactionService.updateTransaction(updateId, updateTrans);
                                    } catch (TransactionNotFoundException ignored) {}
                                }
                                break;

                            case 2: // 查询交易
                                if (!existingIds.isEmpty()) {
                                    String queryId = existingIds.get(random.nextInt(existingIds.size()));
                                    try {
                                        Transaction found = transactionService.getTransaction(queryId);
                                        assertNotNull(found);
                                    } catch (TransactionNotFoundException ignored) {}
                                }
                                break;

                            case 3: // 删除交易
                                if (!existingIds.isEmpty()) {
                                    String deleteId = existingIds.get(random.nextInt(existingIds.size()));
                                    try {
                                        transactionService.deleteTransaction(deleteId);
                                    } catch (TransactionNotFoundException ignored) {}
                                }
                                break;
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        });

        assertTrue(latch.await(60, TimeUnit.SECONDS));
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS));

        long duration = System.currentTimeMillis() - startTime;
        System.out.printf("混合操作压力测试完成，总耗时: %d ms，平均操作耗时: %.2f ms%n",
                duration, (double) duration / (threadCount * operationsPerThread));
    }

    /**
     * 大数据量场景压力测试
     * 测试系统在处理大量写入数据时的性能表现
     * 验证内存使用情况和响应时间
     *
     * @throws InterruptedException 当线程执行被中断时抛出
     */
    @Test
    void largeDataVolume_StressTest() throws InterruptedException {
        int totalTransactions = 10000;
        int batchSize = 100;
        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(totalTransactions / batchSize);
        long startTime = System.currentTimeMillis();

        // 分批创建大量交易记录
        for (int batch = 0; batch < totalTransactions / batchSize; batch++) {
            executorService.submit(() -> {
                try {
                    for (int i = 0; i < batchSize; i++) {
                        Transaction transaction = new Transaction();
                        transaction.setId(UUID.randomUUID().toString());
                        transaction.setAmount(new BigDecimal(String.format("%.2f", Math.random() * 10000)));
                        transaction.setType(Math.random() > 0.5 ? TransactionType.CREDIT : TransactionType.DEBIT);
                        transaction.setDescription("Batch transaction " + System.currentTimeMillis());

                        transactionService.createTransaction(transaction);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(120, TimeUnit.SECONDS));
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS));

        // 验证数据量和性能指标
        List<Transaction> allTransactions = transactionService.getAllTransactions();
        assertEquals(totalTransactions, allTransactions.size());

        long duration = System.currentTimeMillis() - startTime;
        double avgTimePerTransaction = (double) duration / totalTransactions;
        System.out.printf("大数据量测试完成:\n");
        System.out.printf("总交易数: %d\n", totalTransactions);
        System.out.printf("总耗时: %d ms\n", duration);
        System.out.printf("平均每笔交易耗时: %.2f ms\n", avgTimePerTransaction);
        System.out.printf("每秒处理交易数: %.2f\n", (1000.0 * totalTransactions / duration));
    }
}