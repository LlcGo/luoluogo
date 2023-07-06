--标签表
create table if not exists lcbackstage.tag
(
	id bigint auto_increment comment '主键递增'
		primary key,
	tageName varchar(256) null comment '标签名',
	userId bigint null comment '创建标签用户的标签',
	parentId int null comment '用于分类的父标签',
	gender tinyint null comment '性别',
	isParent tinyint null comment '是否为父标签',
	updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
	createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
	isDelete tinyint default 0 null comment '0正常 1删除 (逻辑删除)',
	constraint uindex_tageName
		unique (tageName)
)
comment '标签表' charset=utf8;

create index index_userId
	on lcbackstage.tag (userId);


--用户表
create table if not exists lcbackstage.user
(
	id bigint auto_increment comment '主键递增'
		primary key,
	userName varchar(256) null comment '用户名',
	userAccount varchar(256) null comment '账户',
	userPassword varchar(512) null comment '密码',
	avatarUrl varchar(1024) null comment '头像',
	gender tinyint null comment '性别',
	phone varchar(128) null comment ' 电话',
	email varchar(512) null comment '邮箱',
	userStatus int default 0 null comment '用户状态 int 0 正常',
	updateTime datetime null comment '更新时间',
	createTime datetime null comment '创建时间',
	isDelete tinyint default 0 null comment '0正常 1删除 (逻辑删除)',
	userRole int default 0 null comment '0普通用户 1 管理员',
	plantCode varchar(512) null comment '校验登录凭证',
	tags varchar(1024) null comment '标签列表',
	constraint user_userAccount_uindex
		unique (userAccount)
)
comment '用户表' charset=utf8;

--队伍表
create table if not exists lcbackstage.team
(
	teamId bigint not null comment '队伍id '
		primary key,
	userId bigint null comment '队伍主人的id',
	description varchar(255) not null comment '队伍描述',
	teamName varchar(255) not null comment '队伍名称',
	maxNum int default 1 not null comment '队伍最大人数',
	expireTime datetime null comment ' 队伍过期时间',
	teamStatus int default 0 null comment '队伍状态   0 公开 1私人 2加密',
	createTime datetime default CURRENT_TIMESTAMP null comment ' 创建时间',
	updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
	isDelete tinyint default 0 not null comment ' 是否删除'
)
comment '队伍表' charset=utf8;

--队伍关系表
create table if not exists lcbackstage.user_team
(
	tauId bigint not null comment '主键'
		primary key,
	teamId bigint null comment '队伍id',
	userId bigint null comment '用户id',
	jionTime datetime default CURRENT_TIMESTAMP null comment '加入时间',
	createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
	updateTime datetime default CURRENT_TIMESTAMP null comment '修改时间',
	isDelete tinyint default 0 not null comment '是否删除 0 未删除 1 已删除'
)
comment '**队伍关系表**';