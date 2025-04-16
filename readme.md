# 交易处理服务 (Transaction Processing Service)

一个基于Spring Boot的高性能交易处理服务，支持并发交易处理和数据缓存，提供REST API接口进行交易记录的管理。

## 功能特性

- 支持交易记录的创建、查询、更新和删除操作
- 基于ConcurrentHashMap实现线程安全的数据存储
- 集成Spring Cache实现数据缓存，提升查询性能
- 完整的异常处理机制
- 支持并发请求处理
- 包含全面的单元测试

## 技术架构

- **Spring Boot**: 应用框架
- **Spring Cache**: 数据缓存
- **ConcurrentHashMap**: 线程安全的数据存储
- **JUnit 5**: 单元测试框架
- **Maven**: 项目管理工具

## API接口

### 创建交易记录
```http
POST /api/transactions
Content-Type: application/json

{
    "amount": "100.00",
    "type": "CREDIT",
    "description": "交易描述"
}
```

### 更新交易记录
```http
PUT /api/transactions/{id}
Content-Type: application/json

{
    "amount": "200.00",
    "type": "DEBIT",
    "description": "更新的交易描述"
}
```

### 删除交易记录
```http
DELETE /api/transactions/{id}
```

### 查询交易记录
```http
GET /api/transactions/{id}
```

### 获取所有交易记录
```http
GET /api/transactions
```

## 快速开始

### 环境要求
- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 构建和运行

1. 克隆项目
```bash
git clone [项目地址]
cd transaction-demo
```

2. 构建项目
```bash
mvn clean package
```

3. 运行应用
```bash
java -jar target/transaction-demo-1.0-SNAPSHOT.jar
```

### Docker部署

1. 构建Docker镜像
```bash
docker build -t transaction-demo .
```

2. 运行容器
```bash
docker run -p 8080:8080 transaction-demo
```

## 外部依赖库

### Spring Boot 相关依赖
- **spring-boot-starter-web (3.2.0)**
  - 提供Web应用开发支持
  - 包含Spring MVC、内嵌Tomcat服务器等组件
  - 用于构建RESTful API接口

- **spring-boot-starter-validation (3.2.0)**
  - 提供数据验证功能
  - 用于请求参数的校验

- **spring-boot-starter-cache (3.2.0)**
  - 提供缓存支持
  - 集成Spring Cache抽象
  - 用于提升应用性能

- **spring-boot-starter-test**
  - 集成JUnit 5测试框架
  - 包含MockMvc用于API测试
  - 提供断言库和Mock工具

### 其它依赖
- **lombok**
  - 通过注解简化Java代码
  - 自动生成getter/setter、构造器等
  - 减少样板代码，提高开发效率


## 测试

项目包含全面的单元测试，涵盖了所有核心功能和并发场景：

- 基本的CRUD操作测试
- 异常处理测试
- 并发处理测试

运行测试：
```bash
mvn test
```

## 性能特性

- 使用ConcurrentHashMap确保线程安全的并发操作
- Spring Cache集成提供数据缓存，提升查询性能
- 支持高并发场景下的事务处理
- 完善的异常处理机制确保系统稳定性

