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

###
