# 邮箱验证码登录功能使用指南

## 功能概述

本项目已集成邮箱验证码登录/注册功能，支持用户通过邮箱验证码进行身份验证，无需记住密码。

## 邮件配置

### 1. 配置邮件服务器

在 `src/main/resources/application.yml` 中配置邮件服务：

```yaml
spring:
  mail:
    host: smtp.qq.com           # 邮件服务器地址
    port: 587                   # 端口号
    username: ${MAIL_USERNAME}  # 发件人邮箱
    password: ${MAIL_PASSWORD}  # 授权码（不是邮箱密码）
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
    default-encoding: UTF-8

email:
  verification:
    code-length: 6              # 验证码长度
    expiration-minutes: 10      # 验证码有效期（分钟）
    from: ${MAIL_USERNAME}      # 发件人邮箱
```

### 2. 获取邮箱授权码

#### QQ 邮箱配置

1. 登录 QQ 邮箱网页版
2. 进入「设置」→「账户」
3. 开启「POP3/SMTP 服务」
4. 生成授权码（16 位字符串）
5. 将授权码配置到 `MAIL_PASSWORD` 环境变量

#### 其他邮箱

| 邮箱服务商 | SMTP 服务器 | 端口 |
|-----------|------------|------|
| QQ 邮箱 | smtp.qq.com | 587 |
| 163 邮箱 | smtp.163.com | 587 |
| Gmail | smtp.gmail.com | 587 |
| Outlook | smtp.office365.com | 587 |

### 3. 环境变量配置

推荐使用环境变量配置敏感信息：

```bash
# macOS/Linux
export MAIL_USERNAME="your-email@qq.com"
export MAIL_PASSWORD="your-auth-code"

# Windows
set MAIL_USERNAME=your-email@qq.com
set MAIL_PASSWORD=your-auth-code
```

## API 接口

### 1. 发送验证码

**接口**: `POST /api/email/send-code`

**请求体**:
```json
{
  "email": "user@example.com"
}
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "email": "user@example.com",
    "message": "验证码已发送，请查收邮箱",
    "expirationMinutes": 10
  }
}
```

**cURL 示例**:
```bash
curl -X POST http://localhost:8080/api/email/send-code \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com"}'
```

---

### 2. 邮箱验证码注册

**接口**: `POST /api/auth/register/email`

**请求体**:
```json
{
  "email": "user@example.com",
  "code": "123456",
  "username": "newuser",
  "password": "password123",
  "role": "USER"
}
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "username": "newuser",
    "role": "USER"
  }
}
```

**cURL 示例**:
```bash
curl -X POST http://localhost:8080/api/auth/register/email \
  -H "Content-Type: application/json" \
  -d '{
    "email":"user@example.com",
    "code":"123456",
    "username":"newuser",
    "password":"password123"
  }'
```

---

### 3. 邮箱验证码登录

**接口**: `POST /api/auth/login/email`

**请求体**:
```json
{
  "email": "user@example.com",
  "code": "123456"
}
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "username": "existinguser",
    "role": "USER"
  }
}
```

**cURL 示例**:
```bash
curl -X POST http://localhost:8080/api/auth/login/email \
  -H "Content-Type: application/json" \
  -d '{
    "email":"user@example.com",
    "code":"123456"
  }'
```

---

## 使用流程

### 注册流程

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│  1. 输入邮箱  │ ───> │ 2. 发送验证码 │ ───> │ 3. 输入验证码  │ ───> │  4. 完成注册 │
│             │      │             │      │   用户名密码  │      │             │
└─────────────┘      └─────────────┘      └─────────────┘      └─────────────┘
```

### 登录流程

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│  1. 输入邮箱  │ ───> │ 2. 发送验证码 │ ───> │ 3. 输入验证码  │ ───> │  5. 完成登录 │
│             │      │             │      │             │      │             │
└─────────────┘      └─────────────┘      └─────────────┘      └─────────────┘
```

---

## 数据库表结构

### email_verification_codes 表

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | BIGINT | 主键 |
| email | VARCHAR | 邮箱地址（唯一） |
| code | VARCHAR(10) | 验证码 |
| expire_time | DATETIME | 过期时间 |
| used | BOOLEAN | 是否已使用 |
| create_time | DATETIME | 创建时间 |

### users 表（新增 email 字段）

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR | 用户名（唯一） |
| password | VARCHAR | 密码 |
| role | VARCHAR | 角色 |
| email | VARCHAR | 邮箱地址（唯一） |

---

## 安全特性

1. **验证码一次性使用**: 验证码使用后立即失效
2. **验证码有效期**: 默认 10 分钟过期
3. **邮箱唯一性**: 每个邮箱只能注册一个账户
4. **密码加密**: 使用 BCrypt 加密存储
5. **JWT Token**: 登录成功后颁发 JWT Token

---

## 常见问题

### 1. 邮件发送失败

**可能原因**:
- 邮箱账号或授权码配置错误
- 邮件服务器连接问题
- 邮箱未开启 SMTP 服务

**解决方案**:
- 检查 `application.yml` 中的邮件配置
- 确认环境变量 `MAIL_USERNAME` 和 `MAIL_PASSWORD` 已正确设置
- 登录邮箱网页版开启 SMTP 服务

### 2. 验证码收不到

**可能原因**:
- 邮件被归类为垃圾邮件
- 邮箱地址填写错误
- 邮件服务器延迟

**解决方案**:
- 检查垃圾邮件箱
- 确认邮箱地址正确
- 等待 1-2 分钟后重新发送

### 3. 验证码无效

**可能原因**:
- 验证码已过期（超过 10 分钟）
- 验证码已使用
- 输入的验证码有误

**解决方案**:
- 重新发送验证码
- 确保在有效期内使用
- 仔细核对验证码

---

## Swagger UI 测试

启动应用后访问：http://localhost:8080/swagger-ui.html

在 Swagger UI 中可以找到以下接口进行测试：
- `EmailController` - 邮件管理接口
- `AuthController` - 认证管理接口（包含邮箱登录注册）

---

## 代码文件清单

```
src/main/java/com/example/demo/
├── config/
│   └── (邮件配置在 application.yml)
├── controller/
│   ├── AuthController.java        # 认证控制器（已更新）
│   └── EmailController.java       # 邮件控制器（新增）
├── dto/
│   ├── SendCodeRequest.java       # 发送验证码请求（新增）
│   ├── EmailLoginRequest.java     # 邮箱登录请求（新增）
│   └── EmailRegisterRequest.java  # 邮箱注册请求（新增）
├── model/
│   ├── User.java                  # 用户实体（已更新，添加 email 字段）
│   └── EmailVerificationCode.java # 验证码实体（新增）
├── repository/
│   ├── UserRepository.java        # 用户 Repository（已更新）
│   └── EmailVerificationCodeRepository.java # 验证码 Repository（新增）
├── service/
│   ├── EmailService.java          # 邮件服务（新增）
│   ├── UserService.java           # 用户服务接口（已更新）
│   └── impl/
│       └── UserServiceImpl.java   # 用户服务实现（已更新）
└── ...
```

---

## 下一步优化建议

1. **发送频率限制**: 限制同一邮箱每分钟/每天的发送次数
2. **图形验证码**: 发送短信前增加图形验证码防止刷接口
3. **邮件模板**: 支持自定义邮件模板
4. **异步发送**: 使用 `@Async` 异步发送邮件提升响应速度
5. **发送日志**: 记录邮件发送日志便于追踪问题
