create table lb_account
(
    user_id     bigint not null comment '用户id'
        primary key,
    user_money  bigint default 0 null comment '用户金币数',
    update_time datetime null on update CURRENT_TIMESTAMP comment '更新金币的时间'
) engine = InnoDB;

create table lb_article
(
    article_id          bigint                              not null comment '文章id'
        primary key,
    is_article          bigint default 1                    not null,
    user_id             bigint                              not null comment '发表用户id',
    article_title       text charset utf8mb4 not null comment '文章标题',
    article_content     longtext collate utf8mb4_general_ci not null comment '文章内容',
    article_update_date datetime                            not null comment '发表时间',
    article_pic         varchar(2000) null comment '文章主背景图',
    article_views       bigint default 0                    not null comment '浏览量',
    article_comments    bigint default 0                    not null comment '评论总数',
    article_likes       bigint default 0                    not null comment '点赞数',
    article_collects    bigint default 0                    not null comment '收藏数'
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create table lb_badge
(
    badge_id          bigint auto_increment comment '徽章id'
        primary key,
    badge_name        varchar(255) not null comment '徽章名字',
    badge_description varchar(1000) default '' null comment '徽章描述',
    badge_stock       int           default 0 null comment '徽章剩余库存',
    badge_type        int           default 0 null comment '0:普通徽章 1：限量徽章',
    badge_value       int           default 0 null comment '徽章的价值',
    badge_icon        varchar(1000) default '' null comment '徽章图片链接'
) engine = InnoDB;

create table lb_black
(
    id          bigint unsigned auto_increment comment 'id'
        primary key,
    type        int         not null comment '拉黑目标类型 1.ip 2uid',
    target      varchar(32) not null comment '拉黑目标',
    create_time datetime(3) default CURRENT_TIMESTAMP (3) not null comment '创建时间',
    update_time datetime(3) default CURRENT_TIMESTAMP (3) not null on update CURRENT_TIMESTAMP (3) comment '修改时间',
    constraint idx_type_target
        unique (type, target)
) comment '黑名单' engine = InnoDB
                     collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create table lb_chat_connection
(
    id             int auto_increment comment 'id'
        primary key,
    user_id        bigint   not null comment '用户id',
    chat_user_id   bigint   not null comment '聊天对象id',
    chat_last_time datetime not null comment '最后一次聊天时间'
) comment '聊天对象列表' engine = InnoDB
                           collate = utf8mb4_bin
                           row_format = DYNAMIC;

create table lb_chat_record
(
    record_id          bigint auto_increment
        primary key,
    user_id            bigint                         not null comment '发送者id',
    receiver_id        bigint                         not null comment '接收者id',
    record_content     mediumtext collate utf8mb4_bin not null comment '消息内容',
    record_update_time datetime                       not null on update CURRENT_TIMESTAMP comment '消息发送时间',
    is_saw             tinyint default 0 null,
    constraint id
        unique (record_id)
) engine = InnoDB
    row_format = DYNAMIC;

create index user_id
    on lb_chat_record (user_id);

create index user_record
    on lb_chat_record (user_id, receiver_id);

create table lb_comment
(
    comment_id          bigint auto_increment comment '评论id'
        primary key,
    user_id             bigint           not null comment '评论者id',
    article_id          bigint           not null comment '评论文章id',
    receiver_id         bigint           not null,
    comment_content     varchar(1000) charset utf8mb4 not null comment '内容',
    comment_update_time datetime         not null on update CURRENT_TIMESTAMP comment '评论日期',
    comment_parent_id   bigint           not null comment '被回复的评论的id',
    comment_likes       bigint default 0 not null comment '点赞数',
    constraint comment_commentId_uindex
        unique (comment_id)
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create table lb_friend
(
    id             bigint auto_increment comment '标识ID'
        primary key,
    user_id        bigint not null comment '用户ID',
    user_friend_id bigint not null comment '好友ID',
    user_note      varchar(20) charset utf8mb4 not null comment '好友备注'
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create index user_id
    on lb_friend (user_id);

create table lb_label
(
    label_id          bigint auto_increment comment '标签ID'
        primary key,
    label_name        varchar(20) not null comment '标签名称',
    label_alias       varchar(15) null comment '标签别名',
    label_description text null comment '标签描述'
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create index label_alias
    on lb_label (label_alias);

create index label_name
    on lb_label (label_name);

create table lb_message
(
    message_id          bigint                   not null
        primary key,
    user_id             bigint                   not null,
    receiver_id         bigint                   not null,
    message_title       varchar(255)             not null,
    message_content     varchar(2000) default '' not null,
    message_update_time datetime null on update CURRENT_TIMESTAMP,
    is_saw              tinyint       default 0 null,
    message_type        int           default 6  not null comment ':0-发表文章 1-评论文章 2-收藏文章 3-点赞文章
//     * 4-关注用户 5-回复评论 6-系统消息',
    message_redirect    varchar(2000) null
) engine = InnoDB;

create table lb_order
(
    order_id          bigint   not null comment '订单id'
        primary key,
    user_id           bigint   not null comment '用户id',
    badage_id         bigint null comment '徽章id',
    order_create_time datetime not null comment '订单生成时间'
) engine = InnoDB;

create table lb_permission
(
    permission_id int          not null comment '权限 ID'
        primary key,
    resource_name varchar(50)  not null comment '资源名称',
    action_name   varchar(50)  not null comment '操作名称',
    action_key    varchar(100) not null comment '操作标识符'
) comment '权限表' engine = InnoDB;

create table lb_role
(
    role_id   int         not null comment '角色 ID'
        primary key,
    role_name varchar(50) not null comment '角色名称'
) comment '角色表' engine = InnoDB;

create table lb_role_permission
(
    role_id       int not null comment '角色 ID',
    permission_id int not null comment '权限 ID',
    primary key (role_id, permission_id),
    constraint lb_role_permission_ibfk_1
        foreign key (role_id) references lb_role (role_id),
    constraint lb_role_permission_ibfk_2
        foreign key (permission_id) references lb_permission (permission_id)
) comment '角色权限关联表' engine = InnoDB;

create index permission_id
    on lb_role_permission (permission_id);

create table lb_set_article_label
(
    article_id bigint not null comment '文章id',
    label_id   bigint not null comment '对应的标签id'
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create index label_id
    on lb_set_article_label (label_id);

create table lb_set_article_sort
(
    article_id bigint not null comment '文章ID',
    sort_id    bigint not null comment '分类ID'
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create index sort_id
    on lb_set_article_sort (sort_id);

create table lb_set_artitle_label
(
    article_id bigint auto_increment comment '文章id'
        primary key,
    label_id   bigint not null comment '对应的标签id'
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create index label_id
    on lb_set_artitle_label (label_id);

create table lb_set_artitle_sort
(
    article_id bigint not null comment '文章ID',
    sort_id    bigint not null comment '分类ID',
    primary key (article_id, sort_id)
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create index sort_id
    on lb_set_artitle_sort (sort_id);

create table lb_set_user_badge
(
    user_id  bigint not null,
    badge_id bigint null
) comment '用户徽章对应表' engine = InnoDB;

create table lb_sort
(
    sort_id          bigint      not null comment '分类ID'
        primary key,
    sort_name        varchar(50) not null comment '分类名称',
    sort_alias       varchar(15) not null comment '分类别名',
    sort_description text        not null comment '分类描述',
    parent_sort_id   bigint      not null comment '父分类ID'
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create index sort_alias
    on lb_sort (sort_alias);

create index sort_name
    on lb_sort (sort_name);

create table lb_user
(
    user_id            bigint      not null comment '用户id'
        primary key,
    user_name          varchar(20) charset utf8mb4 not null comment '用户名',
    user_email         varchar(30)                             default '' null comment '用户email',
    user_phone         varchar(20)                             default '' null comment '用户手机号',
    user_password      varchar(50) not null comment '用户密码',
    user_nickname      varchar(20) charset utf8mb4 default '' null comment '用户昵称',
    user_profile_photo varchar(2000)                           default '/source/images/default_pic.png' null comment '用户头像',
    user_intro         varchar(255)                            default '沉默是金' null comment '用户个人介绍',
    user_sex           tinyint(1) null comment '用户性别。0：女，1：男。',
    user_pos           varchar(255) collate utf8mb4_general_ci default '保密' null comment '用户所在地',
    user_birthday      date                                    default '2000-01-01' null comment '用户生日',
    user_certification varchar(255) collate utf8mb4_general_ci default '未知' null comment '用户认证',
    user_education     varchar(255) collate utf8mb4_general_ci default '保密' null comment '用户教育情况',
    user_industry      varchar(255) collate utf8mb4_general_ci default '保密' null comment '用户行业',
    user_register_date datetime    not null comment '注册日期',
    user_bg_pic        varchar(2000) collate utf8_bin          default '/source/images/index/5.jpg' null comment '背景图',
    constraint userinfo_userId_uindex
        unique (user_id)
) engine = InnoDB
    charset = utf8
    row_format = DYNAMIC;

create table lb_user_role
(
    user_id bigint not null comment '用户 ID',
    role_id int    not null comment '角色 ID',
    primary key (user_id, role_id),
    constraint lb_user_role_ibfk_1
        foreign key (user_id) references lb_user (user_id),
    constraint lb_user_role_ibfk_2
        foreign key (role_id) references lb_role (role_id)
) comment '用户角色关联表' engine = InnoDB;

create index role_id
    on lb_user_role (role_id);

