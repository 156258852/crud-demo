# 架构图（PlantUML / UML 版）

> 查看方式：VSCode 装 `PlantUML` 插件 → 打开 `.puml` 文件 → `Alt+D` 预览
> 或在线渲染：https://www.plantuml.com/plantuml/uml

---

## 一、常规 BFF 架构 - 数据流向

```plantuml
@startuml
title 常规 BFF 架构 - 数据流向

actor Client
participant "Controller" as C
participant "Service" as S
participant "Repository" as R
database "数据库" as DB

Client -> C : HTTP 请求
C -> S : 调用业务方法
S -> R : 数据库操作
R -> DB : SQL
DB --> R : 结果
R --> S : Entity
S --> C : DTO
C --> Client : HTTP 响应
@enduml
```

## 二、常规架构 - 组件依赖图

```plantuml
@startuml
title 常规架构 - 组件依赖

package "常规 BFF 架构" {
    [Controller] --> [Service]
    [Service] --> [Repository]
    [Repository] --> [Database]

    [Controller] ..> [Model]
    [Service] ..> [Model]
    [Repository] ..> [Model]
}

note right of [Service]
  问题：依赖链太长
  改底层会影响上层
end note
@enduml
```

## 三、六边形架构 - 数据流向（时序图）

```plantuml
@startuml
title 六边形架构 - 数据流向

actor Client
participant "adapter/in\nUserController" as AI
participant "port/in\nUserUseCase" as PI
participant "application\nUserService" as APP
participant "port/out\nUserRepository" as PO
participant "adapter/out\nRepositoryImpl" as AO
database "数据库" as DB

Client -> AI : HTTP 请求
AI -> PI : 调用接口方法
PI -> APP : 实现（业务逻辑）
APP -> PO : 调用存储接口
PO -> AO : 实现（具体操作）
AO -> DB : SQL
DB --> AO : 结果
AO --> PO : Domain Object
PO --> APP : Domain Object
APP --> PI : Response
PI --> AI : Response
AI --> Client : HTTP 响应
@enduml
```

## 四、六边形架构 - 完整分层（组件图）

```plantuml
@startuml
title 六边形架构 - 分层组件图

package "adapter" {
    package "adapter/in" {
        [UserController]
    }
    package "adapter/out" {
        [UserRepositoryAdapter]
        [SendGridEmailAdapter]
    }
}

package "port" {
    package "port/in" {
        interface UserUseCase
    }
    package "port/out" {
        interface UserRepository
        interface EmailService
    }
}

package "application" {
    [UserService]
}

package "domain" {
    class User {
        - id: Long
        - username: String
        - password: String
        - role: UserRole
        - active: boolean
        + changePassword(newPwd)
        + activate()
        + deactivate()
        + isAdmin(): boolean
    }
    class Money <<ValueObject>> {
        - amount: BigDecimal
        - currency: String
        + add(other): Money
    }
    class UserRegisteredEvent <<DomainEvent>>
    enum UserRole {
        USER
        ADMIN
        MANAGER
    }
}

[UserController] ..> UserUseCase : 依赖
[UserService] ..|> UserUseCase : 实现
[UserService] ..> UserRepository : 依赖
[UserService] ..> EmailService : 依赖
[UserRepositoryAdapter] ..|> UserRepository : 实现
[SendGridEmailAdapter] ..|> EmailService : 实现
[UserService] ..> User : 使用
@enduml
```

## 五、六边形架构 - 依赖关系（可替换实现）

```plantuml
@startuml
title 六边形架构 - 依赖倒置

interface "port/in\nUserUseCase" as PI
interface "port/out\nUserRepository" as PO

[adapter/in\nController] ..> PI : 依赖
[application\nService] ..|> PI : 实现
[测试 Mock] ..|> PI : 实现

[application\nService] ..> PO : 依赖
[adapter/out\nMySQL Impl] ..|> PO : 实现
[adapter/out\nMongo Impl] ..|> PO : 实现
[测试 Mock2] ..|> PO : 实现

note bottom of PO
  所有层都依赖 port（接口）
  不依赖具体实现
end note
@enduml
```

## 六、两种架构对比

```plantuml
@startuml
title 架构对比

rectangle "常规架构" {
    [Controller] -right-> [Service] : 直接依赖
    [Service] -right-> [Repository] : 直接依赖
}

rectangle "六边形架构" {
    [adapter/in] -right-> [port/in] : 依赖接口
    [port/in] -right-> [application]
    [application] -right-> [port/out] : 依赖接口
    [port/out] -right-> [adapter/out]
}

note bottom of "常规架构"
  改一处 → 影响整条链
end note

note bottom of "六边形架构"
  改实现 → 不影响业务逻辑
end note
@enduml
```

## 七、切换数据库（类图）

```plantuml
@startuml
title 切换数据库 - 只需换 Adapter

interface UserRepository {
    + save(user: User): User
    + findById(id: Long): Optional<User>
    + existsByUsername(name: String): boolean
}

class UserService {
    - userRepository: UserRepository
    + save(user: User)
}

class MySQLUserRepository {
    - jpaRepository: JpaRepository
    + save(user: User): User
    + findById(id: Long): Optional<User>
    + existsByUsername(name: String): boolean
}

class MongoUserRepository {
    - mongoTemplate: MongoTemplate
    + save(user: User): User
    + findById(id: Long): Optional<User>
    + existsByUsername(name: String): boolean
}

UserService --> UserRepository : 依赖接口
MySQLUserRepository ..|> UserRepository : 实现
MongoUserRepository ..|> UserRepository : 实现

note right of UserService
  业务代码完全不用改！
  只需切换 Adapter 实现
end note
@enduml
```

## 八、切换第三方服务（类图）

```plantuml
@startuml
title 切换第三方服务 - 阿里云 → 腾讯云

interface SmsService {
    + send(phone: String, message: String)
}

class AliyunSmsAdapter {
    - aliyunClient: AliyunSmsClient
    + send(phone: String, message: String)
}

class TencentSmsAdapter {
    - tencentClient: TencentSmsClient
    + send(phone: String, message: String)
}

class BusinessService {
    - smsService: SmsService
    + notifyUser(phone: String)
}

BusinessService --> SmsService : 依赖接口
AliyunSmsAdapter ..|> SmsService : 旧实现
TencentSmsAdapter ..|> SmsService : 新实现

note bottom of SmsService
  接口不变
  换实现只改 Adapter
  业务代码零修改
end note
@enduml
```

## 九、Domain 层类图

```plantuml
@startuml
title Domain 层 - 核心实体（无框架依赖）

class User <<Entity>> {
    - id: Long
    - username: String
    - password: String
    - role: UserRole
    - active: boolean
    + changePassword(newPwd: String)
    + activate()
    + deactivate()
    + isAdmin(): boolean
}

enum UserRole {
    USER
    ADMIN
    MANAGER
}

class Money <<ValueObject>> {
    - amount: BigDecimal
    - currency: String
    + add(other: Money): Money
}

class Email <<ValueObject>> {
    - value: String
}

class UserRegisteredEvent <<DomainEvent>> {
    - userId: Long
    - username: String
    - timestamp: LocalDateTime
}

class UserNotFoundException <<DomainException>>

User --> UserRole
User ..> UserRegisteredEvent : 触发
User ..> Email : 包含
@enduml
```
