# 电商微信小程序后端服务 - 项目规格说明书

## 1. 项目概述

### 1.1 项目名称
Yamaha Shop Backend - 电商微信小程序后端服务

### 1.2 项目目标
为微信小程序提供完整的后端API服务，支持商品管理、用户管理、订单管理等电商核心功能。

### 1.3 技术栈
| 类别 | 技术选型 | 版本 |
|------|----------|------|
| 核心框架 | Spring Boot | 3.2.0 |
| ORM框架 | MyBatis-Plus | 3.5.7 |
| 数据库 | MySQL | 8.0+ |
| 对象关系映射 | Lombok | - |
| 文件存储 | 腾讯云COS | 5.6.169 |
| API验证 | Spring Validation | - |
| 构建工具 | Maven | 3.x |
| Java版本 | JDK | 17+ |

### 1.4 项目结构
```
com.yamaha/
├── common/              # 通用模块
│   ├── Result.java      # 统一响应封装
│   ├── PageDTO.java     # 分页查询参数
│   └── PageResult.java  # 分页响应封装
├── config/              # 配置模块
│   ├── MyBatisPlusConfig.java
│   └── CosConfig.java
├── controller/          # 控制器层
│   ├── GoodsController.java
│   └── UserController.java
├── entity/              # 实体层
│   ├── Goods.java
│   └── User.java
├── mapper/              # 数据访问层
│   ├── GoodsMapper.java
│   └── UserMapper.java
├── service/             # 业务逻辑层
│   ├── GoodsService.java
│   └── UserService.java
└── util/                # 工具类
    └── CosUtil.java
```

---

## 2. 数据库设计

### 2.1 商品表 (goods)
```sql
CREATE TABLE `goods` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(255) NOT NULL COMMENT '商品名称',
    `price` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片路径',
    `description` TEXT DEFAULT NULL COMMENT '商品描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
```

### 2.2 用户表 (user)
```sql
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `openid` VARCHAR(64) NOT NULL COMMENT '微信OpenID',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 2.3 订单表 (order)
```sql
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已完成，3-已取消',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
    `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '订单备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
```

### 2.4 订单明细表 (order_item)
```sql
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `goods_name` VARCHAR(255) NOT NULL COMMENT '商品名称（冗余）',
    `goods_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片（冗余）',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '商品单价',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `subtotal` DECIMAL(10, 2) NOT NULL COMMENT '小计金额',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';
```

---

## 3. API接口规范

### 3.1 统一响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {}
}
```

**响应码说明**：
| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 3.2 分页响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [],
        "total": 100,
        "size": 10,
        "current": 1,
        "pages": 10
    }
}
```

### 3.3 商品接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/goods/page` | GET | 分页查询商品列表 |
| `/api/goods/list` | GET | 查询所有上架商品 |
| `/api/goods/{id}` | GET | 根据ID查询商品详情 |
| `/api/goods` | POST | 新增商品 |
| `/api/goods/{id}` | PUT | 修改商品 |
| `/api/goods/{id}` | DELETE | 删除商品 |
| `/api/goods/upload` | POST | 上传商品图片 |

**分页查询参数**：
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| pageNum | Long | 否 | 1 | 页码 |
| pageSize | Long | 否 | 10 | 每页条数 |

### 3.4 用户接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/user/login` | POST | 微信登录 |
| `/api/user/info` | GET | 获取用户信息 |
| `/api/user/update` | PUT | 更新用户信息 |

### 3.5 订单接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/order/create` | POST | 创建订单 |
| `/api/order/list` | GET | 查询用户订单列表 |
| `/api/order/{id}` | GET | 查询订单详情 |
| `/api/order/{id}/cancel` | PUT | 取消订单 |
| `/api/order/{id}/pay` | PUT | 模拟支付 |

---

## 4. 功能模块规划

### 4.1 商品模块
- 商品列表展示（分页、搜索）
- 商品详情查看
- 商品管理（增删改查）
- 商品图片上传

### 4.2 用户模块
- 微信小程序授权登录
- 用户信息管理
- 收货地址管理

### 4.3 购物车模块
- 添加商品到购物车
- 修改购物车商品数量
- 删除购物车商品
- 查询购物车列表

### 4.4 订单模块
- 创建订单
- 订单支付
- 订单列表查询
- 订单详情查看
- 取消订单

---

## 5. 配置管理

### 5.1 application.yml 配置项
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yamaha_shop
    username: root
    password: root

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

cos:
  secret-id: ${COS_SECRET_ID}
  secret-key: ${COS_SECRET_KEY}
  bucket: motorcycle-shop-1306776476
  region: ap-shanghai
  base-url: https://motorcycle-shop-1306776476.cos.ap-shanghai.myqcloud.com/

server:
  port: 8080
  servlet:
    context-path: /api
```

### 5.2 环境变量
| 变量名 | 说明 |
|--------|------|
| COS_SECRET_ID | 腾讯云SecretId |
| COS_SECRET_KEY | 腾讯云SecretKey |

---

## 6. 安全规范

### 6.1 接口安全
- 微信登录接口需验证code有效性
- 用户敏感操作需验证登录状态
- 图片上传需限制文件大小（最大10MB）
- SQL注入防护（使用MyBatis-Plus参数化查询）

### 6.2 数据安全
- 用户密码加密存储（微信场景使用openid）
- 敏感信息脱敏处理
- 数据库定期备份

---

## 7. 性能要求

### 7.1 接口响应时间
- 普通接口：< 200ms
- 分页查询：< 500ms
- 文件上传：< 3s

### 7.2 并发能力
- 支持100+并发请求
- 数据库连接池优化

---

## 8. 扩展规划

### 8.1 短期规划
- [ ] 用户模块完善
- [ ] 购物车功能
- [ ] 订单功能
- [ ] 基础API文档

### 8.2 长期规划
- [ ] 优惠券功能
- [ ] 会员积分系统
- [ ] 物流跟踪
- [ ] 消息推送
- [ ] 数据统计分析
