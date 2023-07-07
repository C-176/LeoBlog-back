# LeoBlog-Back

![](https://img.shields.io/badge/LICENSE-Apache2.0-green.svg)
[![](https://img.shields.io/badge/BLOG-@LeoBlog-red.svg)](http://www.leoblog.icu)

## 介绍

LeoBlog是一个仿知乎的博客系统，旨在提供一个简单易用的博客平台，让用户可以方便地发布、编辑和管理自己的博客。该项目同时集成了在线聊天功能和鱼聪明AI智能助手，能够为用户提供更加全面的博客使用体验。该项目使用Java编写，基于Spring
Boot框架开发，前端使用Vue.js编写。

## 功能列表

* 用户注册和登录：用户可以通过注册和登录功能进行身份验证和授权，以便于进行博客发布和管理等操作。
* 发布、编辑和删除文章：用户可以通过LeoBlog平台进行文章的发布、编辑和删除等操作，便于管理自己的博客。
* 查看文章列表和单篇文章：LeoBlog平台提供了文章列表和单篇文章查看功能，方便用户进行浏览和阅读。
* 在线聊天和私信功能：LeoBlog平台集成了在线聊天和私信功能，用户可以通过平台与其他用户进行交流和互动。
* 集成鱼聪明AI智能助手，实现自主问答功能：LeoBlog平台集成了鱼聪明AI智能助手，能够为用户提供自主问答功能，方便用户进行一些常见问题的解答。

## 安装和使用

该项目需要在本地环境中运行，以下是安装和使用该项目的步骤：

### 克隆或下载该仓库

`git clone https://github.com/C-176/LeoBlog-Back.git`

### 进入项目根目录并修改配置文件

在项目根目录下有一个 `application.yaml / application-dev.yaml`文件，根据您自己的环境配置数据库和Redis信息。

## 构建项目

使用Maven构建项目：

`mvn clean package`

## 运行项目

使用以下命令启动项目：

`java -jar target/leoblog-0.0.1-SNAPSHOT.jar`

后端项目即在本地 **8080** 端口（默认）运行起来了，接下来您可以使用前端项目进行交互。

## 贡献

如果您发现了任何问题或想要改进该项目，请随时提出问题或发送拉取请求。我们非常欢迎您的贡献！

## 许可证

该项目采用 Apache.2.0 许可证 进行许可。详情请参阅 LICENSE 文件。

## 鸣谢

Spring Boot：快速构建应用程序的框架。
MyBatis：优秀的ORM框架，用于将Java对象映射到SQL语句。
Redis：开源的内存数据结构存储，用于缓存和消息队列。
Swagger：API文档化工具。
鱼聪明AI：程序员鱼皮所在公司推出的智能AI助手，为用户提供自主问答功能。

## 开发过程中碰到的问题

### 登录校验的实现方式选择，JWT or Redis？

* JWT的使用方式是 将用户id加密之后作为token发送给客户端，并要求客户端每次访问都携带token，然后服务端就可以通过查看token来解码获取用户id，从而进行验证。
* 使用redis的话，是直接生成一个随机token，然后将该token作为key，将userid作为value存放到redis中，并将该token发送给客户端，然后服务端就可以通过查看token来去redis中获取用户id，从而进行验证。
  下面是这两种方式的优缺点：

JWT 方式的优点：

* 无需在服务端存储 token，减少了服务器端存储的开销。
* 可以支持跨域访问，适用于前后端分离的应用场景。
* 可以通过 token 的签名来防止 token 被篡改或伪造。

JWT 方式的缺点：

* 一旦签发，无法在过期前撤销，用户无法强制退出。
* token 中可能会包含敏感信息，需要适当保护。
* 如果 token 过大，会占用网络带宽和存储空间。
* 需要适当保护 token，例如使用 HTTPS 协议传输，并采用适当的加密算法和签名算法等。

Redis 存放 token 的方式优点：

* 可以随时撤销 token，用户可强制退出。
* 适用于需要频繁更新 token 的场景，例如用户密码修改。
* 可以通过 Redis 的过期机制来自动清除过期的 token。

Redis 存放 token 的方式缺点：

* 需要在服务器端存储 token，增加了服务器端存储的开销。
* 需要建立一套存储和管理机制，增加了系统的复杂性和维护成本。
* Redis 中的 token 存储在内存中，如果未经适当保护，可能会被恶意攻击者攻击窃取。
* Redis 是一个内存数据库，如果 Redis 宕机或发生故障，可能导致 token 无法验证或失效。

#### JWT为什么不支持强制退出？redis需要建立的存储和管理机制指的是key的命名和value的内容，以及如何进行校验吗？

JWT 无法支持强制退出，是因为 JWT 的签发方和验证方是分离的，签发方签发了一个有效期内有效的 JWT，验证方只要验证 JWT
的签名是合法的，就会认为该 JWT 是有效的。JWT 的签发方无法撤销已经签发的 JWT，只能通过设置较短的有效期来缓解该问题。如果需要支持强制退出，可以考虑使用基于
Redis 的方案，允许通过删除 Redis 中的 token 来实现强制退出。

Redis 存储和管理机制主要指的是在 Redis 中建立一套存储和管理方案，包括 token 的生成、存储、验证和删除等，以及如何进行校验。例如，在
Redis 中存储 token 时，可以使用用户 ID 作为 key，token 作为 value，然后设置 token 的过期时间。在验证 token 时，需要根据 token
查询对应的用户 ID，然后进行验证。需要注意的是，在存储和管理 token 时，需要考虑安全性、性能、容灾等方面的问题，以及如何防止
token 被恶意攻击者盗用或伪造。

综上，由于redis在服务端有额外的内存开销，且要考虑数据一致性，并且考虑到登录操作一般只需要登录一次，所以最终选择了JWT的方式。

### 在使用了webSocket的springBoot项目中进行单元测试时，需要指定SpringBootTest注解的webEnvironment属性为WebEnvironment.RANDOM_PORT，否则会报错

在 Spring Boot 项目中使用 WebSocket，需要使用一个 WebSocket 服务器来处理客户端的连接请求。
在 Spring Boot 中，如果你需要在单元测试中使用 IOC 容器，通常需要使用 @SpringBootTest 注解来创建 IOC 容器，并将测试类作为
Spring Boot 应用程序的一部分来运行。这意味着，与应用程序相关的所有组件，包括 WebSocket 相关的组件，都会被 IOC 容器管理。

因此，即使你的测试代码中不涉及 WebSocket，但如果项目代码中有 WebSocket 相关的组件，并且这些组件被注入到 IOC
容器中，那么在单元测试中也需要将 webEnvironment 属性设置为 RANDOM_PORT 或 DEFINED_PORT，以便在测试中启动一个真实的 Web
服务器，并确保 IOC 容器能够正确地管理 WebSocket 相关的组件。

如果你将 webEnvironment 属性设置为 MOCK，那么在测试中启动的是一个模拟的 Web 环境，这个环境可能无法正确地管理 WebSocket
相关的组件，导致测试失败。因此，建议在单元测试中使用真实的 Web 服务器，并将 webEnvironment 属性设置为 RANDOM_PORT 或
DEFINED_PORT。

需要注意的是，在使用 RANDOM_PORT 或 DEFINED_PORT
运行单元测试时，需要确保测试环境中没有其他进程使用了相同的端口，否则可能会导致端口被占用，从而导致测试失败。可以通过配置文件或命令行参数来指定端口号，或者使用随机端口来避免这个问题。

### 拦截器放行的规则问题
由于接口的前缀相同，导致不同的接口需要不同的放行匹配规则，例如`/user/{userId}`和`/user/fans`，需要分别放行，否则会导致查看用户信息的接口也被拦截，此时如果 `/user/**`就会导致两个接口都被放行或者都被拦截，所以需要使用Ant表达式分别配置。
由于在SpringSecurity中也需要进行相同的放行，所以为了避免重复配置，直接使用自定义注解来进行放行，然后在拦截器中进行判断，如果有该注解，则放行，否则进行拦截。
* 在拦截器中进行注解判断
* 在SpringSecurity 的 configure 方法中进行注解判断
