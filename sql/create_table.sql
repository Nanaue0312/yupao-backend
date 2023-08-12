-- auto-generated definition
create table user
(
    id            bigint                                     not null comment '用户id'
        primary key,
    username      varchar(30)                                null comment '用户名',
    user_account  varchar(255)                               null comment '用户账户',
    avatar_url    varchar(1024)                              null comment '用户头像',
    gender        tinyint                                    null comment '用户性别：0-女，1-男',
    user_password varchar(255)                               not null comment '密码',
    tele          varchar(30)                                null comment '手机号',
    email         varchar(255)                               null comment '用户邮箱',
    status        tinyint          default 1                 not null comment '状态：0-删除，1-正常，2-封禁',
    create_time   datetime         default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime         default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint          default 1                 not null comment '逻辑删除：0-删除，1-正常',
    user_role     tinyint unsigned default '0'               not null comment '用户角色：0-普通用户，1-管理员',
    tags          varchar(255)                               null comment '用户拥有的标签',
    planet_code   varchar(512)                               null comment '星球id',
    profile       varchar(512)                               null comment '个人简介'
) comment '用户'
    engine = InnoDB;


-- auto-generated definition
create table team
(
    id          bigint auto_increment comment '队伍id'
        primary key,
    name        varchar(30)                             null comment '队伍名称',
    description varchar(1024)                           null comment '队伍描述信息',
    max_count   varchar(1024) default '5'               null comment '最大用户数',
    expire_time datetime                                null comment '过期时间',
    userId      bigint                                  null comment '创建者用户id',
    status      tinyint       default 0                 not null comment '状态：0-公开，1-私密，2-加密',
    password    varchar(255)                            null comment '密码',
    create_time datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint       default 1                 not null comment '逻辑删除：0-删除，1-正常'
)
    comment '队伍';

