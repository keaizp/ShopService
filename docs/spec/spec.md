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
│   ├── CosConfig.java
│   ├── SecurityConfig.java
│   ├── WebMvcConfig.java
│   ├── AuthInterceptor.java
│   └── AdminInterceptor.java
├── controller/          # 控制器层
│   ├── GoodsController.java
│   ├── GoodsSkuController.java
│   ├── CartController.java
│   ├── UserController.java
│   └── UserAddressController.java
├── dto/                 # 数据传输对象
│   ├── GoodsDTO.java
│   ├── CartAddDTO.java
│   └── CartUpdateDTO.java
├── entity/              # 实体层
│   ├── Goods.java
│   ├── GoodsImage.java
│   ├── GoodsParam.java
│   ├── GoodsSpec.java
│   ├── GoodsSku.java
│   ├── Cart.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── User.java
│   ├── UserAddress.java
│   └── Category.java
├── mapper/              # 数据访问层
│   ├── GoodsMapper.java
│   ├── GoodsImageMapper.java
│   ├── GoodsParamMapper.java
│   ├── GoodsSpecMapper.java
│   ├── GoodsSkuMapper.java
│   ├── CartMapper.java
│   ├── OrderMapper.java
│   ├── OrderItemMapper.java
│   ├── UserMapper.java
│   ├── UserAddressMapper.java
│   └── CategoryMapper.java
├── service/             # 业务逻辑层
│   ├── GoodsService.java
│   ├── GoodsSpecService.java
│   ├── GoodsSkuService.java
│   ├── CartService.java
│   └── UserService.java
└── util/                # 工具类
    └── CosUtil.java
```

---

## 2. 数据库设计

### 2.1 商品分类表 (category) - 逻辑删除
```sql
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0为顶级',
    `level` TINYINT NOT NULL DEFAULT 1 COMMENT '层级：1-一级，2-二级，3-三级',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_level` (`level`),
    INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';
```

### 2.2 商品表 (goods) - 逻辑删除
```sql
CREATE TABLE `goods` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `brand_id` BIGINT DEFAULT NULL COMMENT '品牌ID',
    `name` VARCHAR(255) NOT NULL COMMENT '商品名称',
    `subtitle` VARCHAR(500) DEFAULT NULL COMMENT '商品副标题',
    `price` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `main_image` VARCHAR(500) DEFAULT NULL COMMENT '主图片路径',
    `description` TEXT DEFAULT NULL COMMENT '商品描述',
    `detail` TEXT DEFAULT NULL COMMENT '商品详情',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `sales_count` INT NOT NULL DEFAULT 0 COMMENT '销售次数',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_brand_id` (`brand_id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_status` (`status`),
    INDEX `idx_deleted` (`deleted`),
    INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
```

### 2.3 商品图片表 (goods_image) - 逻辑删除
```sql
CREATE TABLE `goods_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片路径',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_goods_id` (`goods_id`),
    INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';
```

### 2.4 商品参数表 (goods_param) - 逻辑删除
```sql
CREATE TABLE `goods_param` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `param_name` VARCHAR(100) NOT NULL COMMENT '参数名称',
    `param_value` VARCHAR(500) NOT NULL COMMENT '参数值',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_goods_id` (`goods_id`),
    INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品参数表';
```

### 2.5 商品规格属性表 (goods_spec) - 逻辑删除
```sql
CREATE TABLE `goods_spec` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `name` VARCHAR(50) NOT NULL COMMENT '规格名称，如颜色、尺寸',
    `values` VARCHAR(500) NOT NULL COMMENT '规格值列表，JSON数组，如["黑色","白色"]',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格属性表';
```

### 2.6 商品SKU表 (goods_sku) - 逻辑删除
```sql
CREATE TABLE `goods_sku` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `spec_values` VARCHAR(500) NOT NULL COMMENT '规格组合JSON，如{"颜色":"黑色","尺寸":"S"}',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'SKU价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT 'SKU库存',
    `image` VARCHAR(255) DEFAULT NULL COMMENT 'SKU独立图片（COS路径，可选）',
    `sku_code` VARCHAR(100) DEFAULT NULL COMMENT 'SKU编码（可选）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_goods_id` (`goods_id`),
    INDEX `idx_sku_code` (`sku_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品SKU表';
```



### 2.7 用户表 (user) - 逻辑删除
```sql
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `openid` VARCHAR(64) NOT NULL COMMENT '微信OpenID',
    `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信UnionID（多应用统一标识）',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_openid` (`openid`),
    INDEX `idx_unionid` (`unionid`),
    INDEX `idx_phone` (`phone`),
    INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 2.8 管理员表 (admin) - 逻辑删除
```sql
CREATE TABLE `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '管理员账号',
    `password` VARCHAR(255) NOT NULL COMMENT '管理员密码',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_username` (`username`),
    INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';
```

### 2.9 用户地址表 (user_address) - 逻辑删除
```sql
CREATE TABLE `user_address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认：0-否，1-是',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';
```

### 2.10 购物车表 (cart) - 物理删除
```sql
CREATE TABLE `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT DEFAULT NULL COMMENT '关联SKU ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    `selected` TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中：0-未选中，1-已选中',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';
```

### 2.11 订单表 (order) - 禁止删除
```sql
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    `pay_amount` DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
    `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款',
    `pay_type` TINYINT DEFAULT NULL COMMENT '支付方式：1-微信支付',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `delivery_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `receive_time` DATETIME DEFAULT NULL COMMENT '收货时间',
    `address_id` BIGINT NOT NULL COMMENT '收货地址ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `receiver_address` VARCHAR(500) NOT NULL COMMENT '完整收货地址',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '订单备注',
    `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
```

### 2.12 订单明细表 (order_item) - 禁止删除
```sql
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT DEFAULT NULL COMMENT '关联SKU ID',
    `spec_info` VARCHAR(255) DEFAULT NULL COMMENT '规格快照，如"黑色 / S"',
    `goods_name` VARCHAR(255) NOT NULL COMMENT '商品名称（冗余）',
    `goods_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片（冗余）',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '商品单价',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `subtotal` DECIMAL(10, 2) NOT NULL COMMENT '小计金额',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';
```

---

## 2.13 删除策略说明

| 表名 | 删除策略 | 说明 |
|------|----------|------|
| **category** | 逻辑删除 | 分类可能被商品引用，保留历史数据 |
| **goods** | 逻辑删除 | 商品被订单引用，物理删除会导致订单数据不完整 |
| **goods_image** | 逻辑删除 | 图片信息与商品关联，保留历史数据 |
| **goods_param** | 逻辑删除 | 参数信息与商品关联，保留历史数据 |
| **goods_spec** | 逻辑删除 | 规格属性与商品关联，保留历史数据 |
| **goods_sku** | 逻辑删除 | SKU被订单引用，保留历史数据 |
| **brand** | 逻辑删除 | 品牌可能被商品引用，保留历史数据 |
| **user** | 逻辑删除 | 用户有历史订单，需保留数据用于售后、统计 |
| **admin** | 逻辑删除 | 管理员账号信息需要保留 |
| **user_address** | 逻辑删除 | 地址可能被历史订单引用 |
| **cart** | 物理删除 | 临时数据，可直接删除 |
| **order** | 禁止删除 | 核心交易数据，只能取消/退款，不可删除 |
| **order_item** | 禁止删除 | 跟随订单，订单不删则明细不删 |

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
| `/api/goods/{id}/images` | GET | 获取商品图片列表 |
| `/api/goods/{id}/params` | GET | 获取商品参数列表 |

**分页查询参数**：
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| pageNum | Long | 否 | 1 | 页码 |
| pageSize | Long | 否 | 10 | 每页条数 |
| categoryId | Long | 否 | - | 分类ID |
| brandId | Long | 否 | - | 品牌ID |
| keyword | String | 否 | - | 关键词 |

### 3.4 商品规格/SKU接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/goods/specs/{goodsId}` | GET | 获取商品规格属性列表 |
| `/api/goods/specs/{goodsId}` | POST | 批量保存商品规格属性（先删后插，需管理员权限） |
| `/api/goods/skus/{goodsId}` | GET | 获取商品SKU列表 |
| `/api/goods/skus/{goodsId}` | POST | 批量保存商品SKU（先删后插，需管理员权限） |

### 3.5 商品图片接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/goods-images` | POST | 新增商品图片 |
| `/api/goods-images/{id}` | PUT | 修改商品图片 |
| `/api/goods-images/{id}` | DELETE | 删除商品图片 |

### 3.6 商品参数接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/goods-params` | POST | 新增商品参数 |
| `/api/goods-params/{id}` | PUT | 修改商品参数 |
| `/api/goods-params/{id}` | DELETE | 删除商品参数 |

### 3.7 品牌接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/brands/page` | GET | 分页查询品牌列表 |
| `/api/brands/list` | GET | 查询所有品牌 |
| `/api/brands/{id}` | GET | 根据ID查询品牌详情 |
| `/api/brands` | POST | 新增品牌 |
| `/api/brands/{id}` | PUT | 修改品牌 |
| `/api/brands/{id}` | DELETE | 删除品牌 |
| `/api/brands/upload` | POST | 上传品牌Logo |

### 3.8 分类接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/categories/page` | GET | 分页查询分类列表 |
| `/api/categories/list` | GET | 查询所有分类 |
| `/api/categories/{id}` | GET | 根据ID查询分类详情 |
| `/api/categories` | POST | 新增分类 |
| `/api/categories/{id}` | PUT | 修改分类 |
| `/api/categories/{id}` | DELETE | 删除分类 |
| `/api/categories/tree` | GET | 获取分类树形结构 |

### 3.9 用户接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/user/login` | POST | 微信登录 |
| `/api/user/info` | GET | 获取用户信息 |
| `/api/user/update` | PUT | 更新用户信息 |

### 3.10 管理员接口

| 接口路径 | 请求方式 | 说明 |
|----------|----------|------|
| `/api/admin/login` | POST | 管理员账号密码登录 |

### 3.11 订单接口

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
- 商品规格属性管理（颜色、尺寸等）
- 商品SKU管理（规格组合 → 独立价格/库存/图片）

### 4.2 用户模块
- 微信小程序授权登录
- 用户信息管理
- 收货地址管理

### 4.3 购物车模块
- 添加商品到购物车（支持SKU选择）
- 修改购物车商品数量
- 删除购物车商品
- 查询购物车列表（含SKU规格信息）

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
- **JWT令牌验证**：除登录和商品列表接口外，所有接口都需要在请求头中携带 `Authorization: Bearer <token>`
- **登录流程**：
  1. 小程序调用 `wx.login()` 获取 code
  2. 后端使用 code 调用微信API获取 openid
  3. 后端生成 JWT token 返回给前端
  4. 前端将 token 存储，后续请求携带 token
- **token有效期**：24小时
- **敏感接口保护**：用户信息、购物车、订单等接口都需要鉴权
- **微信登录接口需验证code有效性**
- **用户敏感操作需验证登录状态**
- **图片上传需限制文件大小（最大10MB）**
- **SQL注入防护（使用MyBatis-Plus参数化查询）**

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
